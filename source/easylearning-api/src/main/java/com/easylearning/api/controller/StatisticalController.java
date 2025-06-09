package com.easylearning.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.statistical.FeStatisticDto;
import com.easylearning.api.dto.statistical.FeStatisticDtoImpl;
import com.easylearning.api.dto.statistical.StatisticalDto;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.mapper.StatisticalMapper;
import com.easylearning.api.model.Statistical;
import com.easylearning.api.model.criteria.StatisticalCriteria;
import com.easylearning.api.repository.StatisticalRepository;
import com.easylearning.api.schedule.StatisticalSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/v1/statistical")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class StatisticalController extends ABasicController{
    @Autowired
    private StatisticalRepository statisticalRepository;
    @Autowired
    private StatisticalMapper statisticalMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StatisticalSchedule statisticalSchedule;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STA_V')")
    public ApiMessageDto<StatisticalDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<StatisticalDto> apiMessageDto = new ApiMessageDto<>();
        Statistical statistical = statisticalRepository.findById(id).orElse(null);
        if(statistical == null){
            throw new NotFoundException("Not found statistical", ErrorCode.STATISTICAL_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(statisticalMapper.fromEntityToDto(statistical));
        apiMessageDto.setMessage("Get statistical by id success");
        return apiMessageDto;
    }
    @ApiIgnore
    @GetMapping(value = "/run-fe-statistic-schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STA_R')")
    public ApiMessageDto<String> runFeStatisticSchedule() {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed run.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        statisticalSchedule.setFeStatistic();
        apiMessageDto.setMessage("Run statistic schedule success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get-fe-statistic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<FeStatisticDto> getFeStatistic() {
        try {
            ApiMessageDto<FeStatisticDto> apiMessageDto = new ApiMessageDto<>();
            Statistical statistical = statisticalRepository.findFirstByStatisticalKey(LifeUniConstant.STATISTICAL_FE_STATISTIC_KEY);
            FeStatisticDto feStatisticDto;
            //create new statistical
            if (statistical == null) {
                feStatisticDto = statisticalRepository.getFeStatistic(LifeUniConstant.STATUS_ACTIVE,LifeUniConstant.COURSE_KIND_SINGLE);
                statistical = new Statistical();
                statistical.setStatisticalKey(LifeUniConstant.STATISTICAL_FE_STATISTIC_KEY);
                statistical.setStatisticalValue(objectMapper.writeValueAsString(feStatisticDto));
                statisticalRepository.save(statistical);
            }else {
                feStatisticDto = objectMapper.readValue(statistical.getStatisticalValue(), FeStatisticDtoImpl.class);
            }
            apiMessageDto.setData(feStatisticDto);
            apiMessageDto.setMessage("get statistic success");
            return apiMessageDto;
        }  catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STA_L')")
    public ApiMessageDto<ResponseListDto<List<StatisticalDto>>> list(StatisticalCriteria statisticalCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<StatisticalDto>>> apiMessageDto = new ApiMessageDto<>();
        Page<Statistical> statistics = statisticalRepository.findAll(statisticalCriteria.getSpecification(), pageable);
        ResponseListDto<List<StatisticalDto>> responseListDto = new ResponseListDto();
        responseListDto.setContent(statisticalMapper.fromEntityToDtoList(statistics.getContent()));
        responseListDto.setTotalElements(statistics.getTotalElements());
        responseListDto.setTotalPages(statistics.getTotalPages());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list statistics success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STA_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Statistical statistical = statisticalRepository.findById(id).orElse(null);
        if(statistical == null){
            throw new NotFoundException("Not found statistical", ErrorCode.STATISTICAL_ERROR_NOT_FOUND);
        }
        statisticalRepository.deleteById(id);
        apiMessageDto.setMessage("Delete statistical success");
        return apiMessageDto;
    }
}
