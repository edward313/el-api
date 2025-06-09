package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.slideshowScheduler.SlideShowDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.slideshowScheduler.CreateSlideShowForm;
import com.easylearning.api.form.slideshowScheduler.UpdateSlideShowForm;
import com.easylearning.api.form.slideshowScheduler.UpdateSortSlideShowForm;
import com.easylearning.api.mapper.SlideShowMapper;
import com.easylearning.api.model.SlideShow;
import com.easylearning.api.model.criteria.SlideShowCriteria;
import com.easylearning.api.repository.SlideShowRepository;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/slideshow")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class SlideShowController extends ABasicController{
    @Autowired
    private SlideShowRepository slideShowRepository;
    @Autowired
    private SlideShowMapper slideShowMapper;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SS_V')")
    public ApiMessageDto<SlideShowDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<SlideShowDto> apiMessageDto = new ApiMessageDto<>();
        SlideShow slideShow = slideShowRepository.findById(id).orElse(null);
        if(slideShow == null){
            throw new NotFoundException("Not found slideshow scheduler", ErrorCode.SLIDESHOW_SCHEDULER_NOT_FOUND);
        }
        apiMessageDto.setData(slideShowMapper.fromEntityToSlideShowSchedulerDto(slideShow));
        apiMessageDto.setMessage("Get slideshow scheduler success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<SlideShowDto>>> list(SlideShowCriteria slideShowCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<SlideShowDto>>> apiMessageDto = new ApiMessageDto<>();
        Page<SlideShow> slideShowSchedulerList = slideShowRepository.findAll(slideShowCriteria.getSpecification(), pageable);
        ResponseListDto<List<SlideShowDto>> responseListDto = new ResponseListDto<>();
        responseListDto.setContent(slideShowMapper.fromEntityToSlideShowSchedulerDtoList(slideShowSchedulerList.getContent()));
        responseListDto.setTotalElements(slideShowSchedulerList.getTotalElements());
        responseListDto.setTotalPages(slideShowSchedulerList.getTotalPages());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list slideshow scheduler success");
        return apiMessageDto;
    }
    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<SlideShowDto>>> autoComplete(SlideShowCriteria slideShowCriteria) {
        ApiMessageDto<ResponseListDto<List<SlideShowDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();

        Pageable pageable = PageRequest.of(0,10);
        slideShowCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);

        Page<SlideShow> slideShowSchedulerList = slideShowRepository.findAll(slideShowCriteria.getSpecification(), pageable);
        ResponseListDto<List<SlideShowDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(slideShowMapper.fromEntityToSlideShowSchedulerDtoAutoCompleteList(slideShowSchedulerList.getContent()));
        responseListObj.setTotalPages(slideShowSchedulerList.getTotalPages());
        responseListObj.setTotalElements(slideShowSchedulerList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list slideshow scheduler auto-complete success");
        return responseListObjApiMessageDto;
    }
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SS_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateSlideShowForm createSlideShowForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        SlideShow slideShow = slideShowMapper.fromCreateSlideShowSchedulerFormToEntity(createSlideShowForm);
        slideShowRepository.save(slideShow);
        apiMessageDto.setMessage("Create slideshow scheduler success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SS_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateSlideShowForm updateSlideShowForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        SlideShow exsitingSlideShow = slideShowRepository.findById(updateSlideShowForm.getId()).orElse(null);
        if(exsitingSlideShow == null){
            throw new NotFoundException("Not found slideshow scheduler", ErrorCode.SLIDESHOW_SCHEDULER_NOT_FOUND);
        }
        slideShowMapper.fromUpdateSlideShowSchedulerFormToEntity(updateSlideShowForm, exsitingSlideShow);
        slideShowRepository.save(exsitingSlideShow);
        apiMessageDto.setMessage("Update slideshow scheduler success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SS_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        SlideShow exsitingSlideShow = slideShowRepository.findById(id).orElse(null);
        if(exsitingSlideShow == null){
            throw new NotFoundException("Not found slideshow scheduler", ErrorCode.SLIDESHOW_SCHEDULER_NOT_FOUND);
        }
        slideShowRepository.deleteById(id);
        apiMessageDto.setMessage("Delete slideshow scheduler success");
        return apiMessageDto;
    }
    @PutMapping(value = "/update-sort",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SS_US')")
    public ApiMessageDto<String> updateSort(@Valid @RequestBody List<UpdateSortSlideShowForm> updateSortSlideShowForms, BindingResult bindingResult){
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed update");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        List<Long> Idlist = updateSortSlideShowForms
                .stream()
                .map(UpdateSortSlideShowForm::getId)
                .collect(Collectors.toList());
        List<SlideShow> slideShowList = slideShowRepository.findAllById(Idlist);
        for (SlideShow slideShow : slideShowList){
            for (UpdateSortSlideShowForm updateSortSlideShowForm : updateSortSlideShowForms){
                if (slideShow.getId().equals(updateSortSlideShowForm.getId())){
                    slideShow.setOrdering(updateSortSlideShowForm.getOrdering());
                    break;
                }
            }
        }
        slideShowRepository.saveAll(slideShowList);
        apiMessageDto.setMessage("Update sort success");
        return apiMessageDto;
    }
}
