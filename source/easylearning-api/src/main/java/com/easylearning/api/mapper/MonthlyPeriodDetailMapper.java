package com.easylearning.api.mapper;

import com.easylearning.api.dto.monthlyPeriodDetail.MonthlyPeriodDetailAdminDto;
import com.easylearning.api.dto.monthlyPeriodDetail.MonthlyPeriodDetailDto;
import com.easylearning.api.model.MonthlyPeriodDetail;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AccountMapper.class, MonthlyPeriodMapper.class})
public interface MonthlyPeriodDetailMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForPeriodDetail")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalMoney", target = "totalMoney")
    @Mapping(source = "totalRefMoney", target = "totalRefMoney")
    @Mapping(source = "period", target = "period", qualifiedByName = "fromEntityToMonthlyPeriodDto")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPeriodDetailAdminDto")
    MonthlyPeriodDetailAdminDto fromEntityToPeriodDetailAdminDto(MonthlyPeriodDetail monthlyPeriodDetail);

    @IterableMapping(elementTargetType = MonthlyPeriodDetailAdminDto.class, qualifiedByName = "fromEntityToPeriodDetailAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<MonthlyPeriodDetailAdminDto> fromEntityToPeriodDetailAdminDtoList(List<MonthlyPeriodDetail> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForPeriodDetail")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalMoney", target = "totalMoney")
    @Mapping(source = "totalRefMoney", target = "totalRefMoney")
    @Mapping(source = "period", target = "period", qualifiedByName = "fromEntityToMonthlyPeriodDto")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPeriodDetailAutoComplete")
    MonthlyPeriodDetailDto fromEntityToPeriodDetailAutoComplete(MonthlyPeriodDetail monthlyPeriodDetail);

    @IterableMapping(elementTargetType = MonthlyPeriodDetailDto.class, qualifiedByName = "fromEntityToPeriodDetailAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<MonthlyPeriodDetailDto> fromEntityToPeriodDetailAutoCompleteList(List<MonthlyPeriodDetail> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDtoForPeriodDetail")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalMoney", target = "totalMoney")
    @Mapping(source = "totalRefMoney", target = "totalRefMoney")
    @Mapping(source = "period", target = "period", qualifiedByName = "fromEntityToMonthlyPeriodDto")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPeriodDetailDto")
    MonthlyPeriodDetailDto fromEntityToPeriodDetailDto(MonthlyPeriodDetail monthlyPeriodDetail);
}
