package com.easylearning.api.mapper;

import com.easylearning.api.dto.courseRetail.CourseRetailAdminDto;
import com.easylearning.api.dto.courseRetail.CourseRetailDto;
import com.easylearning.api.model.CourseRetail;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {StudentMapper.class, CourseMapper.class})
public interface CourseRetailMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "seller", target = "seller", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseRetailAdminDto")
    CourseRetailAdminDto fromEntityToCourseRetailAdminDto(CourseRetail courseRetail);

    @IterableMapping(elementTargetType = CourseRetailAdminDto.class, qualifiedByName = "fromEntityToCourseRetailAdminDto")
    @BeanMapping(ignoreByDefault = true)
    List<CourseRetailAdminDto> fromEntityToCourseRetailAdminDtoList(List<CourseRetail> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "seller", target = "seller", qualifiedByName = "fromStudentToDtoForTransaction")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseRetailDtoAutoComplete")
    CourseRetailDto fromEntityToCourseRetailDtoAutoComplete(CourseRetail courseRetail);

    @IterableMapping(elementTargetType = CourseRetailDto.class, qualifiedByName = "fromEntityToCourseRetailDtoAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<CourseRetailDto> fromEntityToCourseRetailDtoAutoCompleteList(List<CourseRetail> list);

}
