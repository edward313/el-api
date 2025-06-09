package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.sellerCodeTracking.SellerCodeTrackingAdminDto;
import com.easylearning.api.dto.sellerCodeTracking.SellerCodeTrackingDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.sellerCodeTracking.CreateSellerCodeTrackingForm;
import com.easylearning.api.mapper.SellerCodeTrackingMapper;
import com.easylearning.api.model.CourseRetail;
import com.easylearning.api.model.SellerCodeTracking;
import com.easylearning.api.model.Student;
import com.easylearning.api.model.criteria.SellerCodeTrackingCriteria;
import com.easylearning.api.repository.CourseRetailRepository;
import com.easylearning.api.repository.SellerCodeTrackingRepository;
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

@RestController
@RequestMapping("/v1/seller-code-tracking")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class SellerCodeTrackingController extends ABasicController {

    @Autowired
    private SellerCodeTrackingRepository sellerCodeTrackingRepository;

    @Autowired
    private SellerCodeTrackingMapper sellerCodeTrackingMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRetailRepository courseRetailRepository;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SCT_V')")
    public ApiMessageDto<SellerCodeTrackingAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<SellerCodeTrackingAdminDto> apiMessageDto = new ApiMessageDto<>();
        SellerCodeTracking sellerCodeTracking = sellerCodeTrackingRepository.findById(id).orElse(null);
        if (sellerCodeTracking == null) {
            throw new NotFoundException("Seller Code Tracking is not found", ErrorCode.SELLER_CODE_TRACKING_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(sellerCodeTrackingMapper.fromEntityToSellerCodeTrackingAdminDto(sellerCodeTracking));
        apiMessageDto.setMessage("Get seller code tracking detail success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SCT_L')")
    public ApiMessageDto<ResponseListDto<List<SellerCodeTrackingAdminDto>>> list(SellerCodeTrackingCriteria criteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<SellerCodeTrackingAdminDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<SellerCodeTracking> sellerCodeTrackingList = sellerCodeTrackingRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<SellerCodeTrackingAdminDto>> responseListObj = new ResponseListDto<>();
        List<SellerCodeTrackingAdminDto> sellerCodeTrackingDtoList = sellerCodeTrackingMapper.fromEntityToSellerCodeTrackingAdminDtoList(sellerCodeTrackingList.getContent());

        responseListObj.setContent(sellerCodeTrackingDtoList);
        responseListObj.setTotalPages(sellerCodeTrackingList.getTotalPages());
        responseListObj.setTotalElements(sellerCodeTrackingList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list seller code tracking success");
        return responseListObjApiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<SellerCodeTrackingDto>>> autoComplete(SellerCodeTrackingCriteria criteria) {
        ApiMessageDto<ResponseListDto<List<SellerCodeTrackingDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        criteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Pageable pageable = PageRequest.of(0, 10);
        Page<SellerCodeTracking> sellerCodeTrackingList = sellerCodeTrackingRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<SellerCodeTrackingDto>> responseListObj = new ResponseListDto<>();
        List<SellerCodeTrackingDto> sellerCodeTrackingDtoList = sellerCodeTrackingMapper.fromEntityToSellerCodeTrackingDtoAutoCompleteList(sellerCodeTrackingList.getContent());

        responseListObj.setContent(sellerCodeTrackingDtoList);
        responseListObj.setTotalPages(sellerCodeTrackingList.getTotalPages());
        responseListObj.setTotalElements(sellerCodeTrackingList.getTotalElements());
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list seller code tracking success");
        return responseListObjApiMessageDto;
    }


    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SCT_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        SellerCodeTracking sellerCodeTracking = sellerCodeTrackingRepository.findById(id).orElse(null);
        if (sellerCodeTracking == null) {
            throw new NotFoundException("Seller Code Tracking is not found", ErrorCode.SELLER_CODE_TRACKING_ERROR_NOT_FOUND);
        }

        sellerCodeTrackingRepository.deleteById(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete seller code tracking success");
        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> create(@Valid @RequestBody CreateSellerCodeTrackingForm createSellerCodeTrackingForm, BindingResult bindingResult) {
        if(getCurrentToken() != null && !isStudent()){
            throw new UnauthorizationException("not allow create");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        CourseRetail courseRetail = courseRetailRepository.findFirstBySeller_ReferralCode(createSellerCodeTrackingForm.getSellCode()).orElse(null);
        if(courseRetail == null){
            throw new NotFoundException("Course Retail is not found", ErrorCode.COURSE_RETAIL_ERROR_NOT_FOUND);
        }
        if(courseRetail.getStatus() != LifeUniConstant.STATUS_ACTIVE){
            throw new BadRequestException("Course Retail is not active", ErrorCode.COURSE_RETAIL_ERROR_NOT_ACTIVE);
        }
        if(courseRetail.getSeller() == null || courseRetail.getSeller().getStatus() != LifeUniConstant.STATUS_ACTIVE){
            throw new BadRequestException("Seller is not found or active", ErrorCode.SELLER_ERROR_NOT_ACTIVE);
        }
        if(courseRetail.getCourse() == null || courseRetail.getCourse().getStatus() != LifeUniConstant.STATUS_ACTIVE){
            throw new BadRequestException("Course is not found or active", ErrorCode.COURSE_ERROR_NOT_FOUND);
        }
        SellerCodeTracking sellerCodeTracking = new SellerCodeTracking();
        if(getCurrentToken() == null){
            sellerCodeTracking.setBrowserCode(getUniqueBrowserCode());
        }
        if(isStudent()){
            Student student = studentRepository.findById(getCurrentUser()).orElse(null);
            if (student == null) {
                throw new NotFoundException("Student not found", ErrorCode.STUDENT_ERROR_NOT_FOUND);
            }
            sellerCodeTracking.setStudent(student);
        }
        sellerCodeTracking.setSellCode(createSellerCodeTrackingForm.getSellCode());
        sellerCodeTrackingRepository.save(sellerCodeTracking);
        apiMessageDto.setData(sellerCodeTracking.getBrowserCode());
        apiMessageDto.setMessage("Create seller code tracking success");
        return apiMessageDto;
    }

    private String getUniqueBrowserCode(){
        String browserCode;
        SellerCodeTracking sellerCodeTracking;
        do {
            browserCode = com.easylearning.api.utils.StringUtils.generateRandomString(6);
            sellerCodeTracking = sellerCodeTrackingRepository.findFirstByBrowserCode(browserCode).orElse(null);
        } while (sellerCodeTracking != null);
        return browserCode;
    }
}
