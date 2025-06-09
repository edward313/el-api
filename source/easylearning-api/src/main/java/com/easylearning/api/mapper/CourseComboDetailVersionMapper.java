package com.easylearning.api.mapper;

import com.easylearning.api.dto.course.ComboDto;
import com.easylearning.api.model.CourseComboDetail;
import com.easylearning.api.model.CourseComboDetailVersion;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = CourseMapper.class)
public interface CourseComboDetailVersionMapper {
    @Mapping(source = "course",target = "course",qualifiedByName = "fromEntityToCourseDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToComboDtoForCombo")
    ComboDto fromEntityToComboDtoForCombo(CourseComboDetailVersion courseComboDetailVersion);

    @IterableMapping(elementTargetType = ComboDto.class,qualifiedByName = "fromEntityToComboDtoForCombo")
    @BeanMapping(ignoreByDefault = true)
    List<ComboDto> fromEntityToComboDtoForComboList(List<CourseComboDetailVersion> list);
}
