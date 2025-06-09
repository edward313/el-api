package com.easylearning.api.mapper;

import com.easylearning.api.dto.courseReviewHistory.CourseReviewHistoryDto;
import com.easylearning.api.dto.courseTransaction.CourseTransactionAdminDto;
import com.easylearning.api.model.CourseReviewHistory;
import com.easylearning.api.model.CourseTransaction;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {VersionMapper.class})
public interface CourseReviewHistoryMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "version", target = "version", qualifiedByName = "fromEntityToAdminDto")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "reason", target = "reason")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseReviewHistoryDto")
    CourseReviewHistoryDto fromEntityToCourseReviewHistoryDto(CourseReviewHistory courseReviewHistory);
    @IterableMapping(elementTargetType = CourseReviewHistoryDto.class, qualifiedByName = "fromEntityToCourseReviewHistoryDto")
    @BeanMapping(ignoreByDefault = true)
    List<CourseReviewHistoryDto> fromEntityToCourseReviewHistoryDtoList(List<CourseReviewHistory> list);}
