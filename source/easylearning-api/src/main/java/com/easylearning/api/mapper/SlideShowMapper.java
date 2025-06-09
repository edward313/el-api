package com.easylearning.api.mapper;

import com.easylearning.api.dto.slideshowScheduler.SlideShowDto;
import com.easylearning.api.form.slideshowScheduler.CreateSlideShowForm;
import com.easylearning.api.form.slideshowScheduler.UpdateSlideShowForm;
import com.easylearning.api.model.SlideShow;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SlideShowMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "mobileImage", target = "mobileImage")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToSlideShowSchedulerDto")
    SlideShowDto fromEntityToSlideShowSchedulerDto(SlideShow slideShow);

    @IterableMapping(elementTargetType = SlideShowDto.class, qualifiedByName = "fromEntityToSlideShowSchedulerDto")
    @BeanMapping(ignoreByDefault = true)
    List<SlideShowDto> fromEntityToSlideShowSchedulerDtoList(List<SlideShow> list);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "mobileImage", target = "mobileImage")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToSlideShowSchedulerDtoAutoComplete")
    SlideShowDto fromEntityToSlideShowSchedulerDtoAutoComplete(SlideShow slideShow);

    @IterableMapping(elementTargetType = SlideShowDto.class, qualifiedByName = "fromEntityToSlideShowSchedulerDtoAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<SlideShowDto> fromEntityToSlideShowSchedulerDtoAutoCompleteList(List<SlideShow> list);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "mobileImage", target = "mobileImage")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateSlideShowSchedulerFormToEntity")
    SlideShow fromCreateSlideShowSchedulerFormToEntity(CreateSlideShowForm createSlideShowForm);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "mobileImage", target = "mobileImage")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromUpdateSlideShowSchedulerFormToEntity")
    void fromUpdateSlideShowSchedulerFormToEntity(UpdateSlideShowForm updateSlideShowForm, @MappingTarget SlideShow slideShow);
}
