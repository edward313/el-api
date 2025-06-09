package com.easylearning.api.controller;

import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.revenueShare.RevenueShareDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.mapper.RevenueShareMapper;
import com.easylearning.api.model.RevenueShare;
import com.easylearning.api.model.criteria.RevenueShareCriteria;
import com.easylearning.api.repository.RevenueShareRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/revenue-share")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class RevenueShareController extends ABasicController{
    @Autowired
    private RevenueShareMapper revenueShareMapper;
    @Autowired
    private RevenueShareRepository revenueShareRepository;
    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RS_V')")
    public ApiMessageDto<RevenueShareDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<RevenueShareDto> apiMessageDto = new ApiMessageDto<>();
        RevenueShare revenueShare = revenueShareRepository.findById(id).orElse(null);
        if (revenueShare == null) {
            throw new NotFoundException("revenue share is not found", ErrorCode.REVENUE_SHARE_NOT_FOUND);
        }
        apiMessageDto.setData(revenueShareMapper.fromEntityToRevenueShareDto(revenueShare));
        apiMessageDto.setMessage("Get revenue share success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RS_L')")
    public ApiMessageDto<ResponseListDto<List<RevenueShareDto>>> list(RevenueShareCriteria criteria, Pageable pageable) {
        if (!isAdmin()) {
            throw new UnauthorizationException("Not allowed list");
        }
        ApiMessageDto<ResponseListDto<List<RevenueShareDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<RevenueShare> revenueShareList = revenueShareRepository.findAll(criteria.getSpecification(),pageable);
        ResponseListDto<List<RevenueShareDto>> responseListObj = new ResponseListDto<>();
        List<RevenueShareDto> revenueShareDtos = getRevenueShareDtoList(revenueShareList.getContent());

        responseListObj.setContent(revenueShareDtos);
        responseListObj.setTotalPages(revenueShareList.getTotalPages());
        responseListObj.setTotalElements(revenueShareList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list revenue share success");
        return responseListObjApiMessageDto;
    }
}
