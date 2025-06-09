package com.easylearning.api.mapper;

import com.easylearning.api.dto.lesson.LessonAdminDto;
import com.easylearning.api.dto.news.NewsDto;
import com.easylearning.api.form.lesson.CreateLessonForm;
import com.easylearning.api.form.news.CreateNewsForm;
import com.easylearning.api.form.news.UpdateNewsForm;
import com.easylearning.api.model.Lesson;
import com.easylearning.api.model.News;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CategoryMapper.class})
public interface NewsMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryDto" )
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "mobileBanner", target = "mobileBanner")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToNewsDto")
    NewsDto fromEntityToNewsDto(News news);

    @IterableMapping(elementTargetType = NewsDto.class, qualifiedByName = "fromEntityToNewsDto")
    List<NewsDto> fromEntityToNewsDtoList(List<News> news);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryDto" )
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "mobileBanner", target = "mobileBanner")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToNewsDtoForClient")
    NewsDto fromEntityToNewsDtoForClient(News news);

    @IterableMapping(elementTargetType = NewsDto.class, qualifiedByName = "fromEntityToNewsDtoForClient")
    List<NewsDto> fromEntityToNewsDtoListForClient(List<News> news);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "mobileBanner", target = "mobileBanner")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToNewsDtoAutoComplete")
    NewsDto fromEntityToNewsDtoAutoComplete(News news);

    @IterableMapping(elementTargetType = NewsDto.class, qualifiedByName = "fromEntityToNewsDtoAutoComplete")
    List<NewsDto> fromEntityToNewsDtoAutoCompleteList(List<News> news);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "mobileBanner", target = "mobileBanner")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "avatar", target = "avatar")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateNewsFormToEntity")
    News fromCreateNewsFormToEntity(CreateNewsForm createNewsForm);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "mobileBanner", target = "mobileBanner")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "avatar", target = "avatar")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateNewsFromForm")
    void updateNewsFromForm(UpdateNewsForm updateNewsForm, @MappingTarget News news);
}
