package com.easylearning.api.mapper;


import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.courseVersioning.CourseVersioningDto;
import com.easylearning.api.form.course.UpdateCourseForm;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.CourseVersioning;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses ={CategoryMapper.class, ExpertMapper.class, CourseMapper.class, VersionMapper.class})
public interface CourseVersioningMapper {

    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCourseEntityToCourseVersioning")
    CourseVersioning fromCourseEntityToCourseVersioning(Course course);

    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromCourse")
    void updateFromCourse(Course course, @MappingTarget CourseVersioning courseVersioning);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForClient")
    @Mapping(source = "version", target = "version",qualifiedByName = "fromEntityToDtoForVisual")
    @Mapping(source = "visualId", target = "visualId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCourseVersioningToDto")
    CourseVersioningDto fromCourseVersioningToDto(CourseVersioning courseVersioning);

    @IterableMapping(elementTargetType = CourseVersioningDto.class,qualifiedByName = "fromCourseVersioningToDto")
    @BeanMapping(ignoreByDefault = true)
    List<CourseVersioningDto> fromCourseVersioningToDtoList(List<CourseVersioning> list);

    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromUpdateCourseFormToEntity")
    void fromUpdateCourseFormToEntity(UpdateCourseForm updateCourseForm, @MappingTarget CourseVersioning courseVersioning);

    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @Mapping(source = "visualId", target = "visualId")
    @BeanMapping(ignoreByDefault = true)
    @Named("copyToCourseVersioning")
    CourseVersioning copyToCourseVersioning(CourseVersioning courseVersioning);
}
