package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.promotion.CheckValidPromotionCodeDto;
import com.easylearning.api.dto.promotion.PromotionDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.form.promotion.CreatePromotionForm;
import com.easylearning.api.form.promotion.UpdatePromotionForm;
import com.easylearning.api.mapper.PromotionCodeMapper;
import com.easylearning.api.mapper.PromotionMapper;
import com.easylearning.api.model.Promotion;
import com.easylearning.api.model.PromotionCode;
import com.easylearning.api.model.criteria.PromotionCriteria;
import com.easylearning.api.repository.BookingRepository;
import com.easylearning.api.repository.PromotionCodeRepository;
import com.easylearning.api.repository.PromotionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/v1/promotion")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Log4j2
public class PromotionController extends ABasicController{
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotionMapper promotionMapper;
    @Autowired
    private PromotionCodeRepository promotionCodeRepository;
    @Autowired
    private PromotionCodeMapper promotionCodeMapper;
    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PM_L')")
    public ApiMessageDto<ResponseListDto<List<PromotionDto>>> list(PromotionCriteria promotionCriteria, Pageable pageable) {
        ApiMessageDto<ResponseListDto<List<PromotionDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<Promotion> promotionList = promotionRepository.findAll(promotionCriteria.getCriteria(), pageable);
        ResponseListDto<List<PromotionDto>> responseListObj = new ResponseListDto<>();
        List<PromotionDto> promotionDtoList = promotionMapper.fromEntityToPromotionDtoList(promotionList.getContent());
        for(PromotionDto pd: promotionDtoList){
            List<PromotionCode> promotionCodeList = promotionCodeRepository.findAllByPromotionId(pd.getId());
            pd.setPromotionCodes(promotionCodeMapper.fromEntityToPromotionDtoList(promotionCodeList));
        }
        responseListObj.setContent(promotionDtoList);
        responseListObj.setTotalPages(promotionList.getTotalPages());
        responseListObj.setTotalElements(promotionList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get promotion list success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<PromotionDto>>> list(PromotionCriteria promotionCriteria) {
        ApiMessageDto<ResponseListDto<List<PromotionDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        promotionCriteria.setStatus(LifeUniConstant.STATUS_ACTIVE);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Promotion> promotionList = promotionRepository.findAll(promotionCriteria.getCriteria(), pageable);
        ResponseListDto<List<PromotionDto>> responseListObj = new ResponseListDto<>();

        responseListObj.setContent(promotionMapper.fromEntityToPromotionDtoAutoCompleteList(promotionList.getContent()));
        responseListObj.setTotalPages(promotionList.getTotalPages());
        responseListObj.setTotalElements(promotionList.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get promotion auto complete list success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PM_V')")
    public ApiMessageDto<PromotionDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<PromotionDto> apiMessageDto = new ApiMessageDto<>();
        Promotion existingPromotion = promotionRepository.findById(id).orElse(null);
        if (existingPromotion == null) {
            throw new NotFoundException("Promotion not found", ErrorCode.PROMOTION_ERROR_NOT_FOUND);
        }
        PromotionDto promotionDto = promotionMapper.fromEntityToPromotionDto(existingPromotion);
        List<PromotionCode> promotionCodeList = promotionCodeRepository.findAllByPromotionId(id);
        promotionDto.setPromotionCodes(promotionCodeMapper.fromEntityToPromotionDtoList(promotionCodeList));

        apiMessageDto.setData(promotionDto);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get lesson success.");
        return apiMessageDto;
    }
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PM_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreatePromotionForm createPromotionForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Promotion existingPromotion = promotionRepository.findFirstByName(createPromotionForm.getName());
        if (existingPromotion != null) {
            throw new BadRequestException("Promotion name is exist", ErrorCode.PROMOTION_ERROR_EXIST);
        }
        Promotion promotion = promotionMapper.fromCreatePromotionFormToEntity(createPromotionForm);
        promotionRepository.save(promotion);
        if (Objects.equals(createPromotionForm.getState(), LifeUniConstant.PROMOTION_STATE_CREATED) ||
                Objects.equals(createPromotionForm.getState(), LifeUniConstant.PROMOTION_STATE_RUNNING)) {
                for(int i=0;i<createPromotionForm.getQuantity();i++){
                    PromotionCode promotionCode = new PromotionCode();
                    promotionCode.setPromotion(promotion);
                    promotionCode.setStatus(LifeUniConstant.STATUS_ACTIVE);
                    promotionCode.setQuantityUsed(0); // vừa tạo nên quantityUsed = 0
                    promotionCode.setCode(getUniqueCode(promotion.getPrefix(), promotion.getNumRandom()));
                    promotionCodeRepository.save(promotionCode);
                }
        }
        apiMessageDto.setMessage("Create promotion success");
        return apiMessageDto;
    }
    private String getUniqueCode(String prefix, Integer numRandom){
        String code;
        PromotionCode promotionCode;
        Random random = new Random();
        StringBuilder randomNumbers = new StringBuilder();
        do {
            for (int i = 0; i < numRandom; i++) {
                int randomNumber = random.nextInt(10); // Số ngẫu nhiên từ 0 đến 9
                randomNumbers.append(randomNumber);
            }

            code = prefix + randomNumbers;
            promotionCode = promotionCodeRepository.findByCode(code).orElse(null);
        } while (promotionCode != null);
        return code;
    }
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PM_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdatePromotionForm updatePromotionForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Promotion existingPromotion = promotionRepository.findById(updatePromotionForm.getId()).orElse(null);
        if (existingPromotion == null) {
            throw new NotFoundException("Promotion code not found", ErrorCode.PROMOTION_CODE_ERROR_NOT_FOUND);
        }
        if(!checkFlowUpdate(existingPromotion.getState(),updatePromotionForm.getState())){
            throw new BadRequestException("flow updated incorrectly", ErrorCode.PROMOTION_ERROR_NOT_TRUE_FLOW);
        }
        if(Objects.equals(existingPromotion.getState(), LifeUniConstant.PROMOTION_STATE_CREATED))
        {
            promotionMapper.updateEntityFromUpdatePromotionFormForStateCreated(updatePromotionForm, existingPromotion);
        }
        else
        {
            promotionMapper.updateEntityFromUpdatePromotionForm(updatePromotionForm, existingPromotion);
        }
        promotionRepository.save(existingPromotion);

        apiMessageDto.setMessage("Update promotion success");
        return apiMessageDto;
    }
    private boolean checkFlowUpdate(Integer preState, Integer newState){
        Integer[] promotionState = LifeUniConstant.PROMOTION_STATE;
        if (Objects.equals(preState, newState)) {
            return true;
        }
        return Arrays.asList(promotionState).indexOf(newState) - Arrays.asList(promotionState).indexOf(preState) == 1;
    }
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PM_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Promotion existingPromotion = promotionRepository.findById(id).orElse(null);
        if (existingPromotion == null) {
            throw new NotFoundException("Promotion not found", ErrorCode.PROMOTION_ERROR_NOT_FOUND);
        }
        bookingRepository.deleteAllByPromotionId(id);
        promotionCodeRepository.deleteAllByPromotionId(id);
        promotionRepository.deleteById(id);
        apiMessageDto.setMessage("Delete promotion success");
        return apiMessageDto;
    }
    @GetMapping(value = "/check-valid/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<CheckValidPromotionCodeDto> checkValid(@PathVariable("code") String code) {
        ApiMessageDto<CheckValidPromotionCodeDto> apiMessageDto = new ApiMessageDto<>();
        PromotionCode promotionCode = promotionCodeRepository.findValidPromotionCodeByCode(code,LifeUniConstant.STATUS_ACTIVE,
                LifeUniConstant.PROMOTION_STATE_RUNNING, LifeUniConstant.STATUS_ACTIVE).orElse(null);
        Promotion promotion = promotionRepository.findByPromotionCode(code);
        if (promotionCode == null) {
            throw new NotFoundException("Promotion code not found", ErrorCode.PROMOTION_CODE_ERROR_NOT_FOUND);
        }
        CheckValidPromotionCodeDto checkValidPromotionCodeDto = new CheckValidPromotionCodeDto();
        // check student used code or promotion code already reach maximum quality
        if((promotion.getType().equals(LifeUniConstant.PROMOTION_TYPE_USE_ONE) && promotionCode.getQuantityUsed() > 0)
                || (promotion.getType().equals(LifeUniConstant.PROMOTION_TYPE_USE_MULTIPLE) && promotionCode.getQuantityUsed() >= promotion.getQuantity())){
            checkValidPromotionCodeDto.setIsValid(false);
        }
        else {
            checkValidPromotionCodeDto.setIsValid(true);
        }
        checkValidPromotionCodeDto.setQuantityUsed(promotionCode.getQuantityUsed());
        checkValidPromotionCodeDto.setPromotion(promotionMapper.fromEntityToPromotionDtoForClientCheck(promotion));

        apiMessageDto.setData(checkValidPromotionCodeDto);
        apiMessageDto.setMessage("Check promotion code success!");
        return apiMessageDto;
    }
}
