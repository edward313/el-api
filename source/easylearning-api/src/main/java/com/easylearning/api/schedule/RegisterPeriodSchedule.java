package com.easylearning.api.schedule;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.controller.ABasicController;
import com.easylearning.api.model.RegisterPayout;
import com.easylearning.api.model.RegisterPeriod;
import com.easylearning.api.repository.RegisterPayoutRepository;
import com.easylearning.api.repository.RegisterPeriodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.easylearning.api.utils.DateUtils.formatDate;

@Component
@Slf4j
public class RegisterPeriodSchedule extends ABasicController {
    @Autowired
    private RegisterPayoutRepository registerPayoutRepository;

    @Autowired
    private RegisterPeriodRepository registerPeriodRepository;

    @Scheduled(cron = "0 0 0 1,16 * *", zone = "UTC")
    @Transactional
    public void registerPeriodSchedule(){
        log.error("Starting register period");
        try {
            Date currentDate = new Date();
            Date startDate = getDateRange(currentDate).get(0);
            Date endDate = getDateRange(currentDate).get(1);

            RegisterPeriod registerPeriod = new RegisterPeriod();
            registerPeriod.setName(formatDate(new Date(),LifeUniConstant.SCHEDULE_DATE_TIME_FORMAT));
            registerPeriod.setStartDate(startDate);
            registerPeriod.setEndDate(endDate);
            registerPeriodRepository.save(registerPeriod);

            double registerPeriodTotalPayout = 0;
            double registerPeriodTotalTaxMoney = 0;
            Map<Long, RegisterPayout> registerPayoutMap = new HashMap<>();

            List<RegisterPayout> registerPayouts = registerPayoutRepository.findAllByBetweenDateAndKind(startDate, endDate, LifeUniConstant.REGISTER_PAYOUT_STATE_PENDING,LifeUniConstant.REGISTER_PAYOUT_KIND_INDIVIDUAL);
            for (RegisterPayout registerPayout : registerPayouts) {
                registerPeriodTotalPayout += registerPayout.getMoney();
                registerPeriodTotalTaxMoney += registerPayout.getTaxMoney();
                Long accountId = registerPayout.getAccount().getId();
                registerPayout.setRegisterPeriod(registerPeriod);
                RegisterPayout sumRegisterPayout;
                if (registerPayoutMap.containsKey(accountId)) {
                    // Nếu đã tồn tại RegisterPayoutSum cho accountId này, cộng dồn tiền vào RegisterPayoutSum đó
                    sumRegisterPayout = registerPayoutMap.get(accountId);
                    sumRegisterPayout.setMoney(sumRegisterPayout.getMoney() + registerPayout.getMoney());
                    sumRegisterPayout.setTaxMoney(sumRegisterPayout.getTaxMoney() + registerPayout.getTaxMoney());
                } else {
                    // Nếu chưa tồn tại, tạo mới RegisterPayoutSum và đưa vào Map
                    sumRegisterPayout = new RegisterPayout();
                    sumRegisterPayout.setRegisterPeriod(registerPeriod);
                    sumRegisterPayout.setAccountKind(registerPayout.getAccount().getKind());
                    sumRegisterPayout.setAccount(registerPayout.getAccount());
                    sumRegisterPayout.setKind(LifeUniConstant.REGISTER_PAYOUT_KIND_SUM);


                    sumRegisterPayout.setState(LifeUniConstant.REGISTER_PAYOUT_STATE_CALCULATED);

                    sumRegisterPayout.setMoney(registerPayout.getMoney());
                    sumRegisterPayout.setTaxMoney(registerPayout.getTaxMoney());
                    sumRegisterPayout.setBankInfo(registerPayout.getBankInfo());
                    sumRegisterPayout.setTaxPercent(registerPayout.getTaxPercent());

                    registerPayoutMap.put(accountId, sumRegisterPayout);
                }
                registerPayout.setState(LifeUniConstant.REGISTER_PAYOUT_STATE_CALCULATED);
            }

            List<RegisterPayout> registerPayoutSums = new ArrayList<>(registerPayoutMap.values());
            registerPayoutRepository.saveAll(registerPayoutSums);
            registerPayoutRepository.saveAll(registerPayouts);

            registerPeriod.setState(LifeUniConstant.REGISTER_PERIOD_STATE_PENDING);
            registerPeriod.setTotalPayout(registerPeriodTotalPayout);
            registerPeriod.setTotalTaxMoney(registerPeriodTotalTaxMoney);

            registerPeriodRepository.save(registerPeriod);
        }catch (Exception e){
            log.error("registerPeriodError error: {}", e.getMessage());
        }
    }

}
