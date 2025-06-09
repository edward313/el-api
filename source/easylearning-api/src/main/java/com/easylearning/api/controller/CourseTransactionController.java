package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.courseTransaction.CourseTransactionAdminDto;
import com.easylearning.api.dto.courseTransaction.CourseTransactionDto;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.mapper.CourseTransactionMapper;
import com.easylearning.api.model.CourseTransaction;
import com.easylearning.api.model.criteria.CourseTransactionCriteria;
import com.easylearning.api.repository.CourseTransactionRepository;
import com.easylearning.api.repository.RevenueShareRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/course-transaction")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CourseTransactionController extends ABasicController {

    @Autowired
    private CourseTransactionRepository courseTransactionRepository;

    @Autowired
    private CourseTransactionMapper courseTransactionMapper;

    @Autowired
    private RevenueShareRepository revenueShareRepository;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
   @PreAuthorize("hasRole('COU_T_V')")
    public ApiMessageDto<CourseTransactionAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<CourseTransactionAdminDto> apiMessageDto = new ApiMessageDto<>();
        CourseTransaction courseTransaction = courseTransactionRepository.findById(id).orElse(null);
        if (courseTransaction == null) {
            throw new NotFoundException("Course Transaction is not found",ErrorCode.COURSE_TRANSACTION_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(courseTransactionMapper.fromEntityToCourseTransactionAdminDto(courseTransaction));
        apiMessageDto.setMessage("Get course transaction detail success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_T_L')")
    public ApiMessageDto<ResponseListDto<List<CourseTransactionAdminDto>>> list(CourseTransactionCriteria criteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<CourseTransactionAdminDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<CourseTransaction> courseTransactionList = courseTransactionRepository.findAll(criteria.getSpecification(),pageable);
        ResponseListDto<List<CourseTransactionAdminDto>> responseListObj = new ResponseListDto<>();
        List<CourseTransactionAdminDto> courseTransactionDtoList = courseTransactionMapper.fromEntityToCourseTransactionAdminDtoList(courseTransactionList.getContent());

        responseListObj.setContent(courseTransactionDtoList);
        responseListObj.setTotalPages(courseTransactionList.getTotalPages());
        responseListObj.setTotalElements(courseTransactionList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course transaction success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<CourseTransactionDto>>> autoComplete(CourseTransactionCriteria criteria) {
        ApiMessageDto<ResponseListDto<List<CourseTransactionDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Pageable pageable = PageRequest.of(0,10);
        criteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Page<CourseTransaction> courseTransactionList = courseTransactionRepository.findAll(criteria.getSpecification(),pageable);
        ResponseListDto<List<CourseTransactionDto>> responseListObj = new ResponseListDto<>();
        List<CourseTransactionDto> courseTransactionDtoList = courseTransactionMapper.fromEntityToCourseTransactionDtoAutoCompleteList(courseTransactionList.getContent());

        responseListObj.setContent(courseTransactionDtoList);
        responseListObj.setTotalPages(courseTransactionList.getTotalPages());
        responseListObj.setTotalElements(courseTransactionList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list course transaction success");
        return responseListObjApiMessageDto;
    }
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('COU_T_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        CourseTransaction courseTransaction = courseTransactionRepository.findById(id).orElse(null);
        if (courseTransaction == null) {
            throw new NotFoundException("Course Transaction is not found",ErrorCode.COURSE_TRANSACTION_ERROR_NOT_FOUND);
        }
        revenueShareRepository.deleteAllByCourseTransactionId(id);
        courseTransactionRepository.deleteById(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete course transaction success");
        return apiMessageDto;
    }
}
