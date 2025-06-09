package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.expertRegistration.ExpertRegistrationtAdminDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.form.expertRegistraion.ApproveExpertRegistrationForm;
import com.easylearning.api.form.expertRegistraion.CreateExpertRegistrationForm;
import com.easylearning.api.mapper.AccountMapper;
import com.easylearning.api.mapper.ExpertMapper;
import com.easylearning.api.mapper.ExpertRegistrationMapper;
import com.easylearning.api.model.Account;
import com.easylearning.api.model.Category;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.ExpertRegistration;
import com.easylearning.api.model.criteria.ExpertRegistrationCriteria;
import com.easylearning.api.repository.*;
import com.easylearning.api.service.rabbitMQ.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/expert-registration")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ExpertRegistrationController extends ABasicController{
    @Autowired
    private ExpertRegistrationRepository expertRegistrationRepository;
    @Autowired
    private ExpertRegistrationMapper expertRegistrationMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ExpertRepository expertRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private ExpertMapper expertMapper;
    @Autowired
    private ReferralExpertLogRepository referralExpertLogRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private WalletRepository walletRepository;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ER_V')")
    public ApiMessageDto<ExpertRegistrationtAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<ExpertRegistrationtAdminDto> apiMessageDto = new ApiMessageDto<>();
        ExpertRegistration expertRegistration = expertRegistrationRepository.findById(id).orElse(null);
        if (expertRegistration == null) {
            throw new NotFoundException("Not found expert registration",ErrorCode.EXPERT_REGISTRATION_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(expertRegistrationMapper.fromEntityToExpertRegistrationAdminDto(expertRegistration));
        apiMessageDto.setMessage("Get expert registration success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasRole('ER_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        ExpertRegistration expertRegistration = expertRegistrationRepository.findById(id).orElse(null);
        if (expertRegistration == null) {
            throw new NotFoundException("Not found expert registration",ErrorCode.EXPERT_REGISTRATION_ERROR_NOT_FOUND);
        }
        expertRegistrationRepository.deleteById(id)
        ;
        apiMessageDto.setMessage("Delete Expert Registration success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ER_L')")
    public ApiMessageDto<ResponseListDto<List<ExpertRegistrationtAdminDto>>> getList(ExpertRegistrationCriteria criteria, Pageable pageable) {

        ApiMessageDto<ResponseListDto<List<ExpertRegistrationtAdminDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<ExpertRegistrationtAdminDto>> responseListDto = new ResponseListDto<>();
        Page<ExpertRegistration> expertRegistrations = expertRegistrationRepository.findAll(criteria.getSpecification(), pageable);
        responseListDto.setContent(expertRegistrationMapper.fromEntityToExpertRegistrationAdminDtoList(expertRegistrations.getContent()));
        responseListDto.setTotalPages(expertRegistrations.getTotalPages());
        responseListDto.setTotalElements(expertRegistrations.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list Expert Registration success");
        return apiMessageDto;
    }

    @Transactional
    @PostMapping(value = "/registration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> create(@Valid @RequestBody CreateExpertRegistrationForm createForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Account existAccount = accountRepository.findByPhoneOrEmailAndKind(createForm.getPhone(),createForm.getEmail(),LifeUniConstant.USER_KIND_EXPERT);
        ExpertRegistration existExpertRegistration = expertRegistrationRepository.findFirstByPhoneOrEmail(createForm.getPhone(),createForm.getEmail());
        if(existAccount != null || existExpertRegistration != null){
            throw new BadRequestException("Phone or Email already used",ErrorCode.EXPERT_REGISTRATION_ERROR_PHONE_OR_EMAIL_ALREADY_USED);
        }
        // check referralCode
        if(StringUtils.isNoneBlank(createForm.getReferralCode())){
            Expert referralExpert = expertRepository.findByReferralCodeAndStatusAndAccountStatus(createForm.getReferralCode(),LifeUniConstant.STATUS_ACTIVE,LifeUniConstant.STATUS_ACTIVE);
            if(referralExpert == null){
                throw new BadRequestException("Invalid referral code",ErrorCode.EXPERT_REGISTRATION_ERROR_REFERRAL_CODE_INVALID);
            }
            if(Boolean.TRUE.equals(referralExpert.getIsSystemExpert())){
                throw new BadRequestException("Not allow use system expert code",ErrorCode.EXPERT_REGISTRATION_ERROR_NOT_ALLOW_USE_SYSTEM_CODE);
            }
        }
        Category field = categoryRepository.findFirstByIdAndStatus(createForm.getFieldId(), LifeUniConstant.STATUS_ACTIVE);
        if(field == null){
            throw new NotFoundException("Field not found",ErrorCode.CATEGORY_ERROR_NOT_FOUND);
        }
        ExpertRegistration expertRegistration = expertRegistrationMapper.fromCreateFormToExpertRegistration(createForm);
        // setNation
        expertRegistration.setWard(getNationByIdAndKind(createForm.getWardId(),LifeUniConstant.NATION_KIND_COMMUNE));
        expertRegistration.setProvince(getNationByIdAndKind(createForm.getProvinceId(),LifeUniConstant.NATION_KIND_PROVINCE));
        expertRegistration.setDistrict(getNationByIdAndKind(createForm.getDistrictId(),LifeUniConstant.NATION_KIND_DISTRICT));
        expertRegistration.setField(field);
        expertRegistrationRepository.save(expertRegistration);
        apiMessageDto.setMessage("create expert registration success");
        return apiMessageDto;
    }
    @Transactional
    @PutMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ER_A')")
    public ApiMessageDto<String> approve(@Valid @RequestBody ApproveExpertRegistrationForm approveForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        ExpertRegistration expertRegistration = expertRegistrationRepository.findById(approveForm.getId()).orElse(null);
        if(expertRegistration == null) {
            throw new NotFoundException("Not found expert registration",ErrorCode.EXPERT_REGISTRATION_ERROR_NOT_FOUND);
        }
        createExpertAccount(expertMapper.formApproveFormToCreateExpertForm(approveForm));
        expertRegistrationRepository.deleteById(expertRegistration.getId());
        apiMessageDto.setMessage("approve expert registration success");
        return apiMessageDto;
    }
}