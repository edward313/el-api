package com.easylearning.api.mapper;

import com.easylearning.api.dto.revenueShare.RevenueShareDto;
import com.easylearning.api.model.RevenueShare;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses ={StudentMapper.class, ExpertMapper.class, CourseTransactionMapper.class})
public interface RevenueShareMapper {
    @Mapping(source = "id",target = "id")
    @Mapping(source = "courseTransaction",target = "courseTransaction",qualifiedByName = "fromEntityToCourseTransactionDtoAutoComplete")
    @Mapping(source = "seller", target = "seller",qualifiedByName = "fromStudentToDtoForReview")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForCourseTransaction")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "refKind", target = "refKind")
    @Mapping(source = "ratioShare",target = "ratioShare")
    @Mapping(source = "revenueMoney",target = "revenueMoney")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToRevenueShareDto")
    RevenueShareDto fromEntityToRevenueShareDto(RevenueShare revenueShare);

    @IterableMapping(elementTargetType = RevenueShareDto.class,qualifiedByName = "fromEntityToRevenueShareDto")
    @BeanMapping(ignoreByDefault = true)
    List<RevenueShareDto> fromEntityToRevenueShareDtoList(List<RevenueShare> list);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "courseTransaction",target = "courseTransaction",qualifiedByName = "fromEntityToCourseTransactionDtoForPeriodDetail")
    @Mapping(source = "seller", target = "seller",qualifiedByName = "fromStudentToDtoForReview")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForCourseTransaction")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "refKind", target = "refKind")
    @Mapping(source = "ratioShare",target = "ratioShare")
    @Mapping(source = "revenueMoney",target = "revenueMoney")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "payoutStatus", target = "payoutStatus")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToRevenueShareDtoForGetTransaction")
    RevenueShareDto fromEntityToRevenueShareDtoForGetTransaction(RevenueShare revenueShare);

    @IterableMapping(elementTargetType = RevenueShareDto.class,qualifiedByName = "fromEntityToRevenueShareDtoForGetTransaction")
    @BeanMapping(ignoreByDefault = true)
    List<RevenueShareDto> fromEntityToRevenueShareDtoForGetTransactionList(List<RevenueShare> list);
}
