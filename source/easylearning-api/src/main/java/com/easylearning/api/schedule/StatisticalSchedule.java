package com.easylearning.api.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.statistical.FeStatisticDto;
import com.easylearning.api.model.Statistical;
import com.easylearning.api.repository.StatisticalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StatisticalSchedule {
    @Autowired
    private StatisticalRepository statisticalRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void setFeStatistic() {
        try {
            log.error("Update fe statistic...........");
            Statistical statistical = statisticalRepository.findFirstByStatisticalKey(LifeUniConstant.STATISTICAL_FE_STATISTIC_KEY);
            if (statistical == null) {
                statistical = new Statistical();
                statistical.setStatisticalKey(LifeUniConstant.STATISTICAL_FE_STATISTIC_KEY);
            }
            FeStatisticDto feStatisticDto = statisticalRepository.getFeStatistic(LifeUniConstant.STATUS_ACTIVE, LifeUniConstant.COURSE_KIND_SINGLE);
            statistical.setStatisticalValue(objectMapper.writeValueAsString(feStatisticDto));
            statisticalRepository.save(statistical);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
