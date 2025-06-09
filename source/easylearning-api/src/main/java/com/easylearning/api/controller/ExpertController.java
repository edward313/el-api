package com.easylearning.api.controller;


import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.expert.ExpertDto;
import com.easylearning.api.dto.expert.ExpertInfoDto;
import com.easylearning.api.dto.expert.MyStudentDto;
import com.easylearning.api.dto.registration.RegistrationDto;
import com.easylearning.api.dto.revenueShare.MyRevenueDto;
import com.easylearning.api.dto.revenueShare.MyRevenueDtoInterface;
import com.easylearning.api.dto.revenueShare.RevenueShareDto;
import com.easylearning.api.dto.student.StudentAdminDto;
import com.easylearning.api.dto.version.VersionDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.expert.CreateExpertForm;
import com.easylearning.api.form.expert.UpdateExpertForm;
import com.easylearning.api.form.expert.UpdateExpertProfileForm;
import com.easylearning.api.form.expert.UpdateSortExpertForm;
import com.easylearning.api.mapper.*;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.*;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/expert")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ExpertController extends ABasicController {

    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private ExpertMapper expertMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReferralExpertLogRepository referralExpertLogRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseTransactionRepository courseTransactionRepository;

    @Autowired
    private CourseRetailRepository courseRetailRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MonthlyPeriodDetailRepository monthlyPeriodDetailRepository;

    @Autowired
    private RevenueShareRepository revenueShareRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CategoryHomeRepository categoryHomeRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private CourseComboDetailRepository courseComboDetailRepository;
    @Autowired
    private CourseComboDetailVersionRepository courseComboDetailVersionRepository;
    @Autowired
    private CourseReviewHistoryRepository courseReviewHistoryRepository;
    @Autowired
    private LessonVersioningRepository lessonVersioningRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private CourseVersioningRepository courseVersioningRepository;
    @Autowired
    private VersionMapper versionMapper;
    @Autowired
    private RegistrationMapper registrationMapper;


    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EX_V')")
    public ApiMessageDto<ExpertDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<ExpertDto> apiMessageDto = new ApiMessageDto<>();
        Expert expert = expertRepository.findById(id).orElse(null);
        if (expert == null) {
            throw new NotFoundException("Expert is not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }

        apiMessageDto.setData(expertMapper.fromEntityToExpertDto(expert));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get expert success");
        return apiMessageDto;
    }

    @Transactional
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EX_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateExpertForm createExpertForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        createExpertAccount(createExpertForm);
        apiMessageDto.setMessage("Create expert success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EX_L')")
    public ApiMessageDto<ResponseListDto<List<ExpertDto>>> list(ExpertCriteria expertCriteria) {
        ApiMessageDto<ResponseListDto<List<ExpertDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        List<Expert> expertList = expertRepository.findAll(expertCriteria.getSpecification());
        ResponseListDto<List<ExpertDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(expertMapper.fromEntityToExpertDtoList(expertList));

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list experts success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<ExpertDto>>> autoComplete(ExpertCriteria expertCriteria) {
        ApiMessageDto<ResponseListDto<List<ExpertDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();

        Pageable pageable = PageRequest.of(0,10);
        expertCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);

        Page<Expert> developers = expertRepository.findAll(expertCriteria.getSpecification(), pageable);
        ResponseListDto<List<ExpertDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(expertMapper.fromEntityToExpertDtoAutoCompleteList(developers.getContent()));
        responseListObj.setTotalPages(developers.getTotalPages());
        responseListObj.setTotalElements(developers.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list expert auto-complete success");
        return responseListObjApiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EX_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateExpertForm updateExpertForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Expert expert = expertRepository.findById(updateExpertForm.getId()).orElse(null);
        if (expert == null) {
            throw new NotFoundException("Expert is not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        if (!expert.getAccount().getPhone().equalsIgnoreCase(updateExpertForm.getPhone())) {
            Account account = accountRepository.findAccountByPhoneAndKind(updateExpertForm.getPhone(),LifeUniConstant.USER_KIND_EXPERT);
            if (account != null) {
                throw new BadRequestException("Phone is existed",ErrorCode.EXPERT_ERROR_EXIST);
            }
        }
        Account existedAccount = expert.getAccount();
        if (StringUtils.isNoneBlank(updateExpertForm.getPassword())) {
            existedAccount.setPassword(passwordEncoder.encode(updateExpertForm.getPassword()));
        }
        if(Boolean.TRUE.equals(updateExpertForm.getIsOutstanding())){
            if(!Objects.equals(updateExpertForm.getIsOutstanding(),expert.getIsOutstanding())){
                Expert outstandingExpert = expertRepository.findFirstByIsOutstandingOrderByOrderingDesc(true);
                if(outstandingExpert != null && outstandingExpert.getOrdering() != null){
                    updateExpertForm.setOrdering(outstandingExpert.getOrdering() + 1);
                }else {
                    updateExpertForm.setOrdering(1);
                }
            }
        }
        expertMapper.fromUpdateExpertFormToEntity(updateExpertForm, expert);
        //set nation
        setNation(updateExpertForm.getWardId()
                ,updateExpertForm.getDistrictId(),
                updateExpertForm.getProvinceId(),expert);
        expertRepository.save(expert);
        accountMapper.fromUpdateExpertFormToEntity(updateExpertForm,existedAccount);
        accountRepository.save(existedAccount);

        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Update expert success.");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EX_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Expert expert = expertRepository.findById(id).orElse(null);
        if (expert == null) {
            throw new NotFoundException("Expert not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        if(Boolean.TRUE.equals(expert.getIsSystemExpert())){
            throw new BadRequestException("Cannot delete System Expert",ErrorCode.EXPERT_ERROR_CANNOT_DELETE_SYSTEM_EXPERT);
        }
        deleteFilesFromListUrlDocument(lessonRepository.getAllUrlDocumentByExpertId(id));
        courseComboDetailVersionRepository.deleteAllByAccountsId(id);
        courseReviewHistoryRepository.deleteAllByAccountId(id);
        lessonVersioningRepository.deleteAllLessonByExpertId(id);
        courseVersioningRepository.deleteAllByExpertId(id);
        
        registrationRepository.deleteAllByAccountId(id);
        reviewRepository.deleteAllByAccountId(id);
        revenueShareRepository.deleteAllRevenueShareByAccountId(id);
        monthlyPeriodDetailRepository.deleteAllByAccountId(id);
        categoryHomeRepository.deleteByAccount(id);
        courseTransactionRepository.deleteAllByAccountId(id);
        cartItemRepository.deleteAllByAccountId(id);
        courseRetailRepository.deleteAllByAccountId(id);
        referralExpertLogRepository.deleteAllByExpertIdOrRefExpertId(id);
        lessonRepository.deleteAllLessonByExpertId(id);
        courseComboDetailRepository.deleteAllByAccountsId(id);
        courseRepository.deleteAllByExpertId(id);
        walletRepository.deleteAllByAccountId(id);
        expertRepository.deleteById(id);
        versionRepository.deleteAllByAccountId(id);
        accountRepository.deleteById(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete expert success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ExpertDto> getProfile()
    {
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed get");
        }
        ApiMessageDto<ExpertDto> apiMessageDto = new ApiMessageDto<>();

        Expert expert = expertRepository.findById(getCurrentUser()).orElse(null);
        if (expert == null) {
            throw new NotFoundException("Expert not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        if(expert.getIsSystemExpert() == null){
            expert.setIsSystemExpert(false);
        }
        ExpertDto expertDto = expertMapper.fromEntityToExpertDtoForProfile(expert);

        ReferralExpertLog referralExpertLog = referralExpertLogRepository.findFirstByExpertId(expert.getId());
        if(referralExpertLog != null && referralExpertLog.getRefExpert() != null) {
            expertDto.setReferralCode(referralExpertLog.getRefExpert().getReferralCode());
        }

        apiMessageDto.setData(expertDto);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get expert profile success");
        return apiMessageDto;
    }
    @PutMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> updateProfile(@Valid @RequestBody UpdateExpertProfileForm updateExpertProfileForm , BindingResult bindingResult)
    {
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed update");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Expert expert = expertRepository.findById(getCurrentUser()).orElse(null);
        if (expert==null)
        {
            throw new NotFoundException("Expert not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        if (!passwordEncoder.matches(updateExpertProfileForm.getOldPassword(),expert.getAccount().getPassword()))
        {
            throw new BadRequestException("Expert password is incorrect",ErrorCode.EXPERT_ERROR_WRONG_PASSWORD);
        }

        if (StringUtils.isNoneBlank(updateExpertProfileForm.getNewPassword()))
        {
            if(Objects.equals(updateExpertProfileForm.getNewPassword(), updateExpertProfileForm.getOldPassword())){
                throw new BadRequestException("newPassword match with current password",ErrorCode.ACCOUNT_ERROR_NEW_PASSWORD_MATCH_CURRENT_PASSWORD);
            }
            expert.getAccount().setPassword(passwordEncoder.encode(updateExpertProfileForm.getNewPassword()));
        }
        expertMapper.fromUpdateExpertProfileFormToEntity(updateExpertProfileForm,expert);
        //set nation
        setNation(updateExpertProfileForm.getWardId()
                ,updateExpertProfileForm.getDistrictId(),
                updateExpertProfileForm.getProvinceId(),expert);
        expertRepository.save(expert);
        apiMessageDto.setMessage("update expert profile success");
        return apiMessageDto;

    }
    @GetMapping(value = "/my-course", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<CourseDto>>> myCourse(CourseVersioningCriteria criteria, Pageable pageable) {
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed get");
        }
        List<Long> lockedIds = courseRepository.findAllIdByExpertIdAndStatus(getCurrentUser(), LifeUniConstant.STATUS_LOCK);
        criteria.setOrderByCreatedDate(true);
        criteria.setIsMaxCourseVersioning(true);
        criteria.setExpertId(getCurrentUser());
        criteria.setIgnoreVisualStatus(Arrays.asList(LifeUniConstant.STATUS_DELETE));
        criteria.setIgnoreStatus(Arrays.asList(LifeUniConstant.STATUS_DELETE));
        ApiMessageDto<ResponseListDto<List<CourseDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<CourseDto>> responseListObj = new ResponseListDto<>();

        Page<CourseVersioning> courseVersionings = courseVersioningRepository.findAll(criteria.getSpecification(), pageable);
        List<CourseDto> courseDtoList = courseMapper.fromCourseVersioningToCourseDtoForMyCourseList(courseVersionings.getContent());
        for(CourseDto c: courseDtoList){
            Version version = versionRepository.findHighestVersionByCourseId(c.getId());
            VersionDto versionDto = versionMapper.fromEntityToVersionDtoForMyCourse(version);
            if(Objects.equals(version.getState(), LifeUniConstant.VERSION_STATE_REJECT)){
                versionDto.setReasonReject(getReasonFromCourseReviewHistory(version.getId()));
            }
            c.setVersion(versionDto);
            if(lockedIds.contains(c.getId())){
                c.setStatus(LifeUniConstant.STATUS_LOCK);
            }
        }
        responseListObj.setContent(courseDtoList);
        responseListObj.setTotalPages(courseVersionings.getTotalPages());
        responseListObj.setTotalElements(courseVersionings.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/my-seller", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<StudentAdminDto>>> mySeller(StudentCriteria studentCriteria, Pageable pageable) {
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed get");
        }
        studentCriteria.setExpertId(getCurrentUser());

        ApiMessageDto<ResponseListDto<List<StudentAdminDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Student> studentList = studentRepository.findAll(studentCriteria.getSpecification(), pageable);
        ResponseListDto<List<StudentAdminDto>> responseListObj = new ResponseListDto<>();
        List<StudentAdminDto> studentDtoList = studentMapper.fromStudentListToStudentAdminDtoList(studentList.getContent());

        responseListObj.setContent(studentDtoList);
        responseListObj.setTotalPages(studentList.getTotalPages());
        responseListObj.setTotalElements(studentList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get sellers of expert success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/my-refer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<ExpertDto>>> myRefer(ExpertCriteria expertCriteria, Pageable pageable) {
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed get");
        }
        expertCriteria.setReferralExpertId(getCurrentUser());

        ApiMessageDto<ResponseListDto<List<ExpertDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Expert> expertList = expertRepository.findAll(expertCriteria.getSpecification(), pageable);
        ResponseListDto<List<ExpertDto>> responseListObj = new ResponseListDto<>();
        List<ExpertDto> expertDtoList = expertMapper.fromEntityToExpertDtoList(expertList.getContent());

        responseListObj.setContent(expertDtoList);
        responseListObj.setTotalPages(expertList.getTotalPages());
        responseListObj.setTotalElements(expertList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get refer of expert success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ExpertInfoDto> clientGet(@PathVariable("id") Long id) {
        ApiMessageDto<ExpertInfoDto> apiMessageDto = new ApiMessageDto<>();
        Expert expert = expertRepository.findByIdAndIsSystemExpert(id,false);
        if (expert == null) {
            throw new NotFoundException("Expert not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        apiMessageDto.setResult(true);
        apiMessageDto.setData(expertMapper.fromEntityToExpertInfoDto(expert));
        apiMessageDto.setMessage("Get expert success");
        return apiMessageDto;
    }
    @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<ExpertDto>>> clientList(ExpertCriteria expertCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<ExpertDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        expertCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        expertCriteria.setIsSystemExpert(false);

        Page<Expert> expertList = expertRepository.findAll(expertCriteria.getSpecification(), pageable);
        ResponseListDto<List<ExpertDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(expertMapper.fromEntityToExpertDtoForClientList(expertList.getContent()));
        responseListObj.setTotalPages(expertList.getTotalPages());
        responseListObj.setTotalElements(expertList.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get client list expert success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/my-revenue", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<MyRevenueDto> myRevenue() {
        ApiMessageDto<MyRevenueDto> apiMessageDto = new ApiMessageDto<>();
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed get");
        }
        Expert expert = expertRepository.findById(getCurrentUser()).orElse(null);
        if (expert == null) {
            throw new NotFoundException("Expert not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
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
        apiMessageDto.setMessage("Get expert revenue success!!");
        return apiMessageDto;
    }
    @GetMapping(value = "/my-transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<RevenueShareDto>>> myTransaction(RevenueShareCriteria revenueShareCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<RevenueShareDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed get");
        }
        Expert expert = expertRepository.findById(getCurrentUser()).orElse(null);
        if (expert == null) {
            throw new NotFoundException("Expert not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        ResponseListDto<List<RevenueShareDto>> responseListObj = new ResponseListDto<>();
        revenueShareCriteria.setExpertId(getCurrentUser());
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
    @PutMapping(value = "/update-sort",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EX_US')")
    public ApiMessageDto<String> updateSort(@Valid @RequestBody List<UpdateSortExpertForm> updateSortExpertForms, BindingResult bindingResult){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        List<Long> Idlist = updateSortExpertForms
                .stream()
                .map(UpdateSortExpertForm::getId)
                .collect(Collectors.toList());
        List<Expert> experts = expertRepository.findAllById(Idlist);
        for (Expert expert:experts){
            for (UpdateSortExpertForm updateSortExpertForm:updateSortExpertForms){
                if (expert.getId().equals(updateSortExpertForm.getId())){
                    expert.setOrdering(updateSortExpertForm.getOrdering());
                    break;
                }
            }
        }
        expertRepository.saveAll(experts);
        apiMessageDto.setMessage("Update sort success");
        return apiMessageDto;
    }
    @Transactional
    @DeleteMapping(value = "/delete-expert-data/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EX_D')")
    public ApiMessageDto<String> deleteExpertData(@PathVariable("id") Long id) {
        if (!isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed delete");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Expert expert = expertRepository.findById(id).orElse(null);
        if (expert == null) {
            throw new NotFoundException("Expert not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        if(Boolean.TRUE.equals(expert.getIsSystemExpert())){
            throw new BadRequestException("Cannot delete System Expert",ErrorCode.EXPERT_ERROR_CANNOT_DELETE_SYSTEM_EXPERT);
        }
        expert.setTotalLessonTime(0L);
        expert.setTotalStudent(0);
        expert.setTotalCourse(0);
        expertRepository.save(expert);
        deleteExpertFolder(id);
        registrationRepository.deleteAllByAccountId(id);
        reviewRepository.deleteAllByAccountId(id);
        revenueShareRepository.deleteAllRevenueShareByAccountId(id);
        monthlyPeriodDetailRepository.deleteAllByAccountId(id);
        categoryHomeRepository.deleteByAccount(id);
        courseTransactionRepository.deleteAllByAccountId(id);
        cartItemRepository.deleteAllByAccountId(id);
        courseRetailRepository.deleteAllByAccountId(id);
        referralExpertLogRepository.deleteAllByExpertIdOrRefExpertId(id);
        lessonRepository.deleteAllLessonByExpertId(id);
        courseComboDetailRepository.deleteAllByAccountsId(id);
        courseRepository.deleteAllByExpertId(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete expert success");
        return apiMessageDto;
    }
    @GetMapping(value = "/my-student", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EX_MS')")
    public ApiMessageDto<ResponseListDto<List<MyStudentDto>>> myStudent(RegistrationCriteria criteria, Pageable pageable) {
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed get");
        }
        criteria.setExpertId(getCurrentUser());

        ApiMessageDto<ResponseListDto<List<MyStudentDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<MyStudentDto>> responseListObj = new ResponseListDto<>();

        Page<MyStudentDto> myStudents = studentRepository.findAllStudentByExpertId(criteria, pageable);
        responseListObj.setContent(myStudents.getContent());
        responseListObj.setTotalPages(myStudents.getTotalPages());
        responseListObj.setTotalElements(myStudents.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list registration success");
        return responseListObjApiMessageDto;
    }
}
