package com.easylearning.api.service.payos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class PayosService {
    @Value("${feign.payos.client.id}")
    private String clientId;

    @Value("${feign.payos.api.key}")
    private String apiKey;

    @Autowired
    private FeignPayosService feignPayosService;

    @Value("${feign.payos.checksum.key}")
    private String checksumKey;

    @Autowired
    private PayosService payosService;
    @Value("${payos.cancel.url}")
    private String payosCancelUrl;
    @Value("${payos.return.url}")
    private String payosReturnUrl;
    @Autowired
    private ObjectMapper objectMapper;
    Long setExpiredTime(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newDateTime = now.plusMinutes(5);
        return newDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public PayosPaymentDto createPayment(CreatePayosPaymentForm createPayosPaymentForm){
        return feignPayosService.createPayment(clientId,apiKey, createPayosPaymentForm);
    }
    public PayosPaymentDto cancelPayment(Long transactionId, String reason){
        CancelPayosPayment cancelPayosPayment = new CancelPayosPayment();
        cancelPayosPayment.setCancellationReason(reason);
        return feignPayosService.cancelPayment(clientId,apiKey, transactionId.toString(), cancelPayosPayment);
    }
    public String createSignature(Object object){
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Convert the object to JsonNode
            JsonNode jsonNode = mapper.valueToTree(object);
            // Remove the "signature" field if it exists
            if (jsonNode.has("signature")) {
                ((ObjectNode) jsonNode).remove("signature");
            }
            // Convert the JsonNode back to JSON string with single quotes
            String jsonString = mapper.writeValueAsString(jsonNode);

            String transaction =  jsonString.replace("\"", "'");

            JSONObject jsonObject = new JSONObject(transaction);
            Iterator<String> sortedIt = sortedIterator(jsonObject.keys(), String::compareTo);

            StringBuilder transactionStr = new StringBuilder();
            while (sortedIt.hasNext()) {
                String key = sortedIt.next();
                System.out.println(key);
                String valueObj = jsonObject.get(key).toString();
                if (valueObj != null && !Objects.equals(valueObj, "null") && !valueObj.isEmpty()) {
                    transactionStr.append(key);
                    transactionStr.append('=');
                    transactionStr.append(valueObj);
                    if (sortedIt.hasNext()) {
                        transactionStr.append('&');
                        System.out.print(key+ " ");
                        System.out.println(valueObj);
                    }
                }
            }
            System.out.println(transactionStr);
            return new HmacUtils("HmacSHA256", checksumKey).hmacHex(transactionStr.toString());
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Iterator<String> sortedIterator(Iterator<?> it, Comparator<String> comparator) {
        List<String> list = new ArrayList<>();
        while (it.hasNext()) {
            list.add((String) it.next());
        }
        Collections.sort(list, comparator);
        return list.iterator();
    }

    public PayosPaymentDto getPayosPaymentDto(Long amount, Long orderId, String description) {
        CreatePayosPaymentForm createPayosPaymentForm = new CreatePayosPaymentForm();
        createPayosPaymentForm.setAmount(amount.intValue());
        createPayosPaymentForm.setOrderCode(orderId);
        createPayosPaymentForm.setDescription(description);
        createPayosPaymentForm.setReturnUrl(payosReturnUrl);
        createPayosPaymentForm.setCancelUrl(payosCancelUrl);
        createPayosPaymentForm.setSignature(payosService.createSignature(createPayosPaymentForm));

        createPayosPaymentForm.setExpiredAt(setExpiredTime());
        createPayosPaymentForm.setBuyerPhone("");
        createPayosPaymentForm.setBuyerEmail("");
        createPayosPaymentForm.setBuyerAddress("");

        List<PayosItemsForm> payosItemsForms = new ArrayList<>();
        createPayosPaymentForm.setItems(payosItemsForms);
        try {
            return payosService.createPayment(createPayosPaymentForm);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
