package com.easylearning.api.controller;


import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.registration.RegistrationDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.registration.UpdateRegistrationForm;
import com.easylearning.api.mapper.RegistrationMapper;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.Registration;
import com.easylearning.api.model.Student;
import com.easylearning.api.model.criteria.RegistrationCriteria;
import com.easylearning.api.repository.CourseRepository;
import com.easylearning.api.repository.ExpertRepository;
import com.easylearning.api.repository.RegistrationRepository;
import com.easylearning.api.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/registration")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class RegistrationController extends ABasicController{

    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private RegistrationMapper registrationMapper;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ExpertRepository expertRepository;
    
    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REG_V')")
    public ApiMessageDto<RegistrationDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<RegistrationDto> apiMessageDto = new ApiMessageDto<>();
        Registration registration = registrationRepository.findById(id).orElse(null);
        if (registration == null) {
            throw new NotFoundException("Registration is not found", ErrorCode.REGISTRATION_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(registrationMapper.fromEntityToRegistrationDto(registration));
        apiMessageDto.setMessage("Get registration success");
        return apiMessageDto;
    }
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REG_L')")
    public ApiMessageDto<ResponseListDto<List<RegistrationDto>>> list(RegistrationCriteria registrationCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<RegistrationDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Registration> registrations = registrationRepository.findAll(registrationCriteria.getSpecification(), pageable);
        ResponseListDto<List<RegistrationDto>> responseListObj = new ResponseListDto<>();
        List<RegistrationDto> RegistrationDtoList = registrationMapper.fromEntityToRegistrationDtoList(registrations.getContent());

        responseListObj.setContent(RegistrationDtoList);
        responseListObj.setTotalPages(registrations.getTotalPages());
        responseListObj.setTotalElements(registrations.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list registration success");
        return responseListObjApiMessageDto;
    }
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REG_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Registration registration = registrationRepository.findById(id).orElse(null);
        if (registration == null) {
            throw new NotFoundException("Registration is not found", ErrorCode.REGISTRATION_ERROR_NOT_FOUND);
        }
        registrationRepository.deleteById(id);

        Course course = registration.getCourse();
        if(course.getSoldQuantity() == null){
            course.setSoldQuantity(0);
        }
        course.setSoldQuantity(course.getSoldQuantity()-1);
        if(course.getSoldQuantity() < 0){
            course.setSoldQuantity(0);
        }
        updateTotalStudentExpert(registration.getCourse().getExpert(), registration.getStudent().getId(),false);
        courseRepository.save(course);

        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete registration success");
        return apiMessageDto;
    }
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REG_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateRegistrationForm updateRegistrationForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Registration registration = registrationRepository.findById(updateRegistrationForm.getId()).orElse(null);
        if (registration == null) {
            throw new NotFoundException("Registration is not found", ErrorCode.REGISTRATION_ERROR_NOT_FOUND);
        }
        Course course = courseRepository.findById(updateRegistrationForm.getCourseId()).orElse(null);
        if (course == null) {
            throw new NotFoundException("Course is not found", ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        Student student = studentRepository.findById(updateRegistrationForm.getStudentId()).orElse(null);
        if (student == null) {
            throw new NotFoundException("Student is not found", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        if(!Objects.equals(updateRegistrationForm.getStudentId(), registration.getStudent().getId())
                || !Objects.equals(updateRegistrationForm.getCourseId(), registration.getCourse().getId())){
            Registration res =registrationRepository.findFirstByCourseIdAndStudentId(updateRegistrationForm.getCourseId(),updateRegistrationForm.getStudentId());
            if(res != null){
                throw new BadRequestException("Registration is exist, can not update", ErrorCode.REGISTRATION_ERROR_EXIST);
            }
        }
        registration.setCourse(course);
        registration.setStudent(student);
        if(isStudent() && updateRegistrationForm.getIsFinished()){
            throw new BadRequestException("Student can not update course finished", ErrorCode.STUDENT_ERROR_NOT_FOUND);
        }
        else {
            registration.setIsFinished(updateRegistrationForm.getIsFinished());
        }
        registrationRepository.save(registration);
        apiMessageDto.setMessage("Update registration success");
        return apiMessageDto;
    }
}
