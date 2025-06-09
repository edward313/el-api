package com.easylearning.api.mapper;

import com.easylearning.api.dto.registerPayout.RegisterPayoutDto;
import com.easylearning.api.dto.registerPeriod.RegisterPeriodDto;
import com.easylearning.api.form.registerPayout.CreateRegisterPayoutForm;
import com.easylearning.api.model.RegisterPayout;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses ={AccountMapper.class})
public interface RegisterPayoutMapper {
    @Mapping(source = "id",target = "id")
    @Mapping(source = "accountKind",target = "accountKind")
    @Mapping(source = "state",target = "state")
    @Mapping(source = "taxPercent",target = "taxPercent")
    @Mapping(source = "money",target = "money")
    @Mapping(source = "note",target = "note")
    @Mapping(source = "account", target = "account",qualifiedByName = "fromAccountToDtoForPeriodDetail")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "bankInfo", target = "bankInfo")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToRegisterPayoutDto")
    RegisterPayoutDto fromEntityToRegisterPayoutDto(RegisterPayout registerPayout);

    @IterableMapping(elementTargetType = RegisterPayoutDto.class,qualifiedByName = "fromEntityToRegisterPayoutDto")
    @BeanMapping(ignoreByDefault = true)
    List<RegisterPayoutDto> fromEntityToRegisterPayoutDtoList(List<RegisterPayout> list);

    @Mapping(source = "money", target = "money")
    @Mapping(source = "note", target = "note")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateRegisterPayoutFormToEntity")
    RegisterPayout fromCreateRegisterPayoutFormToEntity(CreateRegisterPayoutForm createRegisterPayoutForm);
}
