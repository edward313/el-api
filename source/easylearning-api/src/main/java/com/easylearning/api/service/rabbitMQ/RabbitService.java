package com.easylearning.api.service.rabbitMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.form.BaseMsgForm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RabbitService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitSender rabbitSender;

    @Value("${rabbitmq.backend.app}")
    private String backendApp;

    public  <T> void handleSendMsg(T data, String cmd, String queueName) {
        BaseMsgForm<T> form = new BaseMsgForm<>();
        form.setApp(backendApp);
        form.setCmd(cmd);
        form.setData(data);

        String msg;
        try {
            msg = objectMapper.writeValueAsString(form);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // create queue if existed
        rabbitSender.createQueueIfNotExist(queueName);

        log.error(msg);
        // push msg
        rabbitSender.send(msg, queueName);
    }
}
