package com.easylearning.api.controller;


import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.registerPeriod.RegisterPeriodDto;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.registerPeriod.ChangeStateRegisterPeriodForm;
import com.easylearning.api.mapper.RegisterPayoutMapper;
import com.easylearning.api.mapper.RegisterPeriodMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.RegisterPeriodCriteria;
import com.easylearning.api.repository.*;
import com.easylearning.api.schedule.RegisterPeriodSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/register-period")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class RegisterPeriodController extends ABasicController {
    @Autowired
    private RevenueShareRepository revenueShareRepository;
    @Autowired
    private RegisterPayoutRepository registerPayoutRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ExpertRepository expertRepository;
    @Autowired
    private RegisterPeriodMapper registerPeriodMapper;
    @Autowired
    private RegisterPeriodRepository registerPeriodRepository;
    @Autowired
    private RegisterPayoutMapper registerPayoutMapper;
    @Autowired
    private RegisterPeriodSchedule registerPeriodSchedule;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RPE_L')")
    public ApiMessageDto<ResponseListDto<List<RegisterPeriodDto>>> list(RegisterPeriodCriteria registerPeriodCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<RegisterPeriodDto>>> apiMessageDto = new ApiMessageDto<>();
        registerPeriodCriteria.setIsOrderByCreatedDate(true);
        Page<RegisterPeriod> registerPeriods = registerPeriodRepository.findAll(registerPeriodCriteria.getSpecification(), pageable);
        ResponseListDto<List<RegisterPeriodDto>> responseListDto = new ResponseListDto<>(
                registerPeriodMapper.fromEntityToDtoList(registerPeriods.getContent()),
                registerPeriods.getTotalElements(), registerPeriods.getTotalPages()
        );
        // set excel name
        for (RegisterPeriodDto registerPeriodDto: responseListDto.getContent()){
            registerPeriodDto.setExcelName("uy_nhiem_chi_"+registerPeriodDto.getName());
        }
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RPE_V')")
    public ApiMessageDto<RegisterPeriodDto> getPayoutPeriod(@PathVariable("id") Long id) {
        ApiMessageDto<RegisterPeriodDto> apiMessageDto = new ApiMessageDto<>();
        RegisterPeriod registerPeriod = registerPeriodRepository.findById(id).orElse(null);
        if (registerPeriod == null) {
            throw new NotFoundException("register period not found",ErrorCode.REGISTER_PERIOD_ERROR_NOT_FOUND);
        }
        RegisterPeriodDto registerPeriodDto = registerPeriodMapper.fromEntityToDto(registerPeriod);
        registerPeriodDto.setExcelName("uy_nhiem_chi_"+registerPeriodDto.getName());
        registerPeriodDto.setRegisterPayoutDtos(registerPayoutMapper.fromEntityToRegisterPayoutDtoList(registerPayoutRepository.findAllByRegisterPeriodIdAndKind(registerPeriodDto.getId(), LifeUniConstant.REGISTER_PAYOUT_KIND_SUM)));
        apiMessageDto.setData(registerPeriodDto);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get register period success.");
        return apiMessageDto;
    }


    @Transactional
    @PutMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RPE_A')")
    public ApiMessageDto<String> approve(@Valid @RequestBody ChangeStateRegisterPeriodForm form, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(!isAdmin()){
            throw new UnauthorizationException("Not allowed change state");
        }
        RegisterPeriod registerPeriod = registerPeriodRepository.findByIdAndState(form.getId(), LifeUniConstant.REGISTER_PERIOD_STATE_PENDING);
        if (registerPeriod == null) {
            throw new NotFoundException("register period not found", ErrorCode.REGISTER_PERIOD_ERROR_NOT_FOUND);
        }
        registerPeriod.setState(LifeUniConstant.REGISTER_PERIOD_STATE_APPROVE);

        List<RegisterPayout> registerPayouts = registerPayoutRepository.findAllByRegisterPeriodIdAndKind(form.getId(), LifeUniConstant.REGISTER_PAYOUT_KIND_SUM);
        for(RegisterPayout registerPayout: registerPayouts){
            //wallet
            Wallet wallet = walletRepository.findByAccountId(registerPayout.getAccount().getId());
            if(wallet == null){
                throw new NotFoundException("Not found wallet of account: "+ registerPayout.getAccount().getId(), ErrorCode.WALLET_ERROR_NOT_FOUND);
            }
            wallet.setHoldingBalance(0D);// holding money -> account
            walletRepository.save(wallet);

            //Tạo revenue share kind out
            if(Objects.equals(registerPayout.getAccountKind(), LifeUniConstant.USER_KIND_STUDENT)){
                Student seller = studentRepository.findById(registerPayout.getAccount().getId()).orElse(null);
                if(seller == null){
                    throw new NotFoundException("Seller not found", ErrorCode.SELLER_ERROR_NOT_FOUND);
                }
                createWalletTransaction(wallet, LifeUniConstant.WALLET_TRANSACTION_KIND_WITH_DRAW, registerPayout.getMoney(), LifeUniConstant.WALLET_TRANSACTION_STATE_SUCCESS, LifeUniConstant.WALLET_TRANSACTION_NOTE_WITH_DRAW, wallet.getBalance());
            }
            else {
                Expert expert = expertRepository.findById(registerPayout.getAccount().getId()).orElse(null);
                if(expert == null){
                    throw new NotFoundException("Expert not found", ErrorCode.EXPERT_ERROR_NOT_FOUND);
                }
                createWalletTransaction(wallet, LifeUniConstant.WALLET_TRANSACTION_KIND_WITH_DRAW, registerPayout.getMoney(), LifeUniConstant.WALLET_TRANSACTION_STATE_SUCCESS, LifeUniConstant.WALLET_TRANSACTION_NOTE_WITH_DRAW, wallet.getBalance());
            }
        }
        registerPayoutRepository.updateStateByPeriodIdAndCurrentState(LifeUniConstant.REGISTER_PAYOUT_STATE_APPROVED, LifeUniConstant.REGISTER_PAYOUT_STATE_CALCULATED, registerPeriod.getId());
        registerPeriodRepository.save(registerPeriod);
        apiMessageDto.setMessage("Approve register period success");
        return apiMessageDto;
    }
    public void createRevenue(Expert expert, Student seller, Integer kind, Double money){
        RevenueShare revenueShare = new RevenueShare();
        revenueShare.setSeller(seller);
        revenueShare.setExpert(expert);
        revenueShare.setRevenueMoney(money);
        revenueShare.setKind(kind);
        revenueShareRepository.save(revenueShare);
    }
    @PutMapping(value = "/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PRE_RE')")
    public ApiMessageDto<String> cancel(@Valid @RequestBody ChangeStateRegisterPeriodForm form, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(!isAdmin()){
            throw new UnauthorizationException("Not allowed change state");
        }
        RegisterPeriod registerPeriod = registerPeriodRepository.findByIdAndState(form.getId(),LifeUniConstant.REGISTER_PERIOD_STATE_PENDING);
        if (registerPeriod == null) {
            throw new NotFoundException("register period not found", ErrorCode.REGISTER_PERIOD_ERROR_NOT_FOUND);
        }
        List<RegisterPayout> registerPayouts = registerPayoutRepository.findAllByRegisterPeriodIdAndKind(form.getId(), LifeUniConstant.REGISTER_PAYOUT_KIND_SUM);
        for (RegisterPayout registerPayout: registerPayouts){
            handleMoneyWhenCancelRegisterPayout(registerPayout, walletRepository);
        }
        registerPayoutRepository.updateStateByPeriodIdAndCurrentState(LifeUniConstant.REGISTER_PAYOUT_STATE_ADMIN_CANCELLED, LifeUniConstant.REGISTER_PAYOUT_STATE_CALCULATED, registerPeriod.getId());
        registerPeriod.setState(LifeUniConstant.REGISTER_PERIOD_STATE_REJECT);
        registerPeriodRepository.save(registerPeriod);
        apiMessageDto.setMessage("cancel register period success!");
        return apiMessageDto;
    }
    @ApiIgnore
    @GetMapping(value = "/run-register-period-schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> runScheduler() {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed to run.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        registerPeriodSchedule.registerPeriodSchedule();
        apiMessageDto.setMessage("run register period schedule success");
        return apiMessageDto;
    }
}
