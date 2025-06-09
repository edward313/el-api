package com.easylearning.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.statistical.FeStatisticDto;
import com.easylearning.api.dto.statistical.FeStatisticDtoImpl;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.model.Booking;
import com.easylearning.api.model.Statistical;
import com.easylearning.api.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/v1/manual")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ManualController extends ABasicController{

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private RegisterPayoutRepository registerPayoutRepository;
    @Autowired
    private RevenueShareRepository revenueShareRepository;
    @Autowired
    private RegisterPeriodRepository registerPeriodRepository;
    @Autowired
    private CourseTransactionRepository courseTransactionRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private CompletionRepository completionRepository;
    @Autowired
    private SellerCodeTrackingRepository sellerCodeTrackingRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ExpertRepository expertRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired
    private MonthlyPeriodDetailRepository monthlyPeriodDetailRepository;
    @Autowired
    private MonthlyPeriodRepository monthlyPeriodRepository;
    @Autowired
    private ReferralSellerLogRepository referralSellerLogRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StatisticalRepository statisticalRepository;
    @Autowired
    private CourseRetailRepository courseRetailRepository;
    @Autowired
    private ObjectMapper objectMapper;



    @ApiIgnore
    @Transactional
    @GetMapping("/reset-booking")
    public ApiMessageDto<String> resetBooking(@RequestParam(defaultValue = "false") Boolean isResetAll) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if (!isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed to run.");
        }
        resetBookingData(isResetAll);

        apiMessageDto.setMessage("Reset booking successfully");
        return apiMessageDto;
    }

    private void resetBookingData(Boolean isResetAll) {
        reviewRepository.deleteByKindNot(LifeUniConstant.REVIEW_KIND_SYSTEM);
        registerPayoutRepository.deleteAll();
        revenueShareRepository.deleteAll();
        registerPeriodRepository.deleteAll();
        courseTransactionRepository.deleteAll();
        cartItemRepository.deleteAll();
        registrationRepository.deleteAll();
        completionRepository.deleteAll();
        sellerCodeTrackingRepository.deleteAll();
        bookingRepository.deleteAll();
        expertRepository.resetTotalStudent();
        courseRepository.resetAverageStartAndTotalReviewAndSoldQuantity();
        walletRepository.resetBalanceAndHoldingBalance();
        walletTransactionRepository.deleteAll();
        notificationRepository.deleteAll();
        monthlyPeriodDetailRepository.deleteAll();
        monthlyPeriodRepository.deleteAll();
        resetStatisticValue();

        if (isResetAll) {
            referralSellerLogRepository.deleteAll();
            studentRepository.resetStudentIsSeller();
            courseRetailRepository.deleteAll();
        }
    }

    private void resetStatisticValue(){
        Statistical statistical = statisticalRepository.findFirstByStatisticalKey(LifeUniConstant.STATISTICAL_FE_STATISTIC_KEY);
        FeStatisticDtoImpl feStatistic = convertJsonToObject(statistical.getStatisticalValue());
        feStatistic.setTotalCourseSell(0);
        statistical.setStatisticalValue(convertFeStatisticImplToJson(feStatistic));
        statisticalRepository.save(statistical);
    }

    private String convertFeStatisticImplToJson(FeStatisticDtoImpl statistic) {
        String jsonValue =  null;
        try {
            jsonValue = objectMapper.writeValueAsString(statistic);
        }catch (JsonProcessingException jpe) {
            log.error("Error parsing object to json: "+jpe.getMessage());
        }
        return jsonValue;
    }

    private FeStatisticDtoImpl convertJsonToObject(String jsonValue) {
        FeStatisticDtoImpl feStatistic = null;
        try {
            feStatistic = objectMapper.readValue(jsonValue, FeStatisticDtoImpl.class);
        }catch (JsonProcessingException jpe){
            log.error("Error parsing json to object: "+jpe.getMessage());
        }
        return feStatistic;
    }
}
