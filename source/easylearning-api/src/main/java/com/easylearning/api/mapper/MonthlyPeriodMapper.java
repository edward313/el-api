package com.easylearning.api.mapper;

import com.easylearning.api.dto.monthlyPeriod.MonthlyPeriodAdminDto;
import com.easylearning.api.dto.monthlyPeriod.MonthlyPeriodDto;
import com.easylearning.api.form.monthlyPeriod.CreateMonthlyPeriodForm;
import com.easylearning.api.form.monthlyPeriod.UpdateMonthlyPeriodForm;
import com.easylearning.api.model.MonthlyPeriod;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MonthlyPeriodMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "systemRevenue", target = "systemRevenue")
    @Mapping(source = "totalPayout", target = "totalPayout")
    @Mapping(source = "totalSaleOffMoney", target = "totalSaleOffMoney")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToMonthlyPeriodAdminDto")
    MonthlyPeriodAdminDto fromEntityToMonthlyPeriodAdminDto(MonthlyPeriod monthlyPeriod);

    @IterableMapping(elementTargetType = MonthlyPeriodAdminDto.class, qualifiedByName = "fromEntityToMonthlyPeriodAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<MonthlyPeriodAdminDto> fromEntityToMonthlyPeriodAdminDtoList(List<MonthlyPeriod> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "systemRevenue", target = "systemRevenue")
    @Mapping(source = "totalPayout", target = "totalPayout")
    @Mapping(source = "totalSaleOffMoney", target = "totalSaleOffMoney")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToMonthlyPeriodAutoComplete")
    MonthlyPeriodDto fromEntityToMonthlyPeriodAutoComplete(MonthlyPeriod monthlyPeriod);

    @IterableMapping(elementTargetType = MonthlyPeriodDto.class, qualifiedByName = "fromEntityToMonthlyPeriodAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<MonthlyPeriodDto> fromEntityToMonthlyPeriodAutoCompleteList(List<MonthlyPeriod> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "systemRevenue", target = "systemRevenue")
    @Mapping(source = "totalPayout", target = "totalPayout")
    @Mapping(source = "totalSaleOffMoney", target = "totalSaleOffMoney")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToMonthlyPeriodDto")
    MonthlyPeriodDto fromEntityToMonthlyPeriodDto(MonthlyPeriod monthlyPeriod);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("createFromToEntity")
    MonthlyPeriod createFromToEntity(CreateMonthlyPeriodForm createMonthlyPeriodForm);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "startDate", target = "startDate")
    @Mapping(source = "endDate", target = "endDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateForm")
    void updateFromUpdateForm(UpdateMonthlyPeriodForm updateMonthlyPeriodForm, @MappingTarget MonthlyPeriod monthlyPeriod);
}
