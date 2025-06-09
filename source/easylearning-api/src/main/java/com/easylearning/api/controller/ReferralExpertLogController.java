package com.easylearning.api.controller;


import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.expert.ExpertDto;
import com.easylearning.api.dto.expert.referralLog.ReferralExpertLogDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.mapper.ReferralExpertLogMapper;
import com.easylearning.api.model.Expert;
import com.easylearning.api.model.ReferralExpertLog;
import com.easylearning.api.model.criteria.ExpertCriteria;
import com.easylearning.api.model.criteria.ReferralExpertLogCriteria;
import com.easylearning.api.repository.ReferralExpertLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/referral-expert-log")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ReferralExpertLogController {

    @Autowired
    private ReferralExpertLogRepository referralExpertLogRepository;

    @Autowired
    private ReferralExpertLogMapper referralExpertLogMapper;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REL_V')")
    public ApiMessageDto<ReferralExpertLogDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<ReferralExpertLogDto> apiMessageDto = new ApiMessageDto<>();
        ReferralExpertLog referralExpertLog = referralExpertLogRepository.findById(id).orElse(null);
        if (referralExpertLog == null) {
            throw new NotFoundException("Not found ReferralExpertLog", ErrorCode.REFERRAL_EXPERT_LOG_ERROR_NOT_FOUND);
        }

        apiMessageDto.setData(referralExpertLogMapper.fromEntityToExpertDto(referralExpertLog));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get ReferralExpertLog success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REL_L')")
    public ApiMessageDto<ResponseListDto<List<ReferralExpertLogDto>>> list(ReferralExpertLogCriteria referralExpertLogCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<ReferralExpertLogDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();

        Page<ReferralExpertLog> expertList = referralExpertLogRepository.findAll(referralExpertLogCriteria.getSpecification(), pageable);
        ResponseListDto<List<ReferralExpertLogDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(referralExpertLogMapper.fromEntityToReferralExpertLogDtoList(expertList.getContent()));
        responseListObj.setTotalPages(expertList.getTotalPages());
        responseListObj.setTotalElements(expertList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list ReferralExpertLog success");
        return responseListObjApiMessageDto;
    }
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REL_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        ReferralExpertLog referralExpertLog = referralExpertLogRepository.findById(id).orElse(null);
        if (referralExpertLog == null) {
            throw new NotFoundException("Not found ReferralExpertLog", ErrorCode.REFERRAL_EXPERT_LOG_ERROR_NOT_FOUND);
        }

        referralExpertLogRepository.deleteById(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete ReferralExpertLog success");
        return apiMessageDto;
    }
}
