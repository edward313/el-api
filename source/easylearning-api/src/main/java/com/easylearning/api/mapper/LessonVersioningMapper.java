package com.easylearning.api.mapper;


import com.easylearning.api.dto.lesson.LessonDetailDto;
import com.easylearning.api.dto.lessonVersioning.LessonVersioningDetailDto;
import com.easylearning.api.form.lesson.UpdateLessonForm;
import com.easylearning.api.model.Lesson;
import com.easylearning.api.model.LessonVersioning;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CourseMapper.class, LessonMapper.class})
public interface LessonVersioningMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "videoDuration",target = "videoDuration")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "state", target = "state")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromLessonFormToEntity")
    LessonVersioning fromLessonFormToEntity(Lesson lesson);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "thumbnail", target = "thumbnail")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "videoDuration", target = "videoDuration")
    @Mapping(source = "visualId", target = "visualId")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToLessonVersioningDetailDto")
    LessonVersioningDetailDto fromEntityToLessonVersioningDetailDto(LessonVersioning lessonVersioning);
    @IterableMapping(elementTargetType = LessonVersioningDetailDto.class, qualifiedByName = "fromEntityToLessonVersioningDetailDto")
    List<LessonVersioningDetailDto> fromEntityToLessonVersioningDetailDtoList(List<LessonVersioning> lessonVersionings);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "content",target = "content")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "ordering", target = "ordering")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateLessonFromUpdateLessonForm")
    void updateLessonFromUpdateLessonForm(UpdateLessonForm updateLessonForm, @MappingTarget LessonVersioning lessonVersioning);
}
