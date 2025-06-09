package com.easylearning.api.schedule;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.controller.ABasicController;
import com.easylearning.api.model.Account;
import com.easylearning.api.model.MonthlyPeriod;
import com.easylearning.api.model.MonthlyPeriodDetail;
import com.easylearning.api.model.RevenueShare;
import com.easylearning.api.repository.AccountRepository;
import com.easylearning.api.repository.MonthlyPeriodDetailRepository;
import com.easylearning.api.repository.MonthlyPeriodRepository;
import com.easylearning.api.repository.RevenueShareRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.easylearning.api.utils.DateUtils.formatDate;

@Component
@Slf4j
public class MonthlyPeriodSchedule extends ABasicController {
    @Autowired
    private MonthlyPeriodRepository monthlyPeriodRepository;

    @Autowired
    private RevenueShareRepository revenueShareRepository;

    @Autowired
    private MonthlyPeriodDetailRepository monthlyPeriodDetailRepository;

    @Autowired
    private AccountRepository  accountRepository;

    @Scheduled(cron = "0 0 0 1 * *", zone = "UTC")
    @Transactional
    public void monthlyPeriodSchedule() {
        runPeriodSchedule(false, false, null, null);
    }

    @Transactional
    public void runPeriodSchedule(boolean isManualRun, boolean isReCalculate, Date oldStartDate, Date oldEndDate) {
        log.error("Starting payout period");
        try {
            Date startDate, endDate;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (isReCalculate) {
                startDate = oldStartDate;
                endDate = oldEndDate;
            }else if (isManualRun) {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = calendar.getTime();

                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                endDate = calendar.getTime();
            } else {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH, -1);
                startDate = calendar.getTime();

                calendar.add(Calendar.MONTH, 1);
                endDate = calendar.getTime();
            }


            Boolean existMonthlyPeriod = monthlyPeriodRepository.existsByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate);
            if (!existMonthlyPeriod) {
                // for first run
                RevenueShare oldestRevenueShareUnPaid = revenueShareRepository.findByPayoutStatusOrderByCreatedDateAscLimitOne(LifeUniConstant.REVENUE_SHARE_PAYOUT_STATUS_UNPAID);
                if (oldestRevenueShareUnPaid != null && oldestRevenueShareUnPaid.getCreatedDate().before(startDate)) {
                    startDate = oldestRevenueShareUnPaid.getCreatedDate();
                }

                MonthlyPeriod monthlyPeriod = new MonthlyPeriod();
                monthlyPeriod.setName(formatDate(new Date(), LifeUniConstant.SCHEDULE_DATE_TIME_FORMAT));
                monthlyPeriod.setStartDate(startDate);
                monthlyPeriod.setEndDate(endDate);
                monthlyPeriodRepository.save(monthlyPeriod);

                // find list RevenueShare in valid time and null monthlyPeriod with each monthlyPeriod
                double totalSellerAndExpertRevenue = 0; //Tổng thu nhập seller & expert thông thường
                double totalSystemRevenue = 0;
                double totalSellerAndExpertSystemRevenue = 0; //Tổng thu nhập seller & expert System
                double totalSaleOffMoney = 0; // Tổng tiền giảm giá
                Map<Long, MonthlyPeriodDetail> periodDetailMap = new HashMap<>();

                List<RevenueShare> revenueShares = revenueShareRepository.findAllRevenueShareBetweenAndPayoutStatus(
                        startDate, endDate, LifeUniConstant.REVENUE_SHARE_PAYOUT_STATUS_UNPAID);

                List<Long> calculatedIds = new ArrayList<>(); // for sale off money

                for (RevenueShare revenueShare : revenueShares) {
                    Long accountId = null;
                    boolean isSystemSellerOrExpert = false;
                    if (revenueShare.getSeller() != null) {
                        accountId = revenueShare.getSeller().getId();
                        isSystemSellerOrExpert = revenueShare.getSeller().getIsSystemSeller();
                    } else if (revenueShare.getExpert() != null) {
                        accountId = revenueShare.getExpert().getId();
                        isSystemSellerOrExpert = revenueShare.getExpert().getIsSystemExpert();
                    }
                    // seller and expert
                    if (accountId != null) {
                        if (periodDetailMap.containsKey(accountId)) {
                            // if monthlyPeriodDetail exist in periodDetailMap => update money & totalCourse or totalRefCourse
                            MonthlyPeriodDetail monthlyPeriodDetail = periodDetailMap.get(accountId);
                            if (Objects.equals(revenueShare.getKind(), LifeUniConstant.REVENUE_SHARE_KIND_REF)) {
                                monthlyPeriodDetail.setTotalRefMoney(monthlyPeriodDetail.getTotalRefMoney() + revenueShare.getRevenueMoney());
                            } else {
                                monthlyPeriodDetail.setTotalMoney(monthlyPeriodDetail.getTotalMoney() + revenueShare.getRevenueMoney());
                            }
                        } else {
                            Account account = accountRepository.findById(accountId).orElse(null);
                            MonthlyPeriodDetail monthlyPeriodDetail = createPeriodDetailByPayoutPeriodAndAccountIdAndRevenueShare(monthlyPeriod, account, revenueShare, revenueShare.getRevenueMoney());
                            periodDetailMap.put(accountId, monthlyPeriodDetail);
                        }
                        if (isSystemSellerOrExpert) {
                            totalSellerAndExpertSystemRevenue += revenueShare.getRevenueMoney();
                        } else {
                            totalSellerAndExpertRevenue += revenueShare.getRevenueMoney();
                        }
                    } else { //Doanh thu hệ thống
                        totalSystemRevenue += revenueShare.getRevenueMoney();
                    }
                    // Xử lý giảm giá từ mã khuyến mãi
                    Long bookingId = revenueShare.getCourseTransaction().getBooking().getId();
                    if (!calculatedIds.contains(bookingId)) {
                        calculatedIds.add(bookingId);
                        totalSaleOffMoney += revenueShare.getCourseTransaction().getBooking().getSaleOffMoney();
                    }
                    revenueShare.setPayoutStatus(LifeUniConstant.REVENUE_SHARE_PAYOUT_STATUS_PAID);
                }

                List<MonthlyPeriodDetail> monthlyPeriodDetails = new ArrayList<>(periodDetailMap.values());

                if (totalSystemRevenue > 0) {
                    MonthlyPeriodDetail systemMonthlyPeriodDetail = createSystemPeriodDetail(monthlyPeriod, totalSystemRevenue);
                    monthlyPeriodDetails.add(systemMonthlyPeriodDetail);
                }

                monthlyPeriodDetailRepository.saveAll(monthlyPeriodDetails);
                revenueShareRepository.saveAll(revenueShares);

                monthlyPeriod.setState(LifeUniConstant.MONTHLY_PERIOD_STATE_CALCULATED);
                monthlyPeriod.setTotalPayout(totalSellerAndExpertRevenue);
                monthlyPeriod.setSystemRevenue(totalSystemRevenue + totalSellerAndExpertSystemRevenue); //Doanh thu bao gồm tiền của hệ thống vào tiền của seller system + expert system
                monthlyPeriod.setTotalSaleOffMoney(totalSaleOffMoney);
                monthlyPeriodRepository.save(monthlyPeriod);
            }
        } catch (Exception e) {
            log.error("payoutPeriod error: {}", e.getMessage());
        }
    }

    public MonthlyPeriodDetail createSystemPeriodDetail(MonthlyPeriod monthlyPeriod, Double totalMoney){
        MonthlyPeriodDetail monthlyPeriodDetail = new MonthlyPeriodDetail();
        monthlyPeriodDetail.setPeriod(monthlyPeriod);
        monthlyPeriodDetail.setKind(LifeUniConstant.PERIOD_DETAIL_KIND_SYSTEM);
        monthlyPeriodDetail.setState(LifeUniConstant.PERIOD_DETAIL_STATE_UNPAID);
        monthlyPeriodDetail.setTotalRefMoney(0d);
        monthlyPeriodDetail.setTotalMoney(totalMoney);
        monthlyPeriodDetail.setAccount(getSystemAccount());
        return monthlyPeriodDetail;
    }

    public MonthlyPeriodDetail createPeriodDetailByPayoutPeriodAndAccountIdAndRevenueShare(MonthlyPeriod monthlyPeriod, Account account, RevenueShare revenueShare, Double revenueShareMoney) {
        MonthlyPeriodDetail monthlyPeriodDetail = new MonthlyPeriodDetail();
        monthlyPeriodDetail.setPeriod(monthlyPeriod);
        monthlyPeriodDetail.setState(LifeUniConstant.PERIOD_DETAIL_STATE_UNPAID);
        monthlyPeriodDetail.setAccount(account);
        monthlyPeriodDetail.setTotalRefMoney(0d);
        monthlyPeriodDetail.setTotalMoney(0d);
        if(revenueShare.getKind().equals(LifeUniConstant.REVENUE_SHARE_KIND_REF)) {
            monthlyPeriodDetail.setTotalRefMoney(revenueShareMoney);
        } else {
            monthlyPeriodDetail.setTotalMoney(revenueShareMoney);
        }

        // set kind
        if (revenueShare.getExpert() != null) {
            monthlyPeriodDetail.setKind(LifeUniConstant.PERIOD_DETAIL_KIND_EXPERT);
        } else {
            monthlyPeriodDetail.setKind(LifeUniConstant.PERIOD_DETAIL_KIND_SELLER);
        }
        return monthlyPeriodDetail;
    }
}

