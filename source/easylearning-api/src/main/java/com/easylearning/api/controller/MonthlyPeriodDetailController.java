package com.easylearning.api.controller;


import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.monthlyPeriodDetail.MonthlyPeriodDetailAdminDto;
import com.easylearning.api.dto.monthlyPeriodDetail.MonthlyPeriodDetailDto;
import com.easylearning.api.dto.revenueShare.RevenueShareDto;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.mapper.MonthlyPeriodDetailMapper;
import com.easylearning.api.model.MonthlyPeriodDetail;
import com.easylearning.api.model.RevenueShare;
import com.easylearning.api.model.criteria.PeriodDetailCriteria;
import com.easylearning.api.model.criteria.RevenueShareCriteria;
import com.easylearning.api.repository.MonthlyPeriodDetailRepository;
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
@RequestMapping("/v1/monthly-period-detail")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class MonthlyPeriodDetailController extends ABasicController {

    @Autowired
    private MonthlyPeriodDetailRepository monthlyPeriodDetailRepository;

    @Autowired
    private MonthlyPeriodDetailMapper monthlyPeriodDetailMapper;
    @Autowired
    private RevenueShareRepository revenueShareRepository;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MPD_L')")
    public ApiMessageDto<ResponseListDto<List<MonthlyPeriodDetailAdminDto>>> listPeriodDetail(PeriodDetailCriteria criteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<MonthlyPeriodDetailAdminDto>>> apiMessageDto = new ApiMessageDto<>();

        criteria.setIsOrderByCreatedDate(true);
        Page<MonthlyPeriodDetail> periodDetails = monthlyPeriodDetailRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<MonthlyPeriodDetailAdminDto>> responseListDto = new ResponseListDto<>(
                monthlyPeriodDetailMapper.fromEntityToPeriodDetailAdminDtoList(periodDetails.getContent()),
                periodDetails.getTotalElements(),
                periodDetails.getTotalPages()
        );
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get period detail list success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MPD_V')")
    public ApiMessageDto<MonthlyPeriodDetailAdminDto> getPeriodDetail(@PathVariable("id") Long id) {
        ApiMessageDto<MonthlyPeriodDetailAdminDto> apiMessageDto = new ApiMessageDto<>();
        MonthlyPeriodDetail monthlyPeriodDetail = monthlyPeriodDetailRepository.findById(id).orElse(null);
        if (monthlyPeriodDetail == null) {
            throw new NotFoundException("period detail not found", ErrorCode.PERIOD_DETAIL_ERROR_NOT_FOUND);
        }

        apiMessageDto.setData(monthlyPeriodDetailMapper.fromEntityToPeriodDetailAdminDto(monthlyPeriodDetail));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get period detail success.");
        return apiMessageDto;
    }
    @GetMapping(value = "/get-transaction/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MPD_V')")
    public ApiMessageDto<ResponseListDto<List<RevenueShareDto>>> getTransaction(@PathVariable("id") Long id, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<RevenueShareDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        MonthlyPeriodDetail monthlyPeriodDetail = monthlyPeriodDetailRepository.findById(id).orElse(null);
        if (monthlyPeriodDetail == null) {
            throw new NotFoundException("period detail not found", ErrorCode.PERIOD_DETAIL_ERROR_NOT_FOUND);
        }
        ResponseListDto<List<RevenueShareDto>> responseListObj = new ResponseListDto<>();
        RevenueShareCriteria revenueShareCriteria = new RevenueShareCriteria();
        if(java.util.Objects.equals(monthlyPeriodDetail.getKind(), LifeUniConstant.PERIOD_DETAIL_KIND_EXPERT))
            revenueShareCriteria.setExpertId(monthlyPeriodDetail.getAccount().getId());
        else { // kind seller
            revenueShareCriteria.setSellerId(monthlyPeriodDetail.getAccount().getId());
        }
        revenueShareCriteria.setPeriodId(monthlyPeriodDetail.getPeriod().getId());
        revenueShareCriteria.setIsOrderByCreatedDate(true);
        Page<RevenueShare> revenueSharePages = revenueShareRepository.findAll(revenueShareCriteria.getSpecification(), pageable);
        responseListObj.setContent(getRevenueShareDtoList(revenueSharePages.getContent()));
        responseListObj.setTotalPages(revenueSharePages.getTotalPages());
        responseListObj.setTotalElements(revenueSharePages.getTotalElements());

        responseListObjApiMessageDto.setResult(true);
        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get transaction for period detail success!!");
        return responseListObjApiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MPD_D')")
    public ApiMessageDto<String> deletePeriodDetail(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        MonthlyPeriodDetail monthlyPeriodDetail = monthlyPeriodDetailRepository.findById(id).orElse(null);
        if (monthlyPeriodDetail == null) {
            throw new NotFoundException("period detail not found", ErrorCode.PERIOD_DETAIL_ERROR_NOT_FOUND);
        }

        monthlyPeriodDetailRepository.deleteById(id);
        apiMessageDto.setMessage("Delete period detail success");
        return apiMessageDto;
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<MonthlyPeriodDetailDto>>> listPeriodDetail(PeriodDetailCriteria criteria) {
        ApiMessageDto<ResponseListDto<List<MonthlyPeriodDetailDto>>> apiMessageDto = new ApiMessageDto<>();
        Pageable pageable = PageRequest.of(0,10);
        criteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Page<MonthlyPeriodDetail> periodDetails = monthlyPeriodDetailRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<MonthlyPeriodDetailDto>> responseListDto = new ResponseListDto<>(
                monthlyPeriodDetailMapper.fromEntityToPeriodDetailAutoCompleteList(periodDetails.getContent()),
                periodDetails.getTotalElements(),
                periodDetails.getTotalPages()
        );
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get period detail list success");
        return apiMessageDto;
    }
}