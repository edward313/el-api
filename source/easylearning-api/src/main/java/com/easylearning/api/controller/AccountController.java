package com.easylearning.api.controller;


import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiResponse;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.account.AccountDto;
import com.easylearning.api.dto.account.ForgetPasswordDto;
import com.easylearning.api.dto.account.RequestForgetPasswordForm;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.account.CreateAccountAdminForm;
import com.easylearning.api.form.account.ForgetPasswordForm;
import com.easylearning.api.form.account.UpdateAccountAdminForm;
import com.easylearning.api.form.account.UpdateProfileAdminForm;
import com.easylearning.api.mapper.AccountMapper;
import com.easylearning.api.model.Account;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.Group;
import com.easylearning.api.model.Student;
import com.easylearning.api.model.criteria.AccountCriteria;
import com.easylearning.api.repository.*;
import com.easylearning.api.service.UserBaseApiService;
import com.easylearning.api.utils.AESUtils;
import com.easylearning.api.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/v1/account")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class AccountController extends ABasicController{
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    UserBaseApiService userBaseApiService;

    @Autowired
    ExpertRepository expertRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ReferralExpertLogRepository referralExpertLogRepository;

    @Autowired
    ReferralSellerLogRepository referralSellerLogRepository;

    @Autowired
    CourseRetailRepository courseRetailRepository;

    @Autowired
    CourseTransactionRepository courseTransactionRepository;

    @Autowired
    LessonRepository lessonRepository;
    @Autowired
    LessonVersioningRepository lessonVersioningRepository;
    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    CompletionRepository completionRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    SellerCodeTrackingRepository sellerCodeTrackingRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    MonthlyPeriodDetailRepository monthlyPeriodDetailRepository;

    @Autowired
    RevenueShareRepository revenueShareRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    CategoryHomeRepository categoryHomeRepository;

    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    CourseComboDetailVersionRepository courseComboDetailVersionRepository;
    @Autowired
    CourseComboDetailRepository courseComboDetailRepository;
    @Autowired
    CourseReviewHistoryRepository courseReviewHistoryRepository;
    @Autowired
    CourseVersioningRepository courseVersioningRepository;
    @Autowired
    VersionRepository versionRepository;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_L')")
    public ApiResponse<ResponseListDto<Account>> list(AccountCriteria accountCriteria, Pageable pageable) {
        if(!isSuperAdmin() ){
            throw new UnauthorizationException("Not allowed to list.");
        }
        accountCriteria.setKind(LifeUniConstant.USER_KIND_ADMIN);
        ApiResponse<ResponseListDto<Account>> apiMessageDto = new ApiResponse<>();
        Page<Account> accountList = accountRepository.findAll(accountCriteria.getSpecification() , pageable);
        ResponseListDto<Account> responseListDto = new ResponseListDto(accountList.getContent(), accountList.getTotalElements(), accountList.getTotalPages());
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list success");
        return apiMessageDto;
    }

    @PostMapping(value = "/create_admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_C_AD')")
    public ApiResponse<String> createAdmin(@Valid @RequestBody CreateAccountAdminForm createAccountAdminForm, BindingResult bindingResult) {
        ApiResponse<String> apiMessageDto = new ApiResponse<>();
        Account account = accountRepository.findAccountByUsername(createAccountAdminForm.getUsername());
        if (account != null) {
            throw new BadRequestException("Account is exist",ErrorCode.ACCOUNT_ERROR_USERNAME_EXIST);
        }
        Group group = groupRepository.findById(createAccountAdminForm.getGroupId()).orElse(null);
        if (group == null) {
            throw new NotFoundException("Group not found",ErrorCode.ACCOUNT_ERROR_UNKNOWN);
        }
        account = new Account();
        account.setUsername(createAccountAdminForm.getUsername());
        account.setPassword(passwordEncoder.encode(createAccountAdminForm.getPassword()));
        account.setFullName(createAccountAdminForm.getFullName());
        account.setKind(LifeUniConstant.USER_KIND_ADMIN);
        account.setEmail(createAccountAdminForm.getEmail());
        account.setGroup(group);
        account.setStatus(createAccountAdminForm.getStatus());
        account.setPhone(createAccountAdminForm.getPhone());
        accountRepository.save(account);

        apiMessageDto.setMessage("Create account admin success");
        return apiMessageDto;

    }

    @PutMapping(value = "/update_admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_U_AD')")
    public ApiResponse<String> updateAdmin(@Valid @RequestBody UpdateAccountAdminForm updateAccountAdminForm, BindingResult bindingResult) {

        ApiResponse<String> apiMessageDto = new ApiResponse<>();
        Account account = accountRepository.findById(updateAccountAdminForm.getId()).orElse(null);
        if (account == null) {
            throw new NotFoundException("Account not found",ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        Group group = groupRepository.findById(updateAccountAdminForm.getGroupId()).orElse(null);
        if (group == null) {
            throw new NotFoundException("Group not found",ErrorCode.ACCOUNT_ERROR_UNKNOWN);
        }
        if (StringUtils.isNoneBlank(updateAccountAdminForm.getPassword())) {
            account.setPassword(passwordEncoder.encode(updateAccountAdminForm.getPassword()));
        }
        account.setFullName(updateAccountAdminForm.getFullName());
        if (StringUtils.isNoneBlank(updateAccountAdminForm.getAvatarPath())) {
            if(!updateAccountAdminForm.getAvatarPath().equals(account.getAvatarPath())){
                //delete old image
                userBaseApiService.deleteFile(account.getAvatarPath());
            }
            account.setAvatarPath(updateAccountAdminForm.getAvatarPath());
        }
        account.setGroup(group);
        account.setStatus(updateAccountAdminForm.getStatus());
        account.setEmail(updateAccountAdminForm.getEmail());
        account.setPhone(updateAccountAdminForm.getPhone());
        accountRepository.save(account);

        apiMessageDto.setMessage("Update account admin success");
        return apiMessageDto;

    }


    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_V')")
    public ApiResponse<Account> get(@PathVariable("id") Long id) {
        Account shopProfile = accountRepository.findById(id).orElse(null);
        ApiResponse<Account> apiMessageDto = new ApiResponse<>();
        apiMessageDto.setData(shopProfile);
        apiMessageDto.setMessage("Get shop profile success");
        return apiMessageDto;

    }

    @Transactional
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_D')")
    public ApiResponse<String> delete(@PathVariable("id") Long id) {
        ApiResponse<String> apiMessageDto = new ApiResponse<>();
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            throw new NotFoundException("Account not found",ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        if(LifeUniConstant.USER_KIND_STUDENT.equals(account.getKind())){
            Student seller = studentRepository.findByIdAndIsSeller(id,LifeUniConstant.STUDENT_IS_SELLER).orElse(null);
            if(seller != null && Boolean.TRUE.equals(seller.getIsSystemSeller())){
                throw new BadRequestException("Cannot delete System Seller",ErrorCode.SELLER_ERROR_CANNOT_DELETE_SYSTEM_SELLER);
            }
        }
        if(LifeUniConstant.USER_KIND_EXPERT.equals(account.getKind())){
            Expert expert = expertRepository.findById(id).orElse(null);
            if(expert != null && Boolean.TRUE.equals(expert.getIsSystemExpert())){
                throw new BadRequestException("Cannot delete System Expert",ErrorCode.EXPERT_ERROR_CANNOT_DELETE_SYSTEM_EXPERT);
            }
        }
        //delete avatar file
        userBaseApiService.deleteFile(account.getAvatarPath());
        deleteExpertFolder(id);
        courseComboDetailVersionRepository.deleteAllByAccountsId(id);
        courseReviewHistoryRepository.deleteAllByAccountId(id);
        lessonVersioningRepository.deleteAllLessonByExpertId(id);
        courseVersioningRepository.deleteAllByExpertId(id);
        reviewRepository.updateCourseAverageStarAndTotalReview(id,LifeUniConstant.REVIEW_KIND_COURSE);
        reviewRepository.deleteAllByAccountId(id);
        monthlyPeriodDetailRepository.deleteAllByAccountId(id);
        revenueShareRepository.deleteAllRevenueShareByAccountId(id);
        categoryHomeRepository.deleteByAccount(id);
        courseTransactionRepository.deleteAllByAccountId(id);
        cartItemRepository.deleteAllByAccountId(id);
        courseRetailRepository.deleteAllByAccountId(id);
        registrationRepository.deleteAllByAccountId(id);
        completionRepository.deleteAllByStudentId(id);
        lessonRepository.deleteAllLessonByExpertId(id);
        courseComboDetailRepository.deleteAllByAccountsId(id);
        courseRepository.deleteAllByExpertId(id);
        sellerCodeTrackingRepository.deleteAllByAccountId(id);
        bookingRepository.deleteAllByStudentId(id);
        referralSellerLogRepository.deleteAllByStudentIdOrRefStudentId(id);
        referralExpertLogRepository.deleteAllByExpertIdOrRefExpertId(id);
        walletRepository.deleteAllByAccountId(id);
        studentRepository.deleteAllByAccountId(id);
        expertRepository.deleteAllByAccountId(id);
        versionRepository.deleteAllByAccountId(id);
        accountRepository.deleteById(id);
        apiMessageDto.setMessage("Delete Account success");
        return apiMessageDto;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<AccountDto> profile() {
        long id = getCurrentUser();
        Account account = accountRepository.findById(id).orElse(null);
        ApiResponse<AccountDto> apiMessageDto = new ApiResponse<>();
        if (account == null) {
            throw new NotFoundException("Account not found",ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(accountMapper.fromAccountToDto(account));
        apiMessageDto.setMessage("Get Account success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update_profile_admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> updateProfileAdmin(final HttpServletRequest request, @Valid @RequestBody UpdateProfileAdminForm updateProfileAdminForm, BindingResult bindingResult) {

        ApiResponse<String> apiMessageDto = new ApiResponse<>();
        long id =getCurrentUser();
        var account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            throw new NotFoundException("Account not found",ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        if(!passwordEncoder.matches(updateProfileAdminForm.getOldPassword(), account.getPassword())){
            throw new BadRequestException("Wrong password",ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
        }

        if (StringUtils.isNoneBlank(updateProfileAdminForm.getPassword())) {
            account.setPassword(passwordEncoder.encode(updateProfileAdminForm.getPassword()));
        }
        account.setPhone(updateProfileAdminForm.getPhone());
        account.setFullName(updateProfileAdminForm.getFullName());
        account.setAvatarPath(updateProfileAdminForm.getAvatarPath());
        accountRepository.save(account);

        apiMessageDto.setMessage("Update admin account success");
        return apiMessageDto;

    }

    @PostMapping(value = "/request_forget_password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ForgetPasswordDto> requestForgetPassword(@Valid @RequestBody RequestForgetPasswordForm forgetForm, BindingResult bindingResult){
        ApiResponse<ForgetPasswordDto> apiMessageDto = new ApiResponse<>();
        validationForgetForm(forgetForm.getApp(), forgetForm.getAccountKind());
        Account account = getAccountByEmailAndKind(forgetForm.getEmail(), forgetForm.getAccountKind(), forgetForm.getApp());

        String otp = userBaseApiService.getOTPForgetPassword();
        account.setAttemptCode(0);
        account.setResetPwdCode(otp);
        account.setResetPwdTime(new Date());
        accountRepository.save(account);

        //send email
        userBaseApiService.sendEmail(account.getEmail(),"OTP: "+otp, "Reset password",false);

        ForgetPasswordDto forgetPasswordDto = new ForgetPasswordDto();
        String hash = AESUtils.encrypt (account.getId()+";"+otp, true);
        forgetPasswordDto.setIdHash(hash);

        apiMessageDto.setResult(true);
        apiMessageDto.setData(forgetPasswordDto);
        apiMessageDto.setMessage("Request forget password successfull, please check email.");
        return  apiMessageDto;
    }

    @PostMapping(value = "/forget_password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Long> forgetPassword(@Valid @RequestBody ForgetPasswordForm forgetForm, BindingResult bindingResult){
        ApiResponse<Long> apiMessageDto = new ApiResponse<>();

        String[] hash = AESUtils.decrypt(forgetForm.getIdHash(),true).split(";",2);
        Long id = ConvertUtils.convertStringToLong(hash[0]);
        if(id <= 0){
            throw new BadRequestException("Account wrong reset hash",ErrorCode.ACCOUNT_ERROR_WRONG_HASH_RESET_PASS);
        }


        Account account = accountRepository.findById(id).orElse(null);
        if (account == null ) {
            throw new NotFoundException("Account not found",ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }

        if(account.getAttemptCode() >= LifeUniConstant.MAX_ATTEMPT_FORGET_PWD){
            throw new BadRequestException("Account is locked",ErrorCode.ACCOUNT_ERROR_LOCKED);
        }

        if(!account.getResetPwdCode().equals(forgetForm.getOtp()) ||
                (new Date().getTime() - account.getResetPwdTime().getTime() >= LifeUniConstant.MAX_TIME_FORGET_PWD)){

            //tang so lan
            account.setAttemptCode(account.getAttemptCode()+1);
            accountRepository.save(account);

            throw new BadRequestException("OPT invalid",ErrorCode.ACCOUNT_ERROR_OPT_INVALID);
        }

        if (account.getPassword() != null && passwordEncoder.matches(forgetForm.getNewPassword(), account.getPassword())) {
            throw new BadRequestException("New password match current password", ErrorCode.ACCOUNT_ERROR_NEW_PASSWORD_MATCH_CURRENT_PASSWORD);
        }

        account.setResetPwdTime(null);
        account.setResetPwdCode(null);
        account.setAttemptCode(null);
        account.setPassword(passwordEncoder.encode(forgetForm.getNewPassword()));
        accountRepository.save(account);

        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Change password success.");
        return  apiMessageDto;
    }

    private Account getAccountByEmailAndKind(String email, Integer kind, Integer app) {
        Account account;
        if (app.equals(LifeUniConstant.CLIENT_APP)) {
            account = accountRepository.findAccountByEmailAndKind(email, LifeUniConstant.USER_KIND_STUDENT);
        }else {
            if (kind.equals(LifeUniConstant.USER_KIND_STUDENT)) {
                account = accountRepository.findByEmailAndKindAndIsSeller(email, LifeUniConstant.USER_KIND_STUDENT, LifeUniConstant.STUDENT_IS_SELLER);
            } else {
                account = accountRepository.findAccountByEmailAndKind(email, LifeUniConstant.USER_KIND_EXPERT);
            }
        }
        if (account == null) {
            throw new NotFoundException("Account not found",ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        return account;
    }

    private void validationForgetForm(Integer app, Integer kind) {
        if (app.equals(LifeUniConstant.PORTAL_APP) && kind == null) {
            throw new BadRequestException("Account kind is required");
        }
        if (!Objects.equals(kind, LifeUniConstant.USER_KIND_STUDENT)
                && !Objects.equals(kind, LifeUniConstant.USER_KIND_EXPERT)) {
            throw new BadRequestException("Invalid kind");
        }
    }
}
