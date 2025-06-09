package com.easylearning.api.mapper;

import com.easylearning.api.dto.registerPeriod.RegisterPeriodDto;
import com.easylearning.api.model.RegisterPeriod;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RegisterPeriodMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "totalPayout", target = "totalPayout")
    @Mapping(source = "totalTaxMoney", target = "totalTaxMoney")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToDto")
    RegisterPeriodDto fromEntityToDto(RegisterPeriod registerPeriod);

    @IterableMapping(elementTargetType = RegisterPeriodDto.class, qualifiedByName = "fromEntityToDto")
    @BeanMapping(ignoreByDefault = true)
    List<RegisterPeriodDto> fromEntityToDtoList(List<RegisterPeriod> list);
}
