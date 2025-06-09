package com.easylearning.api.mapper;

import com.easylearning.api.dto.courseTransaction.CourseTransactionAdminDto;
import com.easylearning.api.dto.courseTransaction.CourseTransactionDto;
import com.easylearning.api.model.CourseTransaction;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {StudentMapper.class, ExpertMapper.class, CourseMapper.class, BookingMapper.class})
public interface CourseTransactionMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "expert", target = "expert", qualifiedByName = "fromEntityToExpertDtoForCourseTransaction")
    @Mapping(source = "seller", target = "seller", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "originalPrice", target = "originalPrice")
    @Mapping(source = "booking", target = "booking", qualifiedByName = "fromEntityToBookingDtoForTransaction")
    @Mapping(source = "refSellCode", target = "refSellCode")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseTransactionAdminDto")
    CourseTransactionAdminDto fromEntityToCourseTransactionAdminDto(CourseTransaction courseTransaction);

    @IterableMapping(elementTargetType = CourseTransactionAdminDto.class, qualifiedByName = "fromEntityToCourseTransactionAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<CourseTransactionAdminDto> fromEntityToCourseTransactionAdminDtoList(List<CourseTransaction> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "expert", target = "expert", qualifiedByName = "fromEntityToExpertDtoForCourseTransaction")
    @Mapping(source = "seller", target = "seller", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @Mapping(source = "originalPrice", target = "originalPrice")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "booking", target = "booking", qualifiedByName = "fromEntityToBookingDtoForTransaction")
    @Mapping(source = "refSellCode", target = "refSellCode")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseTransactionDtoAutoComplete")
    CourseTransactionDto fromEntityToCourseTransactionDtoAutoComplete(CourseTransaction courseTransaction);

    @IterableMapping(elementTargetType = CourseTransactionDto.class, qualifiedByName = "fromEntityToCourseTransactionDtoAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<CourseTransactionDto> fromEntityToCourseTransactionDtoAutoCompleteList(List<CourseTransaction> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "expert", target = "expert", qualifiedByName = "fromEntityToExpertDtoForCourseTransaction")
    @Mapping(source = "seller", target = "seller", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "booking", target = "booking", qualifiedByName = "fromEntityToBookingDtoForTransaction")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForPeriodDetail")
    @Mapping(source = "originalPrice", target = "originalPrice")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "refSellCode", target = "refSellCode")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseTransactionDtoForPeriodDetail")
    CourseTransactionDto fromEntityToCourseTransactionDtoForPeriodDetail(CourseTransaction courseTransaction);

    @IterableMapping(elementTargetType = CourseTransactionDto.class, qualifiedByName = "fromEntityToCourseTransactionDtoForPeriodDetail")
    @BeanMapping(ignoreByDefault = true)
    List<CourseTransactionDto> fromEntityToCourseTransactionDtoForPeriodDetailList(List<CourseTransaction> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @Mapping(source = "booking", target = "booking", qualifiedByName = "fromEntityToBookingDtoForTransaction")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "originalPrice", target = "originalPrice")
    @Mapping(source = "createdDate",target = "createdDate")
    @Mapping(source = "refSellCode", target = "refSellCode")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseTransactionDtoForMyRevenue")
    CourseTransactionDto fromEntityToCourseTransactionDtoForMyRevenue(CourseTransaction courseTransaction);

    @IterableMapping(elementTargetType = CourseTransactionDto.class, qualifiedByName = "fromEntityToCourseTransactionDtoForMyRevenue")
    @BeanMapping(ignoreByDefault = true)
    List<CourseTransactionDto> fromEntityToCourseTransactionDtoForMyRevenueList(List<CourseTransaction> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "originalPrice", target = "originalPrice")
    @Mapping(source = "createdDate",target = "createdDate")
    @Mapping(source = "refSellCode", target = "refSellCode")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseTransactionDtoForMyClientBooking")
    CourseTransactionDto fromEntityToCourseTransactionDtoForMyClientBooking(CourseTransaction courseTransaction);

    @IterableMapping(elementTargetType = CourseTransactionDto.class, qualifiedByName = "fromEntityToCourseTransactionDtoForMyClientBooking")
    @BeanMapping(ignoreByDefault = true)
    List<CourseTransactionDto> fromEntityToCourseTransactionDtoForMyClientBookingList(List<CourseTransaction> list);
}
