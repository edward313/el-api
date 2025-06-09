package com.easylearning.api.mapper;

import com.easylearning.api.dto.promotion.code.PromotionCodeDto;
import com.easylearning.api.model.PromotionCode;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {PromotionMapper.class})
public interface PromotionCodeMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "quantityUsed", target = "quantityUsed")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPromotionCodeDto")
    PromotionCodeDto fromEntityToPromotionCodeDto(PromotionCode promotionCode);

    @IterableMapping(elementTargetType = PromotionCodeDto.class, qualifiedByName = "fromEntityToPromotionCodeDto")
    List<PromotionCodeDto> fromEntityToPromotionDtoList(List<PromotionCode> promotionCodes);
}
