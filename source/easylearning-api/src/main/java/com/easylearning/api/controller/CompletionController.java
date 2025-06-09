package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.completion.CompletionDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.completion.CompleteLessonForm;
import com.easylearning.api.form.completion.CreateCompletionForm;
import com.easylearning.api.mapper.CompletionMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.CompletionCriteria;
import com.easylearning.api.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/completion")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CompletionController extends ABasicController{

    @Autowired
    private CompletionRepository completionRepository;
    @Autowired
    private CompletionMapper completionMapper;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private RegistrationRepository registrationRepository;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_V')")
    public ApiMessageDto<CompletionDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<CompletionDto> apiMessageDto = new ApiMessageDto<>();
        Completion completion = completionRepository.findById(id).orElse(null);
        if (completion == null) {
            throw new NotFoundException("Completion not found",ErrorCode.COMPLETION_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(completionMapper.fromEntityToCompletionDto(completion));
        apiMessageDto.setMessage("Get completion success");
        return apiMessageDto;
    }
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_L')")
    public ApiMessageDto<ResponseListDto<List<CompletionDto>>> list(CompletionCriteria completionCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<CompletionDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Completion> completions = completionRepository.findAll(completionCriteria.getSpecification(), pageable);
        ResponseListDto<List<CompletionDto>> responseListObj = new ResponseListDto<>();
        List<CompletionDto> completionDtoList = completionMapper.fromEntityToCompletionDtoList(completions.getContent());

        responseListObj.setContent(completionDtoList);
        responseListObj.setTotalPages(completions.getTotalPages());
        responseListObj.setTotalElements(completions.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list completion success");
        return responseListObjApiMessageDto;
    }
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateCompletionForm createCompletionForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Completion completion = getCompletionForCurrentStudent(createCompletionForm.getCourseId(),createCompletionForm.getLessonId());
        completion.setSecondProgress(createCompletionForm.getSecondProgress());
        Date currentTime = new Date();
        if(completion.getLesson().getVideoDuration() <= createCompletionForm.getSecondProgress()) {
            completion.setSecondProgress(completion.getLesson().getVideoDuration());
            //update false to true isFinished
            if(completion.getIsFinished() == null || Boolean.FALSE.equals(completion.getIsFinished())){
                completion.setDateFinished(currentTime);
                completion.setIsFinished(true);
            }
        }
        completionRepository.save(completion);
        // check new finished lesson => check to update finished course
        if(Boolean.TRUE.equals(completion.getIsFinished()) && Objects.equals(currentTime,completion.getDateFinished())){
            Float percent = courseRepository.getRatioCompleteLesson(completion.getCourse().getId(),completion.getStudent().getId(),LifeUniConstant.LESSON_KIND_SECTION,true);
            if(Objects.equals(percent, 100F)){
                registrationRepository.updateRegistrationState(completion.getStudent().getId(),completion.getCourse().getId(),true);
            }
        }
        apiMessageDto.setMessage("Create completion success");
        return apiMessageDto;
    }
    @PostMapping(value = "/complete-lesson", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_CL')")
    public ApiMessageDto<String> completelesson(@Valid @RequestBody CompleteLessonForm completeLessonForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Completion completion = getCompletionForCurrentStudent(completeLessonForm.getCourseId(),completeLessonForm.getLessonId());
        if(completion.getIsFinished() == null || Boolean.FALSE.equals(completion.getIsFinished())){
            completion.setDateFinished(new Date());
            completion.setIsFinished(true);
            completion.setSecondProgress(completion.getLesson().getVideoDuration());
            completionRepository.save(completion);
            Float percent = courseRepository.getRatioCompleteLesson(completion.getCourse().getId(),completion.getStudent().getId(),LifeUniConstant.LESSON_KIND_SECTION,true);
            if(Objects.equals(percent, 100F)){
                registrationRepository.updateRegistrationState(completion.getStudent().getId(),completion.getCourse().getId(),true);
            }
        }
        apiMessageDto.setMessage("Complete lesson success");
        return apiMessageDto;
    }
    public Completion getCompletionForCurrentStudent(Long courseId, Long lessonId){
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed create");
        }
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            throw new NotFoundException("Course not found",ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        Student student = studentRepository.findById(getCurrentUser()).orElse(null);
        if (student == null) {
            throw new NotFoundException("Student not found",ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        Lesson lesson = lessonRepository.findByIdAndCourseIdExcludeKind(lessonId, courseId,LifeUniConstant.LESSON_KIND_SECTION).orElse(null);
        if (lesson == null) {
            throw new NotFoundException("Lesson not found",ErrorCode.LESSON_ERROR_NOT_FOUND);
        }
        Registration registration =registrationRepository.findFirstByCourseIdAndStudentId(courseId,getCurrentUser());
        if(registration == null){
            throw new BadRequestException("The student has not registered for this course",ErrorCode.COMPLETION_ERROR_CAN_NOT_CREATE);
        }
        Completion completion = completionRepository.findFirstByStudentIdAndCourseIdAndLessonId(student.getId(),course.getId(),lesson.getId());
        if(completion == null){
            completion = new Completion();
            completion.setCourse(course);
            completion.setStudent(student);
            completion.setLesson(lesson);
        }
        return completion;
    }
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COM_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Completion completion = completionRepository.findById(id).orElse(null);
        if (completion == null) {
            throw new NotFoundException("Completion is not found",ErrorCode.COMPLETION_ERROR_NOT_FOUND);
        }
        Float percent = courseRepository.getRatioCompleteLesson(completion.getCourse().getId(),completion.getStudent().getId(),LifeUniConstant.LESSON_KIND_SECTION,true);
        if(!Objects.equals(percent, 100F)){
            registrationRepository.updateRegistrationState(completion.getStudent().getId(),completion.getCourse().getId(),false);
        }
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete completion success");
        return apiMessageDto;
    }
}
