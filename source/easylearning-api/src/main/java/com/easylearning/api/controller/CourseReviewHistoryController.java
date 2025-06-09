package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.courseReviewHistory.CourseReviewHistoryDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.version.*;
import com.easylearning.api.mapper.CourseReviewHistoryMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.CourseReviewHistoryCriteria;
import com.easylearning.api.repository.*;
import com.easylearning.api.service.rabbitMQ.ApproveVersionService;
import com.easylearning.api.service.rabbitMQ.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/course-review-history")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CourseReviewHistoryController extends ABasicController {

    @Autowired
    private CourseReviewHistoryRepository courseReviewHistoryRepository;

    @Autowired
    private CourseReviewHistoryMapper courseReviewHistoryMapper;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private LessonVersioningRepository lessonVersioningRepository;

    @Autowired
    private CourseVersioningRepository courseVersioningRepository;
    @Autowired
    private ApproveVersionService approveVersionService;
    @Autowired
    private NotificationService notificationService;


    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CHR_V')")
    public ApiMessageDto<CourseReviewHistoryDto> get(@PathVariable("id") Long id) {

        ApiMessageDto<CourseReviewHistoryDto> apiMessageDto = new ApiMessageDto<>();
        CourseReviewHistory courseReviewHistory = courseReviewHistoryRepository.findById(id).orElse(null);

        if (courseReviewHistory == null) {
            throw new NotFoundException("Course Review History not found", ErrorCode.COURSE_REVIEW_HISTORY_ERROR_NOT_FOUND);
        }

        apiMessageDto.setData(courseReviewHistoryMapper.fromEntityToCourseReviewHistoryDto(courseReviewHistory));
        apiMessageDto.setMessage("Get Course Review History success");

        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CHR_L')")
    public ApiMessageDto<ResponseListDto<List<CourseReviewHistoryDto>>> list(CourseReviewHistoryCriteria criteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<CourseReviewHistoryDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<CourseReviewHistory> courseReviewHistoryList = courseReviewHistoryRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<CourseReviewHistoryDto>> responseListObj = new ResponseListDto<>();

        List<CourseReviewHistoryDto> courseReviewHistoryDtos = courseReviewHistoryMapper.fromEntityToCourseReviewHistoryDtoList(courseReviewHistoryList.getContent());
        for (CourseReviewHistoryDto crh: courseReviewHistoryDtos){
            CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningUpToVersionCode(crh.getVersion().getCourseId(),crh.getVersion().getVersionCode());
            if(courseVersioning!=null){
                Expert expert = courseVersioning.getExpert();
                crh.setExpertName(expert.getAccount().getFullName());
                crh.setCourseName(courseVersioning.getName());
            }
        }
        responseListObj.setContent(courseReviewHistoryDtos);
        responseListObj.setTotalPages(courseReviewHistoryList.getTotalPages());
        responseListObj.setTotalElements(courseReviewHistoryList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get List Course Review History success");

        return responseListObjApiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CRH_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Version version = versionRepository.findHighestVersionByCourseId(id);
        if(version == null){
            throw new NotFoundException("Version not found",ErrorCode.VERSION_ERROR_NOT_FOUND);
        }
        if(!version.getState().equals(LifeUniConstant.VERSION_STATE_INIT)
                && !version.getState().equals(LifeUniConstant.VERSION_STATE_APPROVE)){
            throw new BadRequestException("Just allow delete when version state init or approve ",ErrorCode.VERSION_ERROR_NOT_INIT_OR_APPROVE);
        }
        CourseReviewHistory courseReviewHistory = courseReviewHistoryRepository.findById(id).orElse(null);
        if (courseReviewHistory == null) {
            throw new NotFoundException("Course Review History is not found", ErrorCode.COURSE_REVIEW_HISTORY_ERROR_NOT_FOUND);
        }
        courseReviewHistoryRepository.deleteById(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete Course Review History success");

        return apiMessageDto;
    }
    @Transactional
    @PutMapping(value = "/submit", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CRH_S')")
    public ApiMessageDto<String> submit(@Valid @RequestBody SubmitVersionForm submitCourseForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Version version = versionRepository.findFirstByIdAndState(submitCourseForm.getVersionId(),LifeUniConstant.VERSION_STATE_INIT).orElse(null);
        if(version == null){
            throw new BadRequestException("Version not found",ErrorCode.VERSION_ERROR_NOT_FOUND);
        }
        Course course = courseRepository.findFirstByIdAndStatusNot(version.getCourseId(),LifeUniConstant.STATUS_DELETE).orElse(null);
        if(course == null){
            throw new BadRequestException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(course.getExpert().getId(), getCurrentUser())){
            throw new BadRequestException("Just only submit yours course",ErrorCode.COURSE_ERROR_NOT_ALLOW_UPDATE_COURSE);
        }
        version.setState(LifeUniConstant.VERSION_STATE_SUBMIT);
        version.setReviewNote(submitCourseForm.getReviewNote());
        versionRepository.save(version);

        CourseReviewHistory oldReview = courseReviewHistoryRepository.findFirstByVersionIdAndState(version.getId(), LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_INIT);
        if(oldReview == null){
            // create course review history if not exist
            CourseReviewHistory courseReviewHistory = new CourseReviewHistory();
            courseReviewHistory.setVersion(version);
            courseReviewHistory.setState(LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_SUBMIT);
            courseReviewHistoryRepository.save(courseReviewHistory);
        }
        else {
            // update state if exist
            oldReview.setState(LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_SUBMIT);
            courseReviewHistoryRepository.save(oldReview);
        }


        // check video process
        List<LessonVersioning> lessonVersioningList = lessonVersioningRepository.findAllByVersionId(version.getId());
        if(!lessonVersioningList.isEmpty()){
            for(LessonVersioning lessonVersioning: lessonVersioningList){
                if(lessonVersioning.getStatus() != LifeUniConstant.STATUS_DELETE){
                    if(Objects.equals(lessonVersioning.getKind(), LifeUniConstant.LESSON_KIND_VIDEO) && !Objects.equals(lessonVersioning.getState(), LifeUniConstant.LESSON_STATE_DONE)){
                        throw new BadRequestException("Video is being processed or has failed",ErrorCode.LESSON_ERROR_VIDEO_STATE_NOT_DONE);
                    }
                }
            }
        }
        CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningByCourseId(version.getCourseId());
        if(courseVersioning.getKind().equals(LifeUniConstant.COURSE_KIND_SINGLE) && courseVersioning.getTotalLesson() == 0){
            throw new BadRequestException("Single course must contain at least one lesson",ErrorCode.COURSE_VERSIONING_VERSION_ERROR_SINGLE_COURSE_NOT_CONTAIN_LESSON);
        }
        apiMessageDto.setMessage("Submit version success!");
        return apiMessageDto;
    }
    @Transactional
    @PutMapping(value = "/cancel-submit", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CRH_CS')")
    public ApiMessageDto<String> cancelSubmit(@Valid @RequestBody CancelSubmitVersionForm cancelSubmitVersionForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Version version = versionRepository.findFirstByIdAndState(cancelSubmitVersionForm.getVersionId(),LifeUniConstant.VERSION_STATE_SUBMIT).orElse(null);
        if(version == null){
            throw new BadRequestException("Version not found",ErrorCode.VERSION_ERROR_NOT_FOUND);
        }
        Course course = courseRepository.findById(version.getCourseId()).orElse(null);
        if(course == null){
            throw new BadRequestException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(course.getExpert().getId(), getCurrentUser())){
            throw new BadRequestException("Just only cancel submit yours course",ErrorCode.COURSE_ERROR_NOT_ALLOW_UPDATE_COURSE);
        }
        version.setState(LifeUniConstant.VERSION_STATE_INIT);
        version.setReviewNote(null);
        versionRepository.save(version);
        // delete course review history
        courseReviewHistoryRepository.deleteByVersionIdAndState(version.getId(), LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_SUBMIT);

        apiMessageDto.setMessage("Cancel submit version success!");
        return apiMessageDto;
    }
    @PostMapping(value = "/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CRH_A')")
    public ApiMessageDto<String> approve(@Valid @RequestBody ApproveVersionForm approveVersionForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if (!isAdmin()) {
            throw new UnauthorizationException("Not allowed approve");
        }
        Version version = versionRepository.findFirstByIdAndState(approveVersionForm.getVersionId(), LifeUniConstant.VERSION_STATE_SUBMIT).orElse(null);
        if(version == null){
            throw new BadRequestException("Version not found",ErrorCode.VERSION_ERROR_NOT_FOUND);
        }
        version.setState(LifeUniConstant.VERSION_STATE_WAITING);
        versionRepository.save(version);
        approveVersionService.sendApproveVersionMessage(version.getId());
        apiMessageDto.setMessage("Approve version success!");
        return apiMessageDto;
    }
    @Transactional
    @PutMapping(value = "/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CRH_R')")
    public ApiMessageDto<String> reject(@Valid @RequestBody RejectVersionForm rejectVersionForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if (!isAdmin()) {
            throw new UnauthorizationException("Not allowed reject");
        }
        Version version = versionRepository.findFirstByIdAndState(rejectVersionForm.getVersionId(),LifeUniConstant.VERSION_STATE_SUBMIT).orElse(null);
        if(version == null){
            throw new BadRequestException("Version not found",ErrorCode.VERSION_ERROR_NOT_FOUND);
        }
        version.setState(LifeUniConstant.VERSION_STATE_REJECT);
        versionRepository.save(version);

        CourseReviewHistory courseReviewHistory = courseReviewHistoryRepository.findFirstByVersionIdAndState(rejectVersionForm.getVersionId(),LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_SUBMIT);
        if(courseReviewHistory == null){
            throw new NotFoundException("Can not found course review history",ErrorCode.COURSE_REVIEW_HISTORY_ERROR_NOT_FOUND);
        }
        courseReviewHistory.setState(LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_REJECT);
        courseReviewHistory.setDate(new Date());
        courseReviewHistory.setReason(rejectVersionForm.getReason());
        courseReviewHistoryRepository.save(courseReviewHistory);
        CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningByCourseId(version.getCourseId());
        if(courseVersioning == null){
            throw new BadRequestException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        notificationService.createNotificationAndSendForApproveOrRejectCourse(LifeUniConstant.NOTIFICATION_KIND_REJECT_COURSE,
                courseVersioning.getExpert().getId(),courseVersioning.getName(),version.getCourseId());
        apiMessageDto.setMessage("Reject version success!");
        return apiMessageDto;
    }
    @Transactional
    @PutMapping(value = "/edit-reject", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CRH_ER')")
    public ApiMessageDto<String> editReject(@Valid @RequestBody EditRejectVersionForm editRejectVersionForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Version version = versionRepository.findFirstByIdAndState(editRejectVersionForm.getVersionId(),LifeUniConstant.VERSION_STATE_REJECT).orElse(null);
        if(version == null){
            throw new BadRequestException("Version not found",ErrorCode.VERSION_ERROR_NOT_FOUND);
        }
        Course course = courseRepository.findById(version.getCourseId()).orElse(null);
        if(course == null){
            throw new BadRequestException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(course.getExpert().getId(), getCurrentUser())){
            throw new BadRequestException("Just only edit reject yours course",ErrorCode.COURSE_ERROR_NOT_ALLOW_UPDATE_COURSE);
        }

        version.setState(LifeUniConstant.VERSION_STATE_INIT);
        versionRepository.save(version);

        CourseReviewHistory courseReviewHistory = courseReviewHistoryRepository.findFirstByVersionIdAndState(editRejectVersionForm.getVersionId(),LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_REJECT);
        if(courseReviewHistory == null){
            throw new NotFoundException("Can not found course review history",ErrorCode.COURSE_REVIEW_HISTORY_ERROR_NOT_FOUND);
        }

        courseReviewHistory.setState(LifeUniConstant.COURSE_REVIEW_HISTORY_STATE_INIT);
        courseReviewHistory.setDate(null);
        courseReviewHistory.setReason(null);
        courseReviewHistoryRepository.save(courseReviewHistory);

        apiMessageDto.setMessage("Edit reject version success!");
        return apiMessageDto;
    }
}
