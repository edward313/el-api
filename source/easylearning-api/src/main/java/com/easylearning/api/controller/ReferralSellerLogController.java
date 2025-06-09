package com.easylearning.api.controller;


import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ApiResponse;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.referralSellerLog.ReferralSellerLogDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.mapper.ReferralSellerLogMapper;
import com.easylearning.api.model.ReferralSellerLog;
import com.easylearning.api.model.criteria.ReferralSellerLogCriteria;
import com.easylearning.api.repository.ReferralSellerLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/referral-seller-log")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ReferralSellerLogController extends ABasicController {
    @Autowired
    private ReferralSellerLogRepository referralSellerLogRepository;
    @Autowired
    private ReferralSellerLogMapper referralSellerLogMapper;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RSL_L')")
    public ApiMessageDto<ResponseListDto<List<ReferralSellerLogDto>>> list(ReferralSellerLogCriteria referralSellerLogCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<ReferralSellerLogDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<ReferralSellerLogDto>> responseListDto = new ResponseListDto<>();
        Page<ReferralSellerLog> referralSellerLogs = referralSellerLogRepository.findAll(referralSellerLogCriteria.getSpecification(), pageable);
        responseListDto.setContent(referralSellerLogMapper.fromEntityListToDtoList(referralSellerLogs.getContent()));
        responseListDto.setTotalPages(referralSellerLogs.getTotalPages());
        responseListDto.setTotalElements(referralSellerLogs.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list referral seller logs success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RSL_V')")
    public ApiResponse<ReferralSellerLogDto> get(@PathVariable("id") Long id) {
        ApiResponse<ReferralSellerLogDto> apiMessageDto = new ApiResponse<>();
        ReferralSellerLog referralSellerLog = referralSellerLogRepository.findById(id).orElse(null);
        if (referralSellerLog == null) {
            throw new NotFoundException("Not found ReferralSellerLog", ErrorCode.REFERRAL_SELLER_LOG_ERROR_NOT_FOUND);
        }

        apiMessageDto.setData(referralSellerLogMapper.fromEntityToDto(referralSellerLog));
        apiMessageDto.setMessage("Get referral seller log success");
        return apiMessageDto;

    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RSL_D')")
    public ApiResponse<String> delete(@PathVariable("id") Long id) {
        ApiResponse<String> apiMessageDto = new ApiResponse<>();
        ReferralSellerLog referralSellerLog = referralSellerLogRepository.findById(id).orElse(null);
        if (referralSellerLog == null) {
            throw new NotFoundException("Not found Referral Seller Log", ErrorCode.REFERRAL_SELLER_LOG_ERROR_NOT_FOUND);
        }
        referralSellerLogRepository.deleteById(id);
        apiMessageDto.setMessage("Delete referral seller log success");
        return apiMessageDto;
    }
}
