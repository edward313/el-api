package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.course.SellerCourseDto;
import com.easylearning.api.dto.revenueShare.MyRevenueDto;
import com.easylearning.api.dto.revenueShare.MyRevenueDtoInterface;
import com.easylearning.api.dto.revenueShare.RevenueShareDto;
import com.easylearning.api.dto.student.StudentAdminDto;
import com.easylearning.api.dto.student.StudentDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.student.CheckCouponForm;
import com.easylearning.api.form.student.UpdateSellerForm;
import com.easylearning.api.form.student.UpdateSellerProfileForm;
import com.easylearning.api.mapper.AccountMapper;
import com.easylearning.api.mapper.CourseMapper;
import com.easylearning.api.mapper.StudentMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.CourseCriteria;
import com.easylearning.api.model.criteria.RevenueShareCriteria;
import com.easylearning.api.model.criteria.StudentCriteria;
import com.easylearning.api.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/seller")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class SellerController extends ABasicController {
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
    private ReferralSellerLogRepository referralSellerLogRepository;
    @Autowired
    private SellerCodeTrackingRepository sellerCodeTrackingRepository;
    @Autowired
    private CourseRetailRepository courseRetailRepository;
    @Autowired
    private CourseTransactionRepository courseTransactionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private MonthlyPeriodDetailRepository monthlyPeriodDetailRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RevenueShareRepository revenueShareRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private CompletionRepository completionRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private ExpertRepository expertRepository;

    @PostMapping("/check-coupon")
    @PreAuthorize("hasRole('SEL_CC')")
    public ApiMessageDto<Boolean> checkCoupon(@Valid @RequestBody CheckCouponForm checkCouponForm) {
        ApiMessageDto<Boolean> apiMessageDto = new ApiMessageDto<>();
        Student seller = studentRepository.findByReferralCodeAndIsSellerAndStatus(checkCouponForm.getCouponCode(),
                LifeUniConstant.STUDENT_IS_SELLER,LifeUniConstant.STATUS_ACTIVE).orElse(null);
        Student student = studentRepository.findById(getCurrentUser()).orElse(null);
        if (student == null) {
            throw new BadRequestException("Student not found", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }

        apiMessageDto.setData(Boolean.TRUE);
        if (seller == null || (student.getReferralCode() != null && StringUtils.equals(student.getReferralCode(), checkCouponForm.getCouponCode()))) {
            apiMessageDto.setData(Boolean.FALSE);
        }

        apiMessageDto.setMessage("Check coupon successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SEL_V')")
    public ApiMessageDto<StudentAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<StudentAdminDto> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findByIdAndIsSeller(id, true).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found seller", ErrorCode.SELLER_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(studentMapper.fromEntityToStudentAdminDto(student));
        apiMessageDto.setMessage("Get seller success");
        return apiMessageDto;
    }
    @Transactional
    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasRole('SEL_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findByIdAndIsSeller(id, true).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found seller", ErrorCode.SELLER_ERROR_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(student.getIsSystemSeller())) {
            throw new BadRequestException("Cannot delete System Seller", ErrorCode.SELLER_ERROR_CANNOT_DELETE_SYSTEM_SELLER);
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
        apiMessageDto.setMessage("Delete seller success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SEL_L')")
    public ApiMessageDto<ResponseListDto<List<StudentAdminDto>>> getList(StudentCriteria studentCriteria, Pageable pageable) {

        ApiMessageDto<ResponseListDto<List<StudentAdminDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<StudentAdminDto>> responseListDto = new ResponseListDto<>();
        studentCriteria.setIsSeller(true);
        Page<Student> students = studentRepository.findAll(studentCriteria.getSpecification(), pageable);
        responseListDto.setContent(studentMapper.fromStudentListToStudentAdminDtoList(students.getContent()));
        responseListDto.setTotalPages(students.getTotalPages());
        responseListDto.setTotalElements(students.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list seller success");
        return apiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<StudentDto>>> ListAutoComplete(StudentCriteria studentCriteria) {

        ApiMessageDto<ResponseListDto<List<StudentDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<StudentDto>> responseListDto = new ResponseListDto<>();
        Pageable pageable = PageRequest.of(0, 10);
        studentCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        studentCriteria.setIsSeller(true);
        Page<Student> students = studentRepository.findAll(studentCriteria.getSpecification(), pageable);
        responseListDto.setContent(studentMapper.fromStudentListToStudentDtoListAutocomplete(students.getContent()));
        responseListDto.setTotalPages(students.getTotalPages());
        responseListDto.setTotalElements(students.getTotalElements());
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("get seller success");
        return apiMessageDto;
    }

    @Transactional
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SEL_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateSellerForm updateSellerForm, BindingResult bindingResult) {

        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findByIdAndIsSeller(updateSellerForm.getId(), true).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found seller", ErrorCode.SELLER_ERROR_NOT_FOUND);
        }
        if (StringUtils.isNoneBlank(updateSellerForm.getPhone()) && student.getAccount().getPhone() != null && !student.getAccount().getPhone().equalsIgnoreCase(updateSellerForm.getPhone())) {
            Account account = accountRepository.findAccountByPhoneAndKind(updateSellerForm.getPhone(),LifeUniConstant.USER_KIND_STUDENT);
            if (account != null) {
                throw new BadRequestException("Phone is existed", ErrorCode.ACCOUNT_ERROR_PHONE_EXIST);
            }
        }
        Account account = student.getAccount();
        if (StringUtils.isNoneBlank(updateSellerForm.getPassword())) {
            account.setPassword(passwordEncoder.encode(updateSellerForm.getPassword()));
        }
        student = setNationToStudent(student, updateSellerForm.getWardId(), updateSellerForm.getProvinceId(), updateSellerForm.getDistrictId());
        studentMapper.updateFromUpdateSellerFormToStudentEntity(updateSellerForm, student);
        studentRepository.save(student);
        accountMapper.fromUpdateSellerFormToEntity(updateSellerForm, account);
        accountRepository.save(account);
        apiMessageDto.setMessage("update seller success");
        return apiMessageDto;
    }

    private Student setNationToStudent(Student student, Long wardId, Long provinceId, Long districtId) {
        if (wardId != null) {
            student.setWard(getNationByIdAndKind(wardId, LifeUniConstant.NATION_KIND_COMMUNE));
        }
        if (provinceId != null) {
            student.setProvince(getNationByIdAndKind(provinceId, LifeUniConstant.NATION_KIND_PROVINCE));
        }
        if (districtId != null) {
            student.setDistrict(getNationByIdAndKind(districtId, LifeUniConstant.NATION_KIND_DISTRICT));
        }
        return student;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<StudentDto> getMyProfile() {
        if (!isSeller()) {
            throw new UnauthorizationException("Not allowed get");
        }
        ApiMessageDto<StudentDto> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findByIdAndIsSeller(getCurrentUser(), LifeUniConstant.STUDENT_IS_SELLER).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found seller", ErrorCode.SELLER_ERROR_NOT_FOUND);
        }
        StudentDto studentDto = studentMapper.fromStudentToDtoForProfile(student);

        ReferralSellerLog referralSellerLog = referralSellerLogRepository.findFirstByStudentId(student.getId());
        studentDto.setReferralCode(referralSellerLog != null ? referralSellerLog.getRefStudent().getReferralCode() : null);

        apiMessageDto.setData(studentDto);
        apiMessageDto.setMessage("get seller profile success");
        return apiMessageDto;
    }

    @Transactional
    @PutMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateSellerProfileForm updateSellerProfileForm, BindingResult bindingResult) {
        if (!isStudent()) {
            throw new UnauthorizationException("Not allowed update");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Student student = studentRepository.findByIdAndIsSeller(getCurrentUser(), LifeUniConstant.STUDENT_IS_SELLER).orElse(null);
        if (student == null) {
            throw new NotFoundException("Not found seller", ErrorCode.SELLER_ERROR_NOT_FOUND);
        }
        student = setNationToStudent(student, updateSellerProfileForm.getWardId(), updateSellerProfileForm.getProvinceId(), updateSellerProfileForm.getDistrictId());
        Account account = student.getAccount();
        if (!passwordEncoder.matches(updateSellerProfileForm.getOldPassword(), student.getAccount().getPassword())) {
            throw new BadRequestException("password is incorrect", ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
        }
        if (StringUtils.isNoneBlank(updateSellerProfileForm.getNewPassword())) {
            if(Objects.equals(updateSellerProfileForm.getNewPassword(), updateSellerProfileForm.getOldPassword())){
                throw new BadRequestException("newPassword match with current password",ErrorCode.ACCOUNT_ERROR_NEW_PASSWORD_MATCH_CURRENT_PASSWORD);
            }
            account.setPassword(passwordEncoder.encode(updateSellerProfileForm.getNewPassword()));
        }
        if(StringUtils.isNoneBlank(updateSellerProfileForm.getReferralCode())) {
            validationSellCode(student, updateSellerProfileForm.getReferralCode());
            createReferralSellerLog(updateSellerProfileForm.getReferralCode(),student);
        }
        studentMapper.updateFromUpdateSellerProfileFormToStudentEntity(updateSellerProfileForm, student);
        studentRepository.save(student);
        accountMapper.fromUpdateSellerProfileFormToEntity(updateSellerProfileForm, account);
        accountRepository.save(account);
        apiMessageDto.setMessage("update sell profile success");
        return apiMessageDto;
    }

    @GetMapping(value = "/my-course", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<SellerCourseDto> myCourse(CourseCriteria courseCriteria, Pageable pageable) {
        if (!isSeller()) {
            throw new UnauthorizationException("Not allowed get");
        }
        Student seller = studentRepository.findById(getCurrentUser()).orElse(null);
        if (seller == null) {
            throw new NotFoundException("Not found seller", ErrorCode.SELLER_ERROR_NOT_FOUND);
        }
        courseCriteria.setSellerId(getCurrentUser());
        courseCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        ApiMessageDto<SellerCourseDto> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<CourseDto>> responseListDto = new ResponseListDto<>();
        Page<Course> courseList = courseRepository.findAll(courseCriteria.getSpecification(), pageable);
        responseListDto.setContent(courseMapper.fromEntityToCourseDtoForMyCourseList(courseList.getContent()));
        responseListDto.setTotalPages(courseList.getTotalPages());
        responseListDto.setTotalElements(courseList.getTotalElements());
        SellerCourseDto sellerCourseDto = new SellerCourseDto();
        sellerCourseDto.setCourses(responseListDto);
        sellerCourseDto.setReferralCode(seller.getReferralCode());
        apiMessageDto.setData(sellerCourseDto);
        apiMessageDto.setMessage("Get seller success");
        return apiMessageDto;
    }

    @GetMapping(value = "/my-refer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<StudentAdminDto>>> myRefer(StudentCriteria studentCriteria, Pageable pageable) {
        if (!isSeller()) {
            throw new UnauthorizationException("Not allowed get");
        }
        studentCriteria.setReferralSellerId(getCurrentUser());

        ApiMessageDto<ResponseListDto<List<StudentAdminDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Student> studentList = studentRepository.findAll(studentCriteria.getSpecification(), pageable);
        ResponseListDto<List<StudentAdminDto>> responseListObj = new ResponseListDto<>();
        List<StudentAdminDto> studentDtoList = studentMapper.fromStudentListToStudentAdminDtoList(studentList.getContent());

        responseListObj.setContent(studentDtoList);
        responseListObj.setTotalPages(studentList.getTotalPages());
        responseListObj.setTotalElements(studentList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/my-revenue", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<MyRevenueDto> myRevenue() {
        ApiMessageDto<MyRevenueDto> apiMessageDto = new ApiMessageDto<>();
        if (!isSeller()) {
            throw new UnauthorizationException("Not allowed get");
        }
        Student seller = studentRepository.findByIdAndIsSeller(getCurrentUser(), LifeUniConstant.STUDENT_IS_SELLER).orElse(null);
        if (seller == null) {
            throw new NotFoundException("Not found seller", ErrorCode.SELLER_ERROR_NOT_FOUND);
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime threeMonthsAgoDateTime = currentDateTime.minusMonths(3);
        Date threeMonthsAgoDate = Date.from(threeMonthsAgoDateTime.atZone(ZoneId.systemDefault()).toInstant());
        MyRevenueDtoInterface myRevenueDtoInterface = revenueShareRepository.getMyRevenueDto(getCurrentUser(), LifeUniConstant.WALLET_TRANSACTION_KIND_WITH_DRAW, threeMonthsAgoDate, LifeUniConstant.REVENUE_SHARE_PAYOUT_STATUS_UNPAID);
        MyRevenueDto myRevenueDto = new MyRevenueDto();
        myRevenueDto.setTotalMoney(myRevenueDtoInterface.getTotalMoney());
        myRevenueDto.setTotalPayoutMoney(myRevenueDtoInterface.getTotalPayoutMoney());

        apiMessageDto.setResult(true);
        apiMessageDto.setData(myRevenueDto);
        apiMessageDto.setMessage("Get seller revenue success!!");
        return apiMessageDto;
    }
    @GetMapping(value = "/my-transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<RevenueShareDto>>> myTransaction(RevenueShareCriteria revenueShareCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<RevenueShareDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        if(!isSeller()){
            throw new UnauthorizationException("Not allowed get");
        }
        Student seller = studentRepository.findByIdAndIsSeller(getCurrentUser(), LifeUniConstant.STUDENT_IS_SELLER).orElse(null);
        if (seller == null) {
            throw new NotFoundException("Not found seller", ErrorCode.SELLER_ERROR_NOT_FOUND);
        }
        ResponseListDto<List<RevenueShareDto>> responseListObj = new ResponseListDto<>();
        revenueShareCriteria.setSellerId(getCurrentUser());
        revenueShareCriteria.setIsOrderByCreatedDate(true);
        revenueShareCriteria.setGetLastThreeMonths(true);
        Page<RevenueShare> revenueSharePages = revenueShareRepository.findAll(revenueShareCriteria.getSpecification(),pageable);
        responseListObj.setContent(getRevenueShareDtoList(revenueSharePages.getContent()));
        responseListObj.setTotalPages(revenueSharePages.getTotalPages());
        responseListObj.setTotalElements(revenueSharePages.getTotalElements());

        responseListObjApiMessageDto.setResult(true);
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get my transaction success!!");
        return responseListObjApiMessageDto;
    }
}
