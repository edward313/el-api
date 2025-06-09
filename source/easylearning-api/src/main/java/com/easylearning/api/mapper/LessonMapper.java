package com.easylearning.api.mapper;

import com.easylearning.api.dto.lesson.LessonAdminDto;
import com.easylearning.api.dto.lesson.LessonDetailDto;
import com.easylearning.api.dto.lesson.LessonDto;
import com.easylearning.api.form.lesson.CreateLessonForm;
import com.easylearning.api.form.lesson.UpdateLessonForm;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.CourseVersioning;
import com.easylearning.api.model.Lesson;
import com.easylearning.api.model.LessonVersioning;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CourseMapper.class})

public interface LessonMapper {
    @Mapping(source = "visualId", target = "id")
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
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromLessonVersioningToLessonAdminDto")
    void fromLessonVersioningToLessonAdminDto(LessonVersioning lessonVersioning,@MappingTarget LessonAdminDto lessonAdminDto);

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
    @Mapping(source = "version", target = "version", qualifiedByName = "fromEntityToAdminDto")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToLessonAdminDto")
    LessonAdminDto fromEntityToLessonAdminDto(Lesson lesson);

    @IterableMapping(elementTargetType = LessonAdminDto.class, qualifiedByName = "fromEntityToLessonAdminDto")
    List<LessonAdminDto> fromEntityToLessonAdminDtoList(List<Lesson> lessons);

    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "thumbnail", target = "thumbnail")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "videoDuration", target = "videoDuration")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromLessonVersioningEntityToLesson")
    void fromLessonVersioningEntityToLesson(LessonVersioning lessonVersioning, @MappingTarget Lesson lesson);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "thumbnail", target = "thumbnail")
    @Mapping(source = "videoDuration", target = "videoDuration")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToLessonDto")
    LessonDto fromEntityToLessonDto(Lesson lesson);

    @IterableMapping(elementTargetType = LessonDto.class, qualifiedByName = "fromEntityToLessonDto")
    List<LessonDto> fromEntityToLessonDtoList(List<Lesson> lessons);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "thumbnail", target = "thumbnail")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "videoDuration", target = "videoDuration")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToLessonDetailDto")
    LessonDetailDto fromEntityToLessonDetailDto(Lesson lesson);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "videoDuration", target = "videoDuration")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToLessonDtoForCourseDetail")
    LessonDetailDto fromEntityToLessonDtoForCourseDetail(Lesson lesson);
    @IterableMapping(elementTargetType = LessonDetailDto.class, qualifiedByName = "fromEntityToLessonDtoForCourseDetail")
    List<LessonDetailDto> fromEntityToLessonDtoForCourseDetailList(List<Lesson> lessons);

    @IterableMapping(elementTargetType = LessonDetailDto.class, qualifiedByName = "fromEntityToLessonDetailDto")
    List<LessonDetailDto> fromEntityToLessonDetailDtoList(List<Lesson> lessons);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "thumbnail", target = "thumbnail")
    @Mapping(source = "videoDuration", target = "videoDuration")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransaction")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToAutoComplete")
    LessonDto fromEntityToAutoComplete(Lesson lesson);

    @IterableMapping(elementTargetType = LessonDto.class, qualifiedByName = "fromEntityToAutoComplete")
    List<LessonDto> fromEntityToAutoCompleteList(List<Lesson> lessons);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateLessonFormToEntity")
    Lesson fromCreateLessonFormToEntity(CreateLessonForm createLessonForm);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "content",target = "content")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateLessonFromUpdateLessonForm")
    void updateLessonFromUpdateLessonForm(UpdateLessonForm updateLessonForm, @MappingTarget Lesson lesson);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "urlDocument", target = "urlDocument")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "thumbnail", target = "thumbnail")
    @Mapping(source = "videoDuration", target = "videoDuration")
    @Mapping(source = "course", target = "course", qualifiedByName = "fromEntityToCourseDtoForCourseTransactionClient")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToLessonDetailClientDto")
    LessonDetailDto fromEntityToLessonDetailClientDto(Lesson lesson);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "isPreview", target = "isPreview")
    @Mapping(source = "kind", target = "kind")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToLessonDtoForVisual")
    LessonDto fromEntityToLessonDtoForVisual(Lesson lesson);
}