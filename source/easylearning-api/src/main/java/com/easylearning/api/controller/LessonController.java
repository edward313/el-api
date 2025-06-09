package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.MoveVideoFileDto;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.lesson.LessonAdminDto;
import com.easylearning.api.dto.lesson.LessonDetailDto;
import com.easylearning.api.dto.lesson.LessonDto;
import com.easylearning.api.dto.version.VersionDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.lesson.*;
import com.easylearning.api.mapper.LessonMapper;
import com.easylearning.api.mapper.LessonVersioningMapper;
import com.easylearning.api.mapper.VersionMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.LessonCriteria;
import com.easylearning.api.repository.*;
import com.easylearning.api.service.hls.HlsService;
import com.easylearning.api.service.rabbitMQ.ProcessVideoService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/lesson")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Log4j2
public class LessonController extends ABasicController{
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonMapper lessonMapper;

    @Autowired
    private ExpertRepository expertRepository;

    @Autowired
    private CompletionRepository completionRepository;

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseVersioningRepository courseVersioningRepository;

    @Autowired
    private ProcessVideoService processVideoService;

    @Autowired
    private HlsService hlsService;
    @Autowired
    private LessonVersioningMapper lessonVersioningMapper;
    @Autowired
    private LessonVersioningRepository lessonVersioningRepository;
    @Autowired
    private VersionRepository versionRepository;
    @Autowired
    private VersionMapper versionMapper;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('L_L')")
    public ApiMessageDto<ResponseListDto<List<LessonAdminDto>>> list(LessonCriteria lessonCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<LessonAdminDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        if(lessonCriteria.getIsActive()==null){
            lessonCriteria.setIsActive(true);
        }
        if(isExpert()){
            lessonCriteria.setExpertId(getCurrentUser());
            lessonCriteria.setIgnoreCourseStatus(LifeUniConstant.STATUS_DELETE);
            lessonCriteria.setIgnoreStatus(LifeUniConstant.STATUS_DELETE);
        }
        Page<Lesson> lessonList = lessonRepository.findAll(lessonCriteria.getSpecification(), pageable);
        ResponseListDto<List<LessonAdminDto>> responseListObj = new ResponseListDto<>();
        List<LessonAdminDto> lessonAdminDtoList = lessonMapper.fromEntityToLessonAdminDtoList(lessonList.getContent());
        for (LessonAdminDto lad: lessonAdminDtoList){
            Version version;
            version = versionRepository.findHighestVersionByCourseIdAndLessonId(lad.getCourse().getId(),lad.getId());
            VersionDto versionDto = versionMapper.fromEntityToVersionDtoForMyCourse(version);
            if(!lessonCriteria.getIsActive()) {
                LessonVersioning lessonVersioning = lessonVersioningRepository.findLatestLessonVersioningByLessonId(lad.getId());
                if(lessonVersioning !=null){
                    version = lessonVersioning.getVersion();
                    if(!Objects.equals(lessonVersioning.getVersion().getState(),LifeUniConstant.VERSION_STATE_APPROVE)){
                        lessonMapper.fromLessonVersioningToLessonAdminDto(lessonVersioning,lad);
                        versionDto = versionMapper.fromEntityToVersionDtoForMyCourse(lessonVersioning.getVersion());
                    }
                }
            }
            if(Objects.equals(version.getState(), LifeUniConstant.VERSION_STATE_REJECT)){
                versionDto.setReasonReject(getReasonFromCourseReviewHistory(version.getId()));
            }
            lad.setVersion(versionDto);
        }
        // remove lesson have ignore status
        if(lessonCriteria.getIgnoreStatus() != null){
            Iterator<LessonAdminDto> iterator = lessonAdminDtoList.iterator();
            while (iterator.hasNext()) {
                LessonAdminDto lad = iterator.next();
                if (lad.getStatus().equals(lessonCriteria.getIgnoreStatus())) {
                    iterator.remove();
                }
            }
        }
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int fromIndex = Math.min(pageNumber * pageSize, lessonAdminDtoList.size());
        int toIndex = Math.min(fromIndex + pageSize, lessonAdminDtoList.size());
        List<LessonAdminDto> pagedLessons = lessonAdminDtoList.subList(fromIndex, toIndex);
        Page<LessonAdminDto> lessonAdminDtoPage = new PageImpl<>(
                pagedLessons,
                PageRequest.of(pageNumber, pageSize),
                lessonAdminDtoList.size()
        );

        responseListObj.setContent(lessonAdminDtoPage.getContent());
        responseListObj.setTotalPages(lessonAdminDtoPage.getTotalPages());
        responseListObj.setTotalElements(lessonAdminDtoPage.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get lesson list success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('L_V')")
    public ApiMessageDto<LessonAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<LessonAdminDto> apiMessageDto = new ApiMessageDto<>();
        Lesson existingLesson = lessonRepository.findById(id).orElse(null);
        if (existingLesson == null) {
            throw new NotFoundException("Lesson is not found",ErrorCode.LESSON_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(lessonMapper.fromEntityToLessonAdminDto(existingLesson));
        if(existingLesson.getKind().equals(LifeUniConstant.LESSON_KIND_VIDEO)){
            apiMessageDto.getData().setVideoUrl(hlsService.getVideoUrl(existingLesson.getContent()));
        }
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get lesson success.");
        return apiMessageDto;
    }
    @GetMapping(value = "/get-latest/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('L_V')")
    public ApiMessageDto<LessonAdminDto> getLatest(@PathVariable("id") Long id) {
        ApiMessageDto<LessonAdminDto> apiMessageDto = new ApiMessageDto<>();
        LessonVersioning lessonVersioning = lessonVersioningRepository.findLatestLessonVersioningByLessonId(id);
        if(isExpert()){
            if(Objects.equals(lessonVersioning.getCourse().getStatus(),LifeUniConstant.STATUS_DELETE) ||
                    !Objects.equals(lessonVersioning.getCourse().getExpert().getId(),getCurrentUser())){
                throw new NotFoundException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
            }
        }
        LessonAdminDto lessonAdminDto = new LessonAdminDto();
        lessonMapper.fromLessonVersioningToLessonAdminDto(lessonVersioning,lessonAdminDto);
        Version version = versionRepository.findHighestVersionByCourseIdAndLessonId(lessonVersioning.getCourse().getId(),lessonVersioning.getVisualId());
        VersionDto versionDto = versionMapper.fromEntityToVersionDtoForMyCourse(version);
        lessonAdminDto.setVersion(versionDto);

        apiMessageDto.setData(lessonAdminDto);
        if(lessonAdminDto.getKind().equals(LifeUniConstant.LESSON_KIND_VIDEO)){
            apiMessageDto.getData().setVideoUrl(hlsService.getVideoUrl(lessonVersioning.getContent()));
        }
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get lesson success.");
        return apiMessageDto;
    }

    @GetMapping(value = "/lesson-detail/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<LessonDetailDto> getLessonDetail(@PathVariable("id") Long id) {
        ApiMessageDto<LessonDetailDto> apiMessageDto = new ApiMessageDto<>();
        Lesson existingLesson;
        existingLesson = getLessonByCurrentUser(id);
        LessonDetailDto lessonDetailDto = lessonMapper.fromEntityToLessonDetailClientDto(existingLesson);

        if(existingLesson.getKind().equals(LifeUniConstant.LESSON_KIND_VIDEO)) {
            lessonDetailDto.setVideoUrl(hlsService.getVideoUrl(existingLesson.getContent()));
        }
        if(isStudent()){
            Completion completion = completionRepository.findFirstByStudentIdAndCourseIdAndLessonIdAndExcludeLessonKind(getCurrentUser(),existingLesson.getCourse().getId(),existingLesson.getId(),LifeUniConstant.LESSON_KIND_SECTION);
            if(completion != null){
                lessonDetailDto.setSecondProgress(completion.getSecondProgress());
                lessonDetailDto.setIsDone(completion.getIsFinished()!=null && completion.getIsFinished());
            }
        }
        apiMessageDto.setData(lessonDetailDto);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get lesson success.");
        return apiMessageDto;
    }
    public Lesson getLessonByCurrentUser(Long lessonId){
        // get isPreview lesson
        Lesson existingLesson = lessonRepository.findFirstByIdAndIsPreview(lessonId,true).orElse(null);
        if(existingLesson == null){
            // if lesson not isPreview => get lesson for Student registration & owner expert
            if(isStudent()){
                existingLesson = lessonRepository.findByIdAndStudentId(lessonId,getCurrentUser()).orElse(null);
            }else if (isExpert()){
                existingLesson = lessonRepository.findByIdAndExpertId(lessonId,getCurrentUser()).orElse(null);
            }
        }
        if (existingLesson == null) {
            throw new NotFoundException("User not have this lesson",ErrorCode.LESSON_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(existingLesson.getState(), LifeUniConstant.LESSON_STATE_DONE)){
            throw new BadRequestException("Lesson not done", ErrorCode.LESSON_ERROR_NOT_DONE);
        }
        return existingLesson;
    }
    
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('L_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateLessonForm createLessonForm, BindingResult bindingResult) {
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed create");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Expert expert = expertRepository.findById(getCurrentUser()).orElse(null);
        if (expert == null) {
            throw new NotFoundException("Expert is not found",ErrorCode.EXPERT_ERROR_NOT_FOUND);
        }
        Course course = courseRepository.findFirstByIdAndStatusNot(createLessonForm.getCourseId(),LifeUniConstant.STATUS_DELETE).orElse(null);
        if (course == null) {
            throw new NotFoundException("Course is not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        Version version = getInitVersion(course.getId());
        if(Objects.equals(course.getKind(), LifeUniConstant.COURSE_KIND_COMBO)){
            throw new NotFoundException("Course combo can not create lesson",ErrorCode.COURSE_ERROR_NOT_TRUE_KIND);
        }
        if(!Objects.equals(course.getExpert().getId(), getCurrentUser())){
            throw new NotFoundException("Can not create lessons with courses that are not yours",ErrorCode.COURSE_ERROR_EXPERT_ONLY_MODIFY_THEIR_COURSE);
        }
        if(Objects.equals(course.getKind(), LifeUniConstant.COURSE_KIND_NO_LESSON)){
            throw new BadRequestException("Course with kind no lesson can not create lesson",ErrorCode.COURSE_ERROR_COURSE_CAN_NOT_HAVE_LESSON);
        }
        LessonVersioning existLessonName = lessonVersioningRepository.findFistLessonVersioningUpToVersionCodeByNameAndNotDelete(course.getId(),
                version.getVersionCode(),LifeUniConstant.STATUS_DELETE,createLessonForm.getName());
        if(existLessonName != null){
            throw new BadRequestException("Lesson name already in course",ErrorCode.LESSON_ERROR_EXISTED);
        }
        Lesson lesson = lessonMapper.fromCreateLessonFormToEntity(createLessonForm);
        lesson.setCourse(course);
        lesson.setStatus(LifeUniConstant.STATUS_ACTIVE);
        lesson.setState(LifeUniConstant.LESSON_STATE_DONE);
        lesson.setVersion(version);
        lessonRepository.save(lesson);
        // create lesson versioning
        LessonVersioning lessonVersioning = lessonVersioningMapper.fromLessonFormToEntity(lesson);
        lessonVersioning.setVisualId(lesson.getId());
        lessonVersioning.setCourse(lesson.getCourse());
        lessonVersioning.setVersion(version);
        lessonVersioningRepository.save(lessonVersioning);
        apiMessageDto.setMessage("Create lesson success");

        if(lesson.getKind().equals(LifeUniConstant.LESSON_KIND_VIDEO) && StringUtils.isNoneBlank(lesson.getContent())){
            lesson.setState(LifeUniConstant.LESSON_STATE_PROCESS);
            lessonVersioning.setState(LifeUniConstant.LESSON_STATE_PROCESS);
            lessonRepository.save(lesson);
            moveVideoFile(lessonVersioning);
            processVideoService.sendProcessVideoMessage(lesson.getId(),lessonVersioning.getVersion().getVersionCode(),course.getExpert().getId(),course.getId(), lessonVersioning.getContent());
        }
        else {
            if (!lesson.getKind().equals(LifeUniConstant.LESSON_KIND_SECTION) && Objects.equals(lesson.getState(), LifeUniConstant.LESSON_STATE_DONE)) {
                CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningByCourseId(course.getId());
                if(!courseVersioning.getVersion().getId().equals(version.getId())){
                    courseVersioning = createNewCourseVersioningFromOldCourseVersioning(courseVersioning.getVisualId(),version);
                }
                if (courseVersioning.getTotalLesson() == null) {
                    courseVersioning.setTotalLesson(0);
                }
                courseVersioning.setTotalLesson(courseVersioning.getTotalLesson() + 1);
                if(!courseVersioning.getVersion().getId().equals(version.getId())){
                    courseVersioning.setId(null);
                    courseVersioning.setVersion(version);
                }
                courseVersioningRepository.save(courseVersioning);
            }
        }
        return apiMessageDto;
    }
    void moveVideoFile(LessonVersioning lessonVersioning){
        try {
            ApiMessageDto<MoveVideoFileDto> result = moveVideoFile(lessonVersioning.getContent(),lessonVersioning.getCourse().getId(),lessonVersioning.getCourse().getExpert().getId());
            if(Boolean.FALSE.equals(result.getResult())){
                log.error(result.getMessage());
                throw new BadRequestException("Fail when move video", ErrorCode.LESSON_ERROR_MOVE_VIDEO_FILE_FALSE);
            }
            lessonVersioning.setContent(result.getData().getFilePath());
            lessonVersioningRepository.save(lessonVersioning);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new BadRequestException("Fail when move video", ErrorCode.LESSON_ERROR_MOVE_VIDEO_FILE_FALSE);
        }
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('L_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateLessonForm updateLessonForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if(!isExpert()){
            throw new UnauthorizationException("Not allowed update");
        }
        LessonVersioning lessonVersioning = getLessonVersioning(updateLessonForm.getId());
        if(!Objects.equals(lessonVersioning.getCourse().getExpert().getId(), getCurrentUser())){
            throw new NotFoundException("Can not create lessons with courses that are not yours",ErrorCode.COURSE_ERROR_EXPERT_ONLY_MODIFY_THEIR_COURSE);
        }
        if(Objects.equals(lessonVersioning.getCourse().getStatus(),LifeUniConstant.STATUS_DELETE)){
            throw new BadRequestException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        if(lessonVersioning.getState().equals(LifeUniConstant.LESSON_STATE_PROCESS)){
            throw new BadRequestException("Lesson is processing",ErrorCode.LESSON_ERROR_IS_PROCESSING);
        }
        if (!lessonVersioning.getName().equals(updateLessonForm.getName())) {
            LessonVersioning existLessonName = lessonVersioningRepository.findFistLessonVersioningUpToVersionCodeByNameAndNotDelete(lessonVersioning.getCourse().getId(),
                    lessonVersioning.getVersion().getVersionCode(),LifeUniConstant.STATUS_DELETE,updateLessonForm.getName());
            if(existLessonName != null && !Objects.equals(existLessonName.getVisualId(),updateLessonForm.getId())){
                throw new BadRequestException("Lesson name already in course",ErrorCode.LESSON_ERROR_EXISTED);
            }
        }
        if(!Objects.equals(lessonVersioning.getKind(), LifeUniConstant.LESSON_KIND_SECTION)){
            if(lessonVersioning.getStatus() != updateLessonForm.getStatus() && (Objects.equals(lessonVersioning.getKind(), LifeUniConstant.LESSON_KIND_TEXT))){
                updateTotalLessonAndTotalStudyTime(updateLessonForm,lessonVersioning);
            }
        }
        boolean isProcess = false;
        if(lessonVersioning.getKind().equals(LifeUniConstant.LESSON_KIND_VIDEO)) {
            if(StringUtils.isNoneBlank(updateLessonForm.getContent()) && !updateLessonForm.getContent().equals(lessonVersioning.getContent())){
                minusCourseVersioningTotalLessonAndTotalStudyTime(lessonVersioning);
                lessonVersioning.setStatus(LifeUniConstant.STATUS_PENDING);
                lessonVersioning.setState(LifeUniConstant.LESSON_STATE_PROCESS);
                lessonVersioning.setContent(null);
                lessonVersioning.setThumbnail(null);
                lessonVersioning.setVideoDuration(0L);
                isProcess = true;
            }
        }else {
            lessonVersioning.setState(LifeUniConstant.LESSON_STATE_DONE);
        }
        lessonVersioningMapper.updateLessonFromUpdateLessonForm(updateLessonForm,lessonVersioning);
        lessonVersioningRepository.save(lessonVersioning);
        if(isProcess){
            moveVideoFile(lessonVersioning);
            processVideoService.sendProcessVideoMessage(lessonVersioning.getVisualId(),lessonVersioning.getVersion().getVersionCode(),lessonVersioning.getCourse().getExpert().getId(), lessonVersioning.getCourse().getId(),lessonVersioning.getContent());
        }
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Update lesson success");
        return apiMessageDto;
    }

    private void updateTotalLessonAndTotalStudyTime(UpdateLessonForm updateLessonForm, LessonVersioning lessonVersioning) {
        CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningByCourseId(lessonVersioning.getCourse().getId());
        if(!courseVersioning.getVersion().getId().equals(lessonVersioning.getVersion().getId())){
            courseVersioning = createNewCourseVersioningFromOldCourseVersioning(courseVersioning.getVisualId(),lessonVersioning.getVersion());
        }
        if(updateLessonForm.getStatus().equals(LifeUniConstant.STATUS_PENDING)){
            courseVersioning.setTotalLesson(courseVersioning.getTotalLesson() - 1);
        }
        if(updateLessonForm.getStatus().equals(LifeUniConstant.STATUS_ACTIVE)){
            courseVersioning.setTotalLesson(courseVersioning.getTotalLesson() + 1);
        }
        courseVersioningRepository.save(courseVersioning);
    }

    private LessonVersioning createNewLessonVersioningFromOldLessonVersioning(Long lessonId, Version version) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if(lesson == null){
            throw new NotFoundException("Lesson not found",ErrorCode.LESSON_ERROR_NOT_FOUND);
        }
        LessonVersioning newLessonVersioning = lessonVersioningMapper.fromLessonFormToEntity(lesson);
        newLessonVersioning.setVisualId(lesson.getId());
        newLessonVersioning.setCourse(lesson.getCourse());
        newLessonVersioning.setVersion(version);
        lessonVersioningRepository.save(newLessonVersioning);
        return newLessonVersioning;
    }

    private void minusCourseVersioningTotalLessonAndTotalStudyTime(LessonVersioning lessonVersioning) {
        if(Objects.equals(lessonVersioning.getState(), LifeUniConstant.LESSON_STATE_DONE)){
            CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningByCourseId(lessonVersioning.getCourse().getId());
            if(!courseVersioning.getVersion().getId().equals(lessonVersioning.getVersion().getId())){
                courseVersioning = createNewCourseVersioningFromOldCourseVersioning(courseVersioning.getVisualId(),lessonVersioning.getVersion());
            }
            courseVersioning.setTotalLesson(courseVersioning.getTotalLesson() - 1);
            courseVersioning.setTotalStudyTime(courseVersioning.getTotalStudyTime() - lessonVersioning.getVideoDuration());
            courseVersioningRepository.save(courseVersioning);
        }
    }
    @Transactional
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('L_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Lesson lesson = lessonRepository.findByIdAndIgnoreCourseStatus(id,LifeUniConstant.STATUS_DELETE).orElse(null);
        if(lesson == null){
            throw new NotFoundException("Lesson not found",ErrorCode.LESSON_ERROR_NOT_FOUND);
        }
        if(isExpert() && !Objects.equals(lesson.getCourse().getExpert().getId(),getCurrentUser())){
            throw new BadRequestException("lesson is not yours",ErrorCode.LESSON_ERROR_LESSON_IS_NOT_YOURS);
        }
        LessonVersioning lessonVersioning = getLessonVersioning(id);
        CourseVersioning courseVersioning = courseVersioningRepository.findLatestCourseVersioningByCourseId(lessonVersioning.getCourse().getId());
        if(!courseVersioning.getVersion().getId().equals(lessonVersioning.getVersion().getId())){
            courseVersioning = createNewCourseVersioningFromOldCourseVersioning(courseVersioning.getVisualId(),lessonVersioning.getVersion());
        }
        if(!Objects.equals(lesson.getKind(),LifeUniConstant.LESSON_KIND_SECTION) && Objects.equals(lessonVersioning.getState(),LifeUniConstant.LESSON_STATE_DONE)){
            decreaseTotalStudyTimeAndTotalLessonCourseVersioning(courseVersioning,lessonVersioning);
        }
        // delete new lesson never been approved
        if(Objects.equals(lesson.getStatus(),LifeUniConstant.STATUS_PENDING))
        {
            if(Objects.equals(lesson.getKind(), LifeUniConstant.LESSON_KIND_VIDEO)){
                deleteLessonVersioningFolder(lesson.getId(),lesson.getVersion().getVersionCode(),lesson.getCourse().getExpert().getId(),lesson.getCourse().getId());
            }
            lessonVersioningRepository.deleteAllByVisualId(id);
            lessonRepository.deleteById(id);
        }else {
            lessonVersioning.setStatus(LifeUniConstant.STATUS_DELETE);
            lessonVersioningRepository.save(lessonVersioning);
        }
        apiMessageDto.setMessage("Delete lesson success");
        return apiMessageDto;
    }

    private void decreaseTotalStudyTimeAndTotalLessonCourseVersioning(CourseVersioning courseVersioning, LessonVersioning lessonVersioning){
        if(courseVersioning.getTotalLesson() == null){
            courseVersioning.setTotalLesson(0);
        }
        if(courseVersioning.getTotalStudyTime() == null){
            courseVersioning.setTotalStudyTime(0L);
        }
        courseVersioning.setTotalLesson(courseVersioning.getTotalLesson()-1);
        courseVersioning.setTotalStudyTime(courseVersioning.getTotalStudyTime() - lessonVersioning.getVideoDuration());

        if(courseVersioning.getTotalLesson() < 0){
            courseVersioning.setTotalLesson(0);
        }
        if(courseVersioning.getTotalStudyTime() < 0){
            courseVersioning.setTotalStudyTime(0L);
        }
        courseVersioningRepository.save(courseVersioning);
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<LessonDto>>> list(LessonCriteria lessonCriteria) {
        ApiMessageDto<ResponseListDto<List<LessonDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        lessonCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lesson> lessonList = lessonRepository.findAll(lessonCriteria.getSpecification(), pageable);
        ResponseListDto<List<LessonDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(lessonMapper.fromEntityToAutoCompleteList(lessonList.getContent()));
        responseListObj.setTotalPages(lessonList.getTotalPages());
        responseListObj.setTotalElements(lessonList.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get lesson list success");
        return responseListObjApiMessageDto;
    }

    private List<String> getUniqueInternalUrls(String jsonSourceUrl, String jsonUpdateUrl) {
        List<String> uniqueInternalUrls = getListUrlsByKindFromJsonUrlDocument(jsonSourceUrl,LifeUniConstant.LESSON_URL_KIND_INTERNAL);
        List<String> internalUrlsT2 = getListUrlsByKindFromJsonUrlDocument(jsonUpdateUrl, LifeUniConstant.LESSON_URL_KIND_INTERNAL);
        uniqueInternalUrls.removeIf(internalUrlsT2::contains);
        return uniqueInternalUrls;
    }
    @PutMapping(value = "/update-sort",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('L_US')")
    public ApiMessageDto<String> updateSort(@Valid @RequestBody UpdateSortLessonFormList updateForm, BindingResult bindingResult){
        if(!isExpert() && !isSuperAdmin()){
            throw new UnauthorizationException("Not allowed update");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        List<Long> Idlist = updateForm.getSortForms()
                .stream()
                .map(UpdateSortLessonForm::getId)
                .collect(Collectors.toList());

        List<LessonVersioning> lessonVersionings;
        if(isExpert()){
            lessonVersionings = lessonVersioningRepository.findAllHighestLessonVersioningByLessonIdsAndExpertId(Idlist,updateForm.getCourseId(),getCurrentUser());
        } else {
            lessonVersionings = lessonVersioningRepository.findAllHighestLessonVersioningByLessonIds(Idlist,updateForm.getCourseId());
        }
        if(!lessonVersionings.isEmpty()){
            Version version = getInitVersion(updateForm.getCourseId());
            for (LessonVersioning lessonVersioning:lessonVersionings){
                for (UpdateSortLessonForm updateSortLessonForm: updateForm.getSortForms()){
                    if (lessonVersioning.getVisualId().equals(updateSortLessonForm.getId())){
                        // nếu lessonVersioning thuộc version cũ (đã approve) => tạo lessonVersioning mới
                        if(!lessonVersioning.getVersion().getId().equals(version.getId())){
                            lessonVersioning = createNewLessonVersioningFromOldLessonVersioning(lessonVersioning.getVisualId(),version);
                        }
                        lessonVersioning.setOrdering(updateSortLessonForm.getOrdering());
                        break;
                    }
                }
            }
            lessonVersioningRepository.saveAll(lessonVersionings);
        }
        apiMessageDto.setMessage("Update sort success");
        return apiMessageDto;
    }
    @PutMapping(value = "/retry-process-video", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('L_R_P_V')")
    public ApiMessageDto<String> retryProcessVideo(@Valid @RequestBody RetryProcessVideoLessonForm retryProcessVideoLessonForm, BindingResult bindingResult){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        LessonVersioning existingLessonVersioning = getLessonVersioning(retryProcessVideoLessonForm.getLessonId());
        if(!existingLessonVersioning.getKind().equals(LifeUniConstant.LESSON_KIND_VIDEO)){
            throw new NotFoundException("Lesson not kind video",ErrorCode.LESSON_ERROR_NOT_KIND_VIDEO);
        }
        if(!existingLessonVersioning.getState().equals(LifeUniConstant.LESSON_STATE_FAIL)){
            throw new BadRequestException("Lesson is not fail",ErrorCode.LESSON_ERROR_NOT_FAIL);
        }
        if(StringUtils.isBlank(existingLessonVersioning.getContent())){
            throw new BadRequestException("Content is blank",ErrorCode.LESSON_ERROR_CONTENT_IS_BLANK);
        }
        minusCourseVersioningTotalLessonAndTotalStudyTime(existingLessonVersioning);
        existingLessonVersioning.setState(LifeUniConstant.LESSON_STATE_PROCESS);
        existingLessonVersioning.setStatus(LifeUniConstant.STATUS_PENDING);
        lessonVersioningRepository.save(existingLessonVersioning);
        processVideoService.sendProcessVideoMessage(existingLessonVersioning.getVisualId(),existingLessonVersioning.getVersion().getVersionCode(),existingLessonVersioning.getCourse().getExpert().getId(),existingLessonVersioning.getCourse().getId(),existingLessonVersioning.getContent());
        apiMessageDto.setMessage("Retry process video success");
        return apiMessageDto;
    }
    LessonVersioning getLessonVersioning(Long lessonId){
        LessonVersioning existingLessonVersioning = lessonVersioningRepository.findLatestLessonVersioningByLessonId(lessonId);
        if(existingLessonVersioning == null) {
            throw new NotFoundException("Lesson is not found",ErrorCode.LESSON_ERROR_NOT_FOUND);
        }
        if(isExpert() && !existingLessonVersioning.getCourse().getExpert().getId().equals(getCurrentUser())){
            throw new NotFoundException("Can not update lessons with courses that are not yours",ErrorCode.COURSE_ERROR_EXPERT_ONLY_MODIFY_THEIR_COURSE);
        }
        Version version = getInitVersion(existingLessonVersioning.getCourse().getId());
        if(!Objects.equals(existingLessonVersioning.getVersion().getId(),version.getId())){
            existingLessonVersioning = createNewLessonVersioningFromOldLessonVersioning(existingLessonVersioning.getVisualId(),version);
        }
        return existingLessonVersioning;
    }
}
