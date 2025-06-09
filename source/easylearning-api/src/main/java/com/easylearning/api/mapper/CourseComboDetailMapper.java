package com.easylearning.api.mapper;

import com.easylearning.api.dto.course.ComboDto;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.CourseComboDetail;
import org.mapstruct.*;

import java.util.List;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,uses = CourseMapper.class)
public interface CourseComboDetailMapper {
    @Mapping(source = "course",target = "course",qualifiedByName = "fromEntityToCourseDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToComboDtoForCombo")
    ComboDto fromEntityToComboDtoForCombo(Course course);

    @IterableMapping(elementTargetType = ComboDto.class,qualifiedByName = "fromEntityToComboDtoForCombo")
    @BeanMapping(ignoreByDefault = true)
    List<ComboDto> fromEntityToComboDtoForComboList(List<Course> list);

    @Mapping(source = "combo",target = "course")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToComboDtoForSingle")
    ComboDto fromEntityToComboDtoForSingle(CourseComboDetail courseComboDetail);

    @IterableMapping(elementTargetType = ComboDto.class,qualifiedByName = "fromEntityToComboDtoForSingle")
    @BeanMapping(ignoreByDefault = true)
    List<ComboDto> fromEntityToComboDtoForSingleList(List<CourseComboDetail> list);
}
