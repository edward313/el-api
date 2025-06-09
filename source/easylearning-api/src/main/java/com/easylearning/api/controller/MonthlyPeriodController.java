package com.easylearning.api.controller;


import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.monthlyPeriod.MonthlyPeriodAdminDto;
import com.easylearning.api.dto.monthlyPeriod.MonthlyPeriodDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.monthlyPeriod.ChangeStateMonthlyPeriodForm;
import com.easylearning.api.form.monthlyPeriod.ReCalculateMonthlyPeriodForm;
import com.easylearning.api.mapper.MonthlyPeriodDetailMapper;
import com.easylearning.api.mapper.MonthlyPeriodMapper;
import com.easylearning.api.model.MonthlyPeriod;
import com.easylearning.api.model.MonthlyPeriodDetail;
import com.easylearning.api.model.Wallet;
import com.easylearning.api.model.criteria.PayoutPeriodCriteria;
import com.easylearning.api.repository.MonthlyPeriodDetailRepository;
import com.easylearning.api.repository.MonthlyPeriodRepository;
import com.easylearning.api.repository.RevenueShareRepository;
import com.easylearning.api.repository.WalletRepository;
import com.easylearning.api.schedule.MonthlyPeriodSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/monthly-period")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class MonthlyPeriodController extends ABasicController {

    @Autowired
    private MonthlyPeriodRepository monthlyPeriodRepository;

    @Autowired
    private MonthlyPeriodMapper monthlyPeriodMapper;

    @Autowired
    private MonthlyPeriodDetailRepository monthlyPeriodDetailRepository;

    @Autowired
    private MonthlyPeriodDetailMapper monthlyPeriodDetailMapper;

    @Autowired
    private RevenueShareRepository revenueShareRepository;
    @Autowired
    private MonthlyPeriodSchedule monthlyPeriodSchedule;
    @Autowired
    private WalletRepository walletRepository;

    @PostMapping("/recalculate-period")
    @PreAuthorize("hasRole('MP_RC')")
    public ApiMessageDto<String> reCalculatePeriod(@Valid @RequestBody ReCalculateMonthlyPeriodForm reCalculateMonthlyPeriodForm) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        MonthlyPeriod monthlyPeriod = monthlyPeriodRepository.findById(reCalculateMonthlyPeriodForm.getPeriodId()).orElse(null);
        if (monthlyPeriod == null || monthlyPeriod.getState().equals(LifeUniConstant.MONTHLY_PERIOD_STATE_DONE)) {
            throw new NotFoundException("Monthly period not found", ErrorCode.MONTHLY_PERIOD_ERROR_NOT_FOUND);
        }
        Date startDate = monthlyPeriod.getStartDate();
        Date endDate = monthlyPeriod.getEndDate();

        monthlyPeriodDetailRepository.deleteAllByPeriodId(monthlyPeriod.getId());
        monthlyPeriodRepository.deleteById(monthlyPeriod.getId());
        revenueShareRepository.updateAllByPayoutStatusAndCreatedDateBetweenStartDateAndEndDate(LifeUniConstant.REVENUE_SHARE_PAYOUT_STATUS_UNPAID, startDate, endDate);
        monthlyPeriodSchedule.runPeriodSchedule(false, true, startDate, endDate);

        apiMessageDto.setMessage("Recalculate monthly period successful");
        return apiMessageDto;
    }


    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MP_L')")
    public ApiMessageDto<ResponseListDto<List<MonthlyPeriodAdminDto>>> listPayoutPeriod(PayoutPeriodCriteria payoutPeriodCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<MonthlyPeriodAdminDto>>> apiMessageDto = new ApiMessageDto<>();
        payoutPeriodCriteria.setIsOrderByCreatedDate(true);
        Page<MonthlyPeriod> payoutPeriods = monthlyPeriodRepository.findAll(payoutPeriodCriteria.getSpecification(), pageable);
        ResponseListDto<List<MonthlyPeriodAdminDto>> responseListDto = new ResponseListDto<>(
                monthlyPeriodMapper.fromEntityToMonthlyPeriodAdminDtoList(payoutPeriods.getContent()),
                payoutPeriods.getTotalElements(),
                payoutPeriods.getTotalPages()
        );
        // set excel name
        for (MonthlyPeriodAdminDto monthlyPeriodAdminDto : responseListDto.getContent()){
            if(monthlyPeriodAdminDto.getState().equals(LifeUniConstant.MONTHLY_PERIOD_STATE_DONE)){
                monthlyPeriodAdminDto.setExcelName("uy_nhiem_chi_"+ monthlyPeriodAdminDto.getName());
            }
        }
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get Monthly period list success");
        return apiMessageDto;
    }

    @ApiIgnore
    @GetMapping(value = "/run-payout-period", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> runPayoutPeriod() {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed to run.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        monthlyPeriodSchedule.runPeriodSchedule(true, false, null, null);
        apiMessageDto.setMessage("run Monthly period schedule success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MP_V')")
    public ApiMessageDto<MonthlyPeriodAdminDto> getPayoutPeriod(@PathVariable("id") Long id) {
        ApiMessageDto<MonthlyPeriodAdminDto> apiMessageDto = new ApiMessageDto<>();
        MonthlyPeriod monthlyPeriod = monthlyPeriodRepository.findById(id).orElse(null);
        if (monthlyPeriod == null) {
            throw new NotFoundException("Monthly period not found",ErrorCode.MONTHLY_PERIOD_ERROR_NOT_FOUND);
        }
        MonthlyPeriodAdminDto monthlyPeriodAdminDto = monthlyPeriodMapper.fromEntityToMonthlyPeriodAdminDto(monthlyPeriod);
        if(!Objects.equals(monthlyPeriod.getState(), LifeUniConstant.MONTHLY_PERIOD_STATE_PENDING)){
            List<MonthlyPeriodDetail> monthlyPeriodDetails = monthlyPeriodDetailRepository.findAllByPeriodId(id);
            monthlyPeriodAdminDto.setPeriodDetails(monthlyPeriodDetailMapper.fromEntityToPeriodDetailAdminDtoList(monthlyPeriodDetails));
        }

        apiMessageDto.setData(monthlyPeriodAdminDto);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get payout period success.");
        return apiMessageDto;
    }


    @Transactional
    @PutMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MP_A')")
    public ApiMessageDto<String> approve(@Valid @RequestBody ChangeStateMonthlyPeriodForm changeStateMonthlyPeriodForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(!isAdmin()){
            throw new UnauthorizationException("Not allowed change state");
        }
        MonthlyPeriod existingMonthlyPeriod = monthlyPeriodRepository.findByIdAndState(changeStateMonthlyPeriodForm.getId(),LifeUniConstant.MONTHLY_PERIOD_STATE_CALCULATED);
        if (existingMonthlyPeriod == null) {
            throw new NotFoundException("Monthly period not found", ErrorCode.MONTHLY_PERIOD_ERROR_NOT_FOUND);
        }
        existingMonthlyPeriod.setState(LifeUniConstant.MONTHLY_PERIOD_STATE_DONE);
        List<MonthlyPeriodDetail> monthlyPeriodDetails = monthlyPeriodDetailRepository.findAllByPeriodId(changeStateMonthlyPeriodForm.getId());
        for(MonthlyPeriodDetail pd: monthlyPeriodDetails){
            Double totalMoney = pd.getTotalMoney() + pd.getTotalRefMoney();
            Wallet wallet = walletRepository.findByAccountId(pd.getAccount().getId());
            if(wallet == null){
                throw new NotFoundException("Not found wallet of account: "+ pd.getAccount().getId(),ErrorCode.WALLET_ERROR_NOT_FOUND);
            }
            wallet.setBalance(wallet.getBalance() + totalMoney);// holding money -> account
            walletRepository.save(wallet);

            //Create wallet transaction by periodDetail Kind
            if(Objects.equals(pd.getKind(), LifeUniConstant.PERIOD_DETAIL_KIND_SELLER)){ //Seller
                createWalletTransaction(wallet, LifeUniConstant.WALLET_TRANSACTION_KIND_IN, totalMoney, LifeUniConstant.WALLET_TRANSACTION_STATE_SUCCESS, LifeUniConstant.WALLET_TRANSACTION_NOTE_IN, wallet.getBalance());
            }else if(Objects.equals(pd.getKind(), LifeUniConstant.PERIOD_DETAIL_KIND_SYSTEM)){ //System
                createWalletTransaction(wallet, LifeUniConstant.WALLET_TRANSACTION_KIND_IN, totalMoney, LifeUniConstant.WALLET_TRANSACTION_STATE_SUCCESS,
                        LifeUniConstant.WALLET_TRANSACTION_NOTE_SYSTEM_REVENUE + pd.getPeriod().getName(), wallet.getBalance());
            } else { //Expert
                createWalletTransaction(wallet, LifeUniConstant.WALLET_TRANSACTION_KIND_IN, totalMoney, LifeUniConstant.WALLET_TRANSACTION_STATE_SUCCESS, LifeUniConstant.WALLET_TRANSACTION_NOTE_IN, wallet.getBalance());
            }

            pd.setState(LifeUniConstant.PERIOD_DETAIL_STATE_PAID);
            monthlyPeriodDetailRepository.save(pd);
        }
        monthlyPeriodRepository.save(existingMonthlyPeriod);
        monthlyPeriodRepository.updateBookingState(existingMonthlyPeriod.getStartDate(), existingMonthlyPeriod.getEndDate(), LifeUniConstant.BOOKING_PAYOUT_STATUS_PAID);
        apiMessageDto.setMessage("Approve payout period success");
        return apiMessageDto;
    }

    @PutMapping(value = "/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MP_CAN')")
    public ApiMessageDto<String> cancel(@Valid @RequestBody ChangeStateMonthlyPeriodForm changeStateMonthlyPeriodForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(!isAdmin()){
            throw new UnauthorizationException("Not allowed change state");
        }
        MonthlyPeriod existingMonthlyPeriod = monthlyPeriodRepository.findByIdAndState(changeStateMonthlyPeriodForm.getId(),LifeUniConstant.MONTHLY_PERIOD_STATE_CALCULATED);
        if (existingMonthlyPeriod == null) {
            throw new NotFoundException("Monthly period not found", ErrorCode.MONTHLY_PERIOD_ERROR_NOT_FOUND);
        }
        //cancel monthlyPeriodDetail
        monthlyPeriodDetailRepository.updateStateByPeriodId(changeStateMonthlyPeriodForm.getId(), LifeUniConstant.MONTHLY_PERIOD_STATE_CANCEL);
        //return payout state of RevenueShare
        revenueShareRepository.updatePayoutStatusBetween(existingMonthlyPeriod.getStartDate(), existingMonthlyPeriod.getEndDate(),LifeUniConstant.REVENUE_SHARE_PAYOUT_STATUS_UNPAID);

        existingMonthlyPeriod.setState(LifeUniConstant.MONTHLY_PERIOD_STATE_CANCEL);
        monthlyPeriodRepository.save(existingMonthlyPeriod);

        apiMessageDto.setMessage("Cancel Monthy period success !");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MP_D')")
    public ApiMessageDto<String> deletePayoutPeriod(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        MonthlyPeriod monthlyPeriod = monthlyPeriodRepository.findById(id).orElse(null);
        if (monthlyPeriod == null) {
            throw new NotFoundException("Monthly period not found", ErrorCode.MONTHLY_PERIOD_ERROR_NOT_FOUND);
        }

        monthlyPeriodDetailRepository.deleteAllByPeriodId(id);
        monthlyPeriodRepository.deleteById(id);
        apiMessageDto.setMessage("Delete payout period success");
        return apiMessageDto;
    }
    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<MonthlyPeriodDto>>> listPayoutPeriod(PayoutPeriodCriteria payoutPeriodCriteria) {
        ApiMessageDto<ResponseListDto<List<MonthlyPeriodDto>>> apiMessageDto = new ApiMessageDto<>();
        Pageable pageable = PageRequest.of(0,10);
        payoutPeriodCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Page<MonthlyPeriod> payoutPeriods = monthlyPeriodRepository.findAll(payoutPeriodCriteria.getSpecification(), pageable);
        ResponseListDto<List<MonthlyPeriodDto>> responseListDto = new ResponseListDto<>(
                monthlyPeriodMapper.fromEntityToMonthlyPeriodAutoCompleteList(payoutPeriods.getContent()),
                payoutPeriods.getTotalElements(),
                payoutPeriods.getTotalPages()
        );
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get Monthly period list success");
        return apiMessageDto;
    }
}
