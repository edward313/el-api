package com.easylearning.api.mapper;

import com.easylearning.api.dto.completion.CompletionDto;
import com.easylearning.api.model.Completion;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses ={StudentMapper.class, CourseMapper.class, LessonMapper.class})
public interface CompletionMapper {

    @Mapping(source = "id",target = "id")
    @Mapping(source = "dateFinished",target = "dateFinished")
    @Mapping(source = "isFinished",target = "isFinished")
    @Mapping(source = "secondProgress",target = "secondProgress")
    @Mapping(source = "student", target = "student",qualifiedByName = "fromStudentToDtoForRegistration")
    @Mapping(source = "course", target = "course",qualifiedByName = "fromEntityToCourseDto")
    @Mapping(source = "lesson", target = "lesson",qualifiedByName = "fromEntityToLessonDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCompletionDto")
    CompletionDto fromEntityToCompletionDto(Completion completion);

    @IterableMapping(elementTargetType = CompletionDto.class,qualifiedByName = "fromEntityToCompletionDto")
    @BeanMapping(ignoreByDefault = true)
    List<CompletionDto> fromEntityToCompletionDtoList(List<Completion> list);

}
