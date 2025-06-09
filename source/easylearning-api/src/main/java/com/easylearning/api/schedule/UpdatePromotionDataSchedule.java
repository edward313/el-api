package com.easylearning.api.schedule;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.repository.PromotionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdatePromotionDataSchedule {
    @Autowired
    private PromotionRepository promotionRepository;

    @Scheduled(cron = "0 0 6/6 * * *", zone = "UTC")
    public void setRunningOrEndStateByValidTime(){
        try {
            promotionRepository.setEndPromotionWhenExpiryTime(LifeUniConstant.PROMOTION_STATE_RUNNING,LifeUniConstant.PROMOTION_STATE_END);
            promotionRepository.setRunningPromotionWhenValidTime(LifeUniConstant.PROMOTION_STATE_RUNNING,LifeUniConstant.PROMOTION_STATE_CREATED);
            log.error("Set state promotion success");
        }catch (Exception e){
            log.error("Set state promotion fail" + e.getMessage());
        }
    }
}
