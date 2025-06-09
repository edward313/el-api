package com.easylearning.api.mapper;

import com.easylearning.api.dto.promotion.PromotionDto;
import com.easylearning.api.form.promotion.CreatePromotionForm;
import com.easylearning.api.form.promotion.UpdatePromotionForm;
import com.easylearning.api.model.Promotion;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PromotionMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "discountValue", target = "discountValue")
    @Mapping(source = "limitValue", target = "limitValue")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "state", target = "state")
    @Mapping(source ="prefix", target = "prefix")
    @Mapping(source ="numRandom", target = "numRandom")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPromotionDto")
    PromotionDto fromEntityToPromotionDto(Promotion promotion);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "discountValue", target = "discountValue")
    @Mapping(source = "limitValue", target = "limitValue")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPromotionDtoForClientCheck")
    PromotionDto fromEntityToPromotionDtoForClientCheck(Promotion promotion);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "discountValue", target = "discountValue")
    @Mapping(source = "limitValue", target = "limitValue")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source ="prefix", target = "prefix")
    @Mapping(source ="numRandom", target = "numRandom")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPromotionDtoForBooking")
    PromotionDto fromEntityToPromotionDtoForBooking(Promotion promotion);

    @IterableMapping(elementTargetType = PromotionDto.class, qualifiedByName = "fromEntityToPromotionDto")
    List<PromotionDto> fromEntityToPromotionDtoList(List<Promotion> promotions);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPromotionDtoAutoComplete")
    PromotionDto fromEntityToPromotionDtoAutoComplete(Promotion promotion);
    @IterableMapping(elementTargetType = PromotionDto.class, qualifiedByName = "fromEntityToPromotionDtoAutoComplete")
    List<PromotionDto> fromEntityToPromotionDtoAutoCompleteList(List<Promotion> promotions);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "discountValue", target = "discountValue")
    @Mapping(source = "limitValue", target = "limitValue")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @Mapping(source ="prefix", target = "prefix")
    @Mapping(source ="numRandom", target = "numRandom")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreatePromotionFormToEntity")
    Promotion fromCreatePromotionFormToEntity(CreatePromotionForm createPromotionForm);

    @Mapping(source = "description", target = "description")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateEntityFromUpdatePromotionFormForStateCreated")
    void updateEntityFromUpdatePromotionFormForStateCreated(UpdatePromotionForm updatePromotionForm, @MappingTarget Promotion promotion);

    @Mapping(source = "state", target = "state")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateEntityFromUpdatePromotionForm")
    void updateEntityFromUpdatePromotionForm(UpdatePromotionForm updatePromotionForm, @MappingTarget Promotion promotion);
}
