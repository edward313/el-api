package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.courseRetail.CourseRetailAdminDto;
import com.easylearning.api.dto.courseRetail.CourseRetailDto;
import com.easylearning.api.dto.courseRetail.RegisterRetailDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.courseRetail.RegisterRetailForm;
import com.easylearning.api.mapper.CourseRetailMapper;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.CourseRetail;
import com.easylearning.api.model.Student;
import com.easylearning.api.model.criteria.CourseRetailCriteria;
import com.easylearning.api.repository.CourseRetailRepository;
import com.easylearning.api.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/course-retail")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CourseRetailController extends ABasicController {

    @Autowired
    private CourseRetailRepository courseRetailRepository;

    @Autowired
    private CourseRetailMapper courseRetailMapper;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_R_V')")
    public ApiMessageDto<CourseRetailAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<CourseRetailAdminDto> apiMessageDto = new ApiMessageDto<>();
        CourseRetail courseRetail = courseRetailRepository.findById(id).orElse(null);
        if (courseRetail == null) {
            throw new NotFoundException("Course Retail is not found",ErrorCode.COURSE_RETAIL_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(courseRetailMapper.fromEntityToCourseRetailAdminDto(courseRetail));
        apiMessageDto.setMessage("Get course retail detail success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_R_L')")
    public ApiMessageDto<ResponseListDto<List<CourseRetailAdminDto>>> list(CourseRetailCriteria criteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<CourseRetailAdminDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<CourseRetail> courseRetailList = courseRetailRepository.findAll(criteria.getSpecification() , pageable);
        ResponseListDto<List<CourseRetailAdminDto>> responseListObj = new ResponseListDto<>();
        List<CourseRetailAdminDto> courseRetailDtoList = courseRetailMapper.fromEntityToCourseRetailAdminDtoList(courseRetailList.getContent());

        responseListObj.setContent(courseRetailDtoList);
        responseListObj.setTotalPages(courseRetailList.getTotalPages());
        responseListObj.setTotalElements(courseRetailList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course retail success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<CourseRetailDto>>> autoComplete(CourseRetailCriteria criteria) {
        ApiMessageDto<ResponseListDto<List<CourseRetailDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Pageable pageable = PageRequest.of(0,10);
        criteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Page<CourseRetail> courseRetailList = courseRetailRepository.findAll(criteria.getSpecification() , pageable);
        ResponseListDto<List<CourseRetailDto>> responseListObj = new ResponseListDto<>();
        List<CourseRetailDto> courseRetailDtoList = courseRetailMapper.fromEntityToCourseRetailDtoAutoCompleteList(courseRetailList.getContent());

        responseListObj.setContent(courseRetailDtoList);
        responseListObj.setTotalPages(courseRetailList.getTotalPages());
        responseListObj.setTotalElements(courseRetailList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course retail success");
        return responseListObjApiMessageDto;
    }


    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_R_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        CourseRetail courseRetail = courseRetailRepository.findById(id).orElse(null);
        if (courseRetail == null) {
            throw new NotFoundException("Course Retail is not found",ErrorCode.COURSE_RETAIL_ERROR_NOT_FOUND);
        }

        courseRetailRepository.deleteById(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete course retail success");
        return apiMessageDto;
    }
    @DeleteMapping(value = "/delete-by-course/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_R_DBC')")
    public ApiMessageDto<String> deleteByCourse(@PathVariable("courseId") Long courseId) {
        if(!isSeller()){
            throw new UnauthorizationException("Not allowed delete.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        CourseRetail courseRetail = courseRetailRepository.findFirstByCourseIdAndSellerId(courseId, getCurrentUser()).orElse(null);
        if (courseRetail == null) {
            throw new NotFoundException("Course Retail is not found",ErrorCode.COURSE_RETAIL_ERROR_NOT_FOUND);
        }
        courseRetailRepository.deleteAllByCourseIdAndSellerId(courseId, getCurrentUser());
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete course retail success");
        return apiMessageDto;
    }


    @PostMapping(value = "/register-retail", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_R_REG')")
    public ApiMessageDto<RegisterRetailDto> create(@Valid @RequestBody RegisterRetailForm registerRetailForm, BindingResult bindingResult) {
        ApiMessageDto<RegisterRetailDto> apiMessageDto = new ApiMessageDto<>();
        if(!isStudent()){
            throw new UnauthorizationException("Not allowed register.");
        }
        Student seller = getValidSeller(getCurrentUser());
        if(Boolean.TRUE.equals(seller.getIsSystemSeller())){
            throw new BadRequestException("Not allow use system seller code",ErrorCode.STUDENT_ERROR_NOT_ALLOW_USE_SYSTEM_CODE);
        }
        Course course = getValidCourse(registerRetailForm.getCourseId());
        if(course.getExpert().getIsSystemExpert()){
            throw  new BadRequestException("Course of system expert can not be registered.", ErrorCode.COURSE_RETAIL_ERROR_NOT_ALLOW_CREATE);
        }
        RegisterRetailDto registerRetailDto = new RegisterRetailDto();
        CourseRetail existCourseRetail = courseRetailRepository.findFirstByCourseIdAndSellerId(course.getId(),getCurrentUser()).orElse(null);
        if(existCourseRetail != null){
            registerRetailDto.setRefCode(seller.getReferralCode());
            apiMessageDto.setData(registerRetailDto);
            apiMessageDto.setResult(true);
            return apiMessageDto;
        }
        CourseRetail courseRetail = new CourseRetail();
        courseRetail.setCourse(course);
        courseRetail.setSeller(seller);
        courseRetail.setStatus(LifeUniConstant.STATUS_ACTIVE);
        courseRetailRepository.save(courseRetail);

        registerRetailDto.setRefCode(seller.getReferralCode());
        apiMessageDto.setData(registerRetailDto);
        apiMessageDto.setMessage("Register course retail success");
        return apiMessageDto;
    }

    private CourseRetail getCourseRetailByIdAndCurrentUser(Long courseRetailId){
        CourseRetail courseRetail;
        if(isSuperAdmin()){
            courseRetail = courseRetailRepository.findById(courseRetailId).orElse(null);
        }else {
            courseRetail = courseRetailRepository.findFirstByIdAndExpertId(courseRetailId,getCurrentUser()).orElse(null);
        }
        if (courseRetail == null) {
            throw new BadRequestException("Course Retail is not found");
        }
        return courseRetail;
    }

    private Student getValidSeller(Long sellerId){
        Student seller = studentRepository.findByIdAndIsSeller(sellerId,LifeUniConstant.STUDENT_IS_SELLER).orElse(null);
        if(seller == null){
            throw new NotFoundException("Seller not found");
        }
        if(!Objects.equals(seller.getStatus(),LifeUniConstant.STATUS_ACTIVE)){
            throw new BadRequestException("Seller not active");
        }
        return seller;
    }
}
