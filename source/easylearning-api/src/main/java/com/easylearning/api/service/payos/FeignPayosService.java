package com.easylearning.api.service.payos;


import com.easylearning.api.config.CustomFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "winwin-payos", url = "${feign.payos.url}", configuration = CustomFeignConfig.class)
public interface FeignPayosService {
    @PostMapping(value = "/v2/payment-requests")
    PayosPaymentDto createPayment(@RequestHeader("x-client-id") String clientId, @RequestHeader("x-api-key") String apiKey,
                                  @RequestBody CreatePayosPaymentForm createPayosPaymentForm);

    @PostMapping(value = "v2/payment-requests/{id}/cancel")
    PayosPaymentDto cancelPayment(@RequestHeader("x-client-id") String clientId, @RequestHeader("x-api-key") String apiKey,
                                  @PathVariable("id") String id, @RequestBody CancelPayosPayment cancelPayosPayment);
}
