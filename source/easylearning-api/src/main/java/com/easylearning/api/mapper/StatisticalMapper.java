package com.easylearning.api.mapper;

import com.easylearning.api.dto.statistical.StatisticalDto;
import com.easylearning.api.model.Statistical;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatisticalMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "statisticalKey", target = "statisticalKey")
    @Mapping(source = "statisticalValue", target = "statisticalValue")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToDto")
    StatisticalDto fromEntityToDto(Statistical statistical);

    @IterableMapping(elementTargetType = StatisticalDto.class, qualifiedByName = "fromEntityToDto")
    List<StatisticalDto> fromEntityToDtoList(List<Statistical> statisticals);
}
