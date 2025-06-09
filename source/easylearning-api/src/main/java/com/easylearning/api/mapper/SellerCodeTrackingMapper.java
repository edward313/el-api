package com.easylearning.api.mapper;

import com.easylearning.api.dto.sellerCodeTracking.SellerCodeTrackingAdminDto;
import com.easylearning.api.dto.sellerCodeTracking.SellerCodeTrackingDto;
import com.easylearning.api.model.SellerCodeTracking;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {StudentMapper.class})
public interface SellerCodeTrackingMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "sellCode", target = "sellCode")
    @Mapping(source = "browserCode", target = "browserCode")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToSellerCodeTrackingAdminDto")
    SellerCodeTrackingAdminDto fromEntityToSellerCodeTrackingAdminDto(SellerCodeTracking sellerCodeTracking);

    @IterableMapping(elementTargetType = SellerCodeTrackingAdminDto.class, qualifiedByName = "fromEntityToSellerCodeTrackingAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<SellerCodeTrackingAdminDto> fromEntityToSellerCodeTrackingAdminDtoList(List<SellerCodeTracking> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "student", target = "student", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "sellCode", target = "sellCode")
    @Mapping(source = "browserCode", target = "browserCode")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToSellerCodeTrackingDtoAutoComplete")
    SellerCodeTrackingDto fromEntityToSellerCodeTrackingDtoAutoComplete(SellerCodeTracking sellerCodeTracking);

    @IterableMapping(elementTargetType = SellerCodeTrackingDto.class, qualifiedByName = "fromEntityToSellerCodeTrackingDtoAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<SellerCodeTrackingDto> fromEntityToSellerCodeTrackingDtoAutoCompleteList(List<SellerCodeTracking> list);
}
