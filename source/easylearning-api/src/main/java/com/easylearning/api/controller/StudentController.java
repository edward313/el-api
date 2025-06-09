package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.UploadFileDto;
import com.easylearning.api.dto.account.VerifyTokenResponseDto;
import com.easylearning.api.dto.booking.BookingDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.facebook.FacebookUserInfo;
import com.easylearning.api.dto.google.VerifyTokenGGResponseDto;
import com.easylearning.api.dto.student.StudentAdminDto;
import com.easylearning.api.dto.student.StudentDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.GoogleUserInfo;
import com.easylearning.api.form.UploadFileFromUrlForm;
import com.easylearning.api.form.account.RegisterProfileFacebookForm;
import com.easylearning.api.form.account.RegisterProfileGoogleForm;
import com.easylearning.api.form.account.VerifyFacebookForm;
import com.easylearning.api.form.account.VerifyGoogleForm;
import com.easylearning.api.form.student.SignUpStudentForm;
import com.easylearning.api.form.student.UpdateStudentForm;
import com.easylearning.api.form.student.UpdateStudentProfileForm;
import com.easylearning.api.form.student.UpgradeSellerForm;
import com.easylearning.api.mapper.AccountMapper;
import com.easylearning.api.mapper.BookingMapper;
import com.easylearning.api.mapper.CourseMapper;
import com.easylearning.api.mapper.StudentMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.BookingCriteria;
import com.easylearning.api.model.criteria.CourseCriteria;
import com.easylearning.api.model.criteria.StudentCriteria;
import com.easylearning.api.repository.*;
import com.easylearning.api.service.Oauth2JWTTokenService;
import com.easylearning.api.service.feign.FeignLifeUniMediaService;
import com.easylearning.api.service.oauth2Login.GoogleService;
import com.easylearning.api.service.oauth2Login.LoginFacebookService;
import com.easylearning.api.service.rabbitMQ.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/student")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class StudentController extends ABasicController{

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ReferralSellerLogRepository referralSellerLogRepository;
    @Autowired
    private CourseTransactionRepository courseTransactionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CompletionRepository completionRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private SellerCodeTrackingRepository sellerCodeTrackingRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private LoginFacebookService loginFacebookService;
    @Autowired
    private Oauth2JWTTokenService oauth2JWTTokenService;
    @Autowired
    private RevenueShareRepository revenueShareRepository;
    @Autowired
    private GoogleService googleService;
    @Autowired
    private MonthlyPeriodDetailRepository monthlyPeriodDetailRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private BookingMapper bookingMapper;
    @Autowired
    private FeignLifeUniMediaService feignLifeUniMediaService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ExpertRepository expertRepository;
    @Value("${admin.username}")
    private String admin;
    @Autowired
    private CourseRetailRepository courseRetailRepository;

    @Transactional
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> create(@Valid @RequestBody SignUpStudentForm signUpStudentForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Account accountByEmail = accountRepository.findAccountByEmail(signUpStudentForm.getEmail());
        if (accountByEmail != null) {
            throw new BadRequestException("email already exists", ErrorCode.ACCOUNT_ERROR_EMAIL_EXIST);
        }
        checkExistPhone(signUpStudentForm.getPhone());
        Account account = accountMapper.fromSignUpStudentToAccount(signUpStudentForm);
        account.setPassword(passwordEncoder.encode(signUpStudentForm.getPassword()));
        account.setKind(LifeUniConstant.USER_KIND_STUDENT);
        Group group = groupRepository.findFirstByKind(LifeUniConstant.GROUP_KIND_STUDENT);
        account.setGroup(group);
        accountRepository.save(account);
        createStudentWallet(account);

        Student student = studentMapper.fromSignUpFormToEntity(signUpStudentForm);
        student.setAccount(account);
        student.setIsSeller(LifeUniConstant.STUDENT_IS_NOT_SELLER);
        studentRepository.save(student);

        if(StringUtils.isNoneBlank(signUpStudentForm.getReferralCode())){
            createReferralSellerLog(signUpStudentForm.getReferralCode(),student);
            // Set default balance
//            Double systemBonus = getSystemBonus();
//            studentWallet.setBalance(systemBonus);
//            walletRepository.save(studentWallet);
//
//            createWalletTransaction(studentWallet, LifeUniConstant.WALLET_TRANSACTION_KIND_INIT_MONEY, studentWallet.getBalance(), LifeUniConstant.WALLET_TRANSACTION_STATE_SUCCESS, LifeUniConstant.WALLET_TRANSACTION_NOTE_INIT_MONEY, systemBonus);
//
//            student.setIsReferralBonusPaid(true);
//            studentRepository.save(student);
        }
        apiMessageDto.setMessage("Sign up success");
        return apiMessageDto;
    }
    public void checkExistPhone(String phone){
        if (StringUtils.isNoneBlank(phone)) {
            Account accountByPhone = accountRepository.findAccountByPhoneAndKind(phone, LifeUniConstant.USER_KIND_STUDENT);
            if (accountByPhone != null) {
                throw new BadRequestException("phone number already exists", ErrorCode.USER_ERROR_EXIST);
            }
        }
    }
    private void createStudentWallet(Account account){
        Wallet wallet = new Wallet();
        wallet.setAccount(account);
        wallet.setKind(LifeUniConstant.WALLET_KIND_STUDENT);
        wallet.setWalletNumber(getUniqueWalletNumber());
        walletRepository.save(wallet);
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ST_V')")
    public ApiMessageDto<StudentAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<StudentAdminDto> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found student", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(studentMapper.fromEntityToStudentAdminDto(student));
        apiMessageDto.setMessage("Get student success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasRole('ST_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found student", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        reviewRepository.updateCourseAverageStarAndTotalReview(id,LifeUniConstant.REVIEW_KIND_COURSE);
        expertRepository.updateExpertsAfterDeleteStudent(id);
        reviewRepository.deleteAllByAccountId(id);
        monthlyPeriodDetailRepository.deleteAllByAccountId(id);
        revenueShareRepository.deleteAllRevenueShareByAccountId(id);
        courseTransactionRepository.deleteAllByAccountId(id);
        bookingRepository.deleteAllByStudentId(id);
        referralSellerLogRepository.deleteAllByStudentIdOrRefStudentId(id);
        completionRepository.deleteAllByStudentId(id);
        registrationRepository.deleteAllByAccountId(id);
        sellerCodeTrackingRepository.deleteAllByAccountId(id);
        cartItemRepository.deleteAllByAccountId(id);
        walletRepository.deleteAllByAccountId(id);
        courseRetailRepository.deleteAllByAccountId(id);
        studentRepository.deleteById(student.getId());
        accountRepository.deleteById(student.getId());
        apiMessageDto.setMessage("Delete Student success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ST_L')")
    public ApiMessageDto<ResponseListDto<List<StudentAdminDto>>> getList(StudentCriteria studentCriteria, Pageable pageable) {

        ApiMessageDto<ResponseListDto<List<StudentAdminDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<StudentAdminDto>> responseListDto = new ResponseListDto<>();
        Page<Student> students = studentRepository.findAll(studentCriteria.getSpecification(), pageable);
        responseListDto.setContent(studentMapper.fromStudentListToStudentAdminDtoList(students.getContent()));
        responseListDto.setTotalPages(students.getTotalPages());
        responseListDto.setTotalElements(students.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list student success");
        return apiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<StudentDto>>> ListAutoComplete(StudentCriteria studentCriteria) {

        ApiMessageDto<ResponseListDto<List<StudentDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<StudentDto>> responseListDto = new ResponseListDto<>();
        Pageable pageable = PageRequest.of(0, 10);
        studentCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Page<Student> students = studentRepository.findAll(studentCriteria.getSpecification(), pageable);
        responseListDto.setContent(studentMapper.fromStudentListToStudentDtoListAutocomplete(students.getContent()));
        responseListDto.setTotalPages(students.getTotalPages());
        responseListDto.setTotalElements(students.getTotalElements());
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("get student success");
        return apiMessageDto;
    }

    @Transactional
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ST_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateStudentForm updateStudentForm, BindingResult bindingResult) {

        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findById(updateStudentForm.getId()).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found student", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        if (StringUtils.isNoneBlank(updateStudentForm.getPhone()) && student.getAccount().getPhone() != null && !student.getAccount().getPhone().equalsIgnoreCase(updateStudentForm.getPhone())) {
            Account account = accountRepository.findAccountByPhoneAndKind(updateStudentForm.getPhone(), LifeUniConstant.USER_KIND_STUDENT);
            if (account != null) {
                throw new BadRequestException("Phone is existed", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
            }
        }
        Account account = student.getAccount();
        if (StringUtils.isNoneBlank(updateStudentForm.getPassword())) {
            account.setPassword(passwordEncoder.encode(updateStudentForm.getPassword()));
        }
        student = setNationToStudent(student,updateStudentForm.getWardId(),updateStudentForm.getProvinceId(),updateStudentForm.getDistrictId());
        studentMapper.updateFromUpdateFormToStudentEntity(updateStudentForm, student);
        studentRepository.save(student);
        accountMapper.fromUpdateStudentFormToEntity(updateStudentForm, account);
        accountRepository.save(account);
        apiMessageDto.setMessage("update student success");
        return apiMessageDto;
    }

    @Transactional
    @PutMapping(value = "/upgrade-seller", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> upgradeStudentToSeller(@Valid @RequestBody UpgradeSellerForm updateStudentForm, BindingResult bindingResult) {
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed upgrade");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findByIdAndIsSeller(getCurrentUser(),LifeUniConstant.STUDENT_IS_NOT_SELLER).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found student", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        if(StringUtils.isNoneBlank(updateStudentForm.getReferralCode())){
            Student referralSeller = studentRepository.findByReferralCodeAndIsSeller(updateStudentForm.getReferralCode(), LifeUniConstant.STUDENT_IS_SELLER).orElse(null);
            if(referralSeller == null){
                throw new BadRequestException("referral code invalid", ErrorCode.STUDENT_REFERRAL_CODE_INVALID);
            }
            //notificationService.createNotificationAndSendMessage(LifeUniConstant.NOTIFICATION_KIND_UPGRADE_SELLER,referralSeller.getId());
            ReferralSellerLog referralSellerLog = new ReferralSellerLog();
            referralSellerLog.setStudent(student);
            referralSellerLog.setRefStudent(referralSeller);
            referralSellerLogRepository.save(referralSellerLog);
        }
        studentRepository.save(student);
        createSellerWallet(student.getAccount());
        apiMessageDto.setMessage("upgrade seller success");
        return apiMessageDto;
    }
    private void createSellerWallet(Account account){
        Wallet wallet = new Wallet();
        wallet.setAccount(account);
        wallet.setKind(LifeUniConstant.WALLET_KIND_SELLER);
        wallet.setWalletNumber(getUniqueWalletNumber());
        walletRepository.save(wallet);
    }
    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<StudentDto> getMyProfile()
    {
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed get");
        }
        ApiMessageDto<StudentDto> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findById(getCurrentUser()).orElse(null);
        if(student == null){
            throw new NotFoundException("Not found student", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        StudentDto studentDto = studentMapper.fromStudentToDtoForProfile(student);

        ReferralSellerLog referralSellerLog = referralSellerLogRepository.findFirstByStudentId(student.getId());
        studentDto.setReferralCode(referralSellerLog != null ? referralSellerLog.getRefStudent().getReferralCode() : null);

        apiMessageDto.setData(studentDto);
        apiMessageDto.setMessage("get student profile success");
        return apiMessageDto;
    }
    @Transactional
    @PutMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateStudentProfileForm updateStudentProfileForm, BindingResult bindingResult) {
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed update");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findById(getCurrentUser()).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found student", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        student = setNationToStudent(student,updateStudentProfileForm.getWardId(),updateStudentProfileForm.getProvinceId(),updateStudentProfileForm.getDistrictId());
        Account account = student.getAccount();
        if (!passwordEncoder.matches(updateStudentProfileForm.getOldPassword(),student.getAccount().getPassword())) {
            throw new BadRequestException("password is incorrect", ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
        }
        if (StringUtils.isNoneBlank(updateStudentProfileForm.getNewPassword())) {
            if(Objects.equals(updateStudentProfileForm.getNewPassword(), updateStudentProfileForm.getOldPassword())){
                throw new BadRequestException("newPassword match with current password",ErrorCode.ACCOUNT_ERROR_NEW_PASSWORD_MATCH_CURRENT_PASSWORD);
            }
            account.setPassword(passwordEncoder.encode(updateStudentProfileForm.getNewPassword()));
        }
        if(StringUtils.isNoneBlank(updateStudentProfileForm.getReferralCode())) {
            validationSellCode(student, updateStudentProfileForm.getReferralCode());
            createReferralSellerLog(updateStudentProfileForm.getReferralCode(),student);

            // Set init money if student does not have a referral
//            if (!student.getIsReferralBonusPaid()) {
//                double defaultBalance = getSystemBonus();
//                Wallet studentWallet = walletRepository.findFirstByAccountId(student.getAccount().getId());
//                if (studentWallet != null) {
//                    Double balance = studentWallet.getBalance() != null ?
//                            studentWallet.getBalance() + defaultBalance : defaultBalance;
//                    createWalletTransaction(studentWallet, LifeUniConstant.WALLET_TRANSACTION_KIND_INIT_MONEY, balance, LifeUniConstant.WALLET_TRANSACTION_STATE_SUCCESS, LifeUniConstant.WALLET_TRANSACTION_NOTE_INIT_MONEY, balance);
//                    studentWallet.setBalance(balance);
//                    walletRepository.save(studentWallet);
//
//                    student.setIsReferralBonusPaid(true);
//                }
//            }
        }
        studentMapper.updateFromUpdateStudentProfileFormToStudentEntity(updateStudentProfileForm, student);
        studentRepository.save(student);
        accountMapper.fromUpdateStudentProfileFormToEntity(updateStudentProfileForm, account);
        accountRepository.save(account);
        apiMessageDto.setMessage("update student profile success");
        return apiMessageDto;
    }

    private Student setNationToStudent(Student student, Long wardId, Long provinceId, Long districtId){
        if(wardId != null){
            student.setWard(getNationByIdAndKind(wardId,LifeUniConstant.NATION_KIND_COMMUNE));
        }
        if(provinceId != null){
            student.setProvince(getNationByIdAndKind(provinceId,LifeUniConstant.NATION_KIND_PROVINCE));
        }
        if(districtId != null){
            student.setDistrict(getNationByIdAndKind(districtId,LifeUniConstant.NATION_KIND_DISTRICT));
        }
        return student;
    }

    @GetMapping(value = "/my-course", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<CourseDto>>> myCourse(CourseCriteria courseCriteria, Pageable pageable) {
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed get");
        }
        courseCriteria.setStudentId(getCurrentUser());
        courseCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        courseCriteria.setIgnoreKind(LifeUniConstant.COURSE_KIND_COMBO);

        ApiMessageDto<ResponseListDto<List<CourseDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Course> courseList = courseRepository.findAll(courseCriteria.getSpecification(), pageable);
        ResponseListDto<List<CourseDto>> responseListObj = new ResponseListDto<>();
        List<CourseDto> courseDtoList = courseMapper.fromEntityToCourseDtoList(courseList.getContent());

        for (CourseDto courseDto: courseDtoList){
            setCompletionPercent(courseDto,getCurrentUser());
        }
        responseListObj.setContent(courseDtoList);
        responseListObj.setTotalPages(courseList.getTotalPages());
        responseListObj.setTotalElements(courseList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course success");
        return responseListObjApiMessageDto;
    }
    void setCompletionPercent(CourseDto courseDto, Long studentId){
        Float percent = courseRepository.getRatioCompleteLesson(courseDto.getId(),studentId,LifeUniConstant.LESSON_KIND_SECTION,true);
        courseDto.setPercent(Objects.requireNonNullElse(percent, 0F));
    }

    @GetMapping(value = "/my-booking", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<BookingDto>>> myBooking(BookingCriteria bookingCriteria, Pageable pageable) {
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed get");
        }
        bookingCriteria.setStudentId(getCurrentUser());

        ApiMessageDto<ResponseListDto<List<BookingDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Booking> bookings = bookingRepository.findAll(bookingCriteria.getSpecification(), pageable);
        ResponseListDto<List<BookingDto>> responseListObj = new ResponseListDto<>();
        List<BookingDto> bookingDtoList = bookingMapper.fromEntityToBookingDtoForClientList(bookings.getContent());

        responseListObj.setContent(bookingDtoList);
        responseListObj.setTotalPages(bookings.getTotalPages());
        responseListObj.setTotalElements(bookings.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list booking success");
        return responseListObjApiMessageDto;
    }
    @PostMapping(value = "/facebook/verify-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<VerifyTokenResponseDto> verifyTokenFacebook(@Valid @RequestBody VerifyFacebookForm verifyFacebookForm, BindingResult bindingResult) {
        ApiMessageDto<VerifyTokenResponseDto> apiMessageDto = new ApiMessageDto<>();
        FacebookUserInfo facebookUserInfo = loginFacebookService.getUserInfo(verifyFacebookForm.getToken());
        if (facebookUserInfo == null) {
            throw new BadRequestException("Get user info fail", ErrorCode.STUDENT_ERROR_CANNOT_GET_INFO_FROM_TOKEN);
        }
        Account accountByFacebookId = accountRepository.findFirstByFacebookId(facebookUserInfo.getId());
        VerifyTokenResponseDto verifyTokenResponseDto = new VerifyTokenResponseDto();
        if (accountByFacebookId == null) {
            accountByFacebookId = createNewAccountByFacebookInfo(facebookUserInfo,verifyFacebookForm.getToken());
        }
        if (accountByFacebookId.getStatus() == LifeUniConstant.STATUS_ACTIVE) {
            apiMessageDto.setMessage("Verify token success");
            verifyTokenResponseDto.setAccessToken(oauth2JWTTokenService.getAccessTokenForStudent(accountByFacebookId.getPhone()));
            apiMessageDto.setData(verifyTokenResponseDto);
            return apiMessageDto;
        }
        // return data to register account info
        if (accountByFacebookId.getStatus() == LifeUniConstant.STATUS_PENDING &&
                accountByFacebookId.getPlatform().equals(LifeUniConstant.ACCOUNT_PLATFORM_FACEBOOK)) {

            apiMessageDto.setMessage("Get data success");
            verifyTokenResponseDto.setPlatform(accountByFacebookId.getPlatform());
            verifyTokenResponseDto.setCode(accountByFacebookId.getResetPwdCode());
            verifyTokenResponseDto.setPlatformUserId(accountByFacebookId.getFacebookId());
            verifyTokenResponseDto.setEmail(accountByFacebookId.getEmail());
            apiMessageDto.setData(verifyTokenResponseDto);
            return apiMessageDto;
        }
        throw new BadRequestException("Account not active or pending", ErrorCode.ACCOUNT_ERROR_NOT_PENDING);
    }
    @PostMapping(value = "/google/verify-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<VerifyTokenGGResponseDto> verifyTokenGG(@Valid @RequestBody VerifyGoogleForm verifyGoogleForm, BindingResult bindingResult) {
        ApiMessageDto<VerifyTokenGGResponseDto> apiMessageDto = new ApiMessageDto<>();
        GoogleUserInfo googleUserInfo = googleService.getUserInfo(verifyGoogleForm.getToken());
        if(googleUserInfo == null){
            throw new BadRequestException("google code invalid", ErrorCode.USER_ERROR_GOOGLE_CODE_INVALID);
        }
        Student student = studentRepository.findFirstByAccount_EmailAndAccount_Platform(googleUserInfo.getEmail(), LifeUniConstant.ACCOUNT_PLATFORM_GOOGLE).orElse(null);
        Account account = null;
        if(student!= null){
            account = student.getAccount();
        }
        VerifyTokenGGResponseDto verifyTokenGGResponseDto = new VerifyTokenGGResponseDto();
        if(account == null) {
            account = createNewAccountByGoogleInfo(googleUserInfo);
        }
        // return access token
        if (account.getStatus() == LifeUniConstant.STATUS_ACTIVE) {
            apiMessageDto.setMessage("Verify token success");
            verifyTokenGGResponseDto.setAccessToken(oauth2JWTTokenService.getAccessTokenForStudent(account.getPhone()));
            apiMessageDto.setData(verifyTokenGGResponseDto);
            return apiMessageDto;
        }
        // return data to register account info
        if (account.getStatus() == LifeUniConstant.STATUS_PENDING && account.getPlatform().equals(LifeUniConstant.ACCOUNT_PLATFORM_GOOGLE)) {
            apiMessageDto.setMessage("get data to register info success");
            verifyTokenGGResponseDto.setPlatform(account.getPlatform());
            verifyTokenGGResponseDto.setCode(account.getResetPwdCode());
            verifyTokenGGResponseDto.setPlatformUserId(account.getGoogleId());
            apiMessageDto.setData(verifyTokenGGResponseDto);
            return apiMessageDto;
        }
        throw new BadRequestException("Account not active or pending", ErrorCode.ACCOUNT_ERROR_NOT_PENDING);
    }
    public Account createNewAccountByFacebookInfo(FacebookUserInfo facebookUserInfo, String accessToken) {
        Account account = accountMapper.fromFacebookUserInfoToAccount(facebookUserInfo);
        account.setResetPwdCode(com.easylearning.api.utils.StringUtils.generateRandomString(6));
        account.setStatus(LifeUniConstant.STATUS_PENDING);
        account.setKind(LifeUniConstant.USER_KIND_STUDENT);
        account.setPlatform(LifeUniConstant.ACCOUNT_PLATFORM_FACEBOOK);

        Group group = groupRepository.findFirstByKind(LifeUniConstant.GROUP_KIND_STUDENT);
        account.setGroup(group);
        accountRepository.save(account);
        updateAvatarFromInternetToLifeUniMedia(loginFacebookService.getFacebookAvatarUrl(accessToken),oauth2JWTTokenService.getInternalAccessToken(admin).getValue(),account.getId());

        Student student = new Student();
        student.setAccount(account);
        student.setIsSeller(LifeUniConstant.STUDENT_IS_NOT_SELLER);
        student.setIsReferralBonusPaid(false);
        student.setStatus(LifeUniConstant.STATUS_PENDING);
        studentRepository.save(student);

        createStudentWallet(account);
        return account;
    }
    public Account createNewAccountByGoogleInfo(GoogleUserInfo googleUserInfo){
        Account existAccount = accountRepository.findAccountByEmailOrGoogleId(googleUserInfo.getEmail(),googleUserInfo.getId());
        if (existAccount != null) {
            throw new BadRequestException("Email or GoogleId already exists", ErrorCode.ACCOUNT_ERROR_EXIST);
        }
        Account account = accountMapper.fromGoogleUserInfoToAccount(googleUserInfo);
        account.setResetPwdCode(com.easylearning.api.utils.StringUtils.generateRandomString(6));
        account.setStatus(LifeUniConstant.STATUS_PENDING);
        account.setKind(LifeUniConstant.USER_KIND_STUDENT);
        account.setPlatform(LifeUniConstant.ACCOUNT_PLATFORM_GOOGLE);
        Group group = groupRepository.findFirstByKind(LifeUniConstant.GROUP_KIND_STUDENT);
        account.setGroup(group);
        accountRepository.save(account);
        updateAvatarFromInternetToLifeUniMedia(account.getAvatarPath(),oauth2JWTTokenService.getInternalAccessToken(admin).getValue(),account.getId());
        Student student = new Student();
        student.setAccount(account);
        student.setIsSeller(LifeUniConstant.STUDENT_IS_NOT_SELLER);
        student.setIsReferralBonusPaid(false);
        student.setStatus(LifeUniConstant.STATUS_PENDING);
        studentRepository.save(student);
        createStudentWallet(account);
        return account;
    }
    @Transactional
    @PostMapping(value = "/facebook/register-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<OAuth2AccessToken> loginByGoogle(@Valid @RequestBody RegisterProfileFacebookForm registerProfileFacebookForm, BindingResult bindingResult) {
        ApiMessageDto<OAuth2AccessToken> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findFirstByIsSellerAndAccountFacebookIdAndAccountResetPwdCodeAndAccountStatus(LifeUniConstant.STUDENT_IS_NOT_SELLER,registerProfileFacebookForm.getPlatformUserId(), registerProfileFacebookForm.getCode(),LifeUniConstant.STATUS_PENDING).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found student", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        Account accountByEmail = accountRepository.findAccountByEmail(registerProfileFacebookForm.getEmail());
        if (accountByEmail != null && !Objects.equals(student.getId(), accountByEmail.getId())) {
            throw new BadRequestException("email already exists", ErrorCode.ACCOUNT_ERROR_EMAIL_EXIST);
        }
        checkExistPhone(registerProfileFacebookForm.getPhone());
        Account account = student.getAccount();
        account.setPhone(registerProfileFacebookForm.getPhone());
        account.setEmail(registerProfileFacebookForm.getEmail());
        account.setPassword(passwordEncoder.encode(registerProfileFacebookForm.getPassword()));
        account.setStatus(LifeUniConstant.STATUS_ACTIVE);

        studentMapper.updateFromRegisterProfileFacebookFormToStudentEntity(registerProfileFacebookForm, student);
        student.setStatus(LifeUniConstant.STATUS_ACTIVE);

        accountRepository.save(account);
        studentRepository.save(student);
        if(StringUtils.isNoneBlank(registerProfileFacebookForm.getReferralCode())){
            createReferralSellerLog(registerProfileFacebookForm.getReferralCode(),student);
        }
        apiMessageDto.setMessage("register profile success");
        apiMessageDto.setData(oauth2JWTTokenService.getAccessTokenForStudent(account.getPhone()));
        return apiMessageDto;
    }

    void updateAvatarFromInternetToLifeUniMedia(String url, String accessToken, Long accountId){
        if(StringUtils.isNoneBlank(url)){
            try {
                ApiMessageDto<UploadFileDto> uploadFileDto = uploadAvatarFromUrl(LifeUniConstant.FILE_TYPE_AVATAR,url,accessToken,accountId);
                accountRepository.updateAvatarByAccountId(uploadFileDto.getData().getFilePath(),accountId);
            }catch (Exception e){
                log.error("Fail to upload avatar"+ e.getMessage());
            }
        }
    }
    public ApiMessageDto<UploadFileDto> uploadAvatarFromUrl(String type, String url, String accessToken, Long accountId) {
        UploadFileFromUrlForm form = new UploadFileFromUrlForm();
        form.setUrl(url);
        form.setAccountId(accountId);
        form.setType(type);
        return feignLifeUniMediaService.uploadImageFromUrl(form,"Bearer " + accessToken);
    }

    @Transactional
    @PostMapping(value = "/google/register-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<OAuth2AccessToken> loginByGoogle(@Valid @RequestBody RegisterProfileGoogleForm registerProfileGoogleForm, BindingResult bindingResult) {
        ApiMessageDto<OAuth2AccessToken> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findFirstByIsSellerAndAccount_GoogleIdAndAccount_ResetPwdCodeAndAccount_Status(LifeUniConstant.STUDENT_IS_NOT_SELLER,
                registerProfileGoogleForm.getPlatformUserId(),registerProfileGoogleForm.getCode(),LifeUniConstant.STATUS_PENDING).orElse(null);
        if(student == null){
            throw new NotFoundException("Not found student", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        checkExistPhone(registerProfileGoogleForm.getPhone());
        Account account = student.getAccount();
        account.setPhone(registerProfileGoogleForm.getPhone());
        account.setPassword(passwordEncoder.encode(registerProfileGoogleForm.getPassword()));
        account.setStatus(LifeUniConstant.STATUS_ACTIVE);

        studentMapper.updateFromRegisterProfileGoogleFormToStudentEntity(registerProfileGoogleForm,student);
        student.setStatus(LifeUniConstant.STATUS_ACTIVE);
        
        accountRepository.save(account);
        studentRepository.save(student);
        if(StringUtils.isNoneBlank(registerProfileGoogleForm.getReferralCode())){
            createReferralSellerLog(registerProfileGoogleForm.getReferralCode(),student);
        }
        apiMessageDto.setMessage("register profile success");
        apiMessageDto.setData(oauth2JWTTokenService.getAccessTokenForStudent(account.getPhone()));
        return apiMessageDto;
    }
}
