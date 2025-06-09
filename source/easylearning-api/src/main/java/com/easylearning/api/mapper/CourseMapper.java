package com.easylearning.api.mapper;


import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.form.course.CreateCourseForm;
import com.easylearning.api.form.course.UpdateCourseForm;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.CourseVersioning;
import com.easylearning.api.model.elastic.ElasticCourse;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses ={CategoryMapper.class, ExpertMapper.class, VersionMapper.class})
public interface CourseMapper {

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
    @Mapping(source = "version", target = "version",qualifiedByName = "fromEntityToAdminDto")
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
    @Named("fromEntityToCourseDto")
    CourseDto fromEntityToCourseDto(Course course);


    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromEntityToCourseDto")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromEntityToCourseDtoList(List<Course> list);

    @Mapping(source = "visualId",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForClient")
    @Mapping(source = "version", target = "version",qualifiedByName = "fromEntityToAdminDto")
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
    @BeanMapping(ignoreByDefault = true,qualifiedByName = "afterMappingFromCourseVersioningToCourseDto")
    @Named("fromCourseVersioningToCourseDto")
    CourseDto fromCourseVersioningToCourseDto(CourseVersioning courseVersioning);

    @AfterMapping
    @Named("afterMappingFromCourseVersioningToCourseDto")
    default void setStatusAndIsPublished(CourseVersioning courseVersioning, @MappingTarget CourseDto courseDto) {
        if(Objects.equals(courseVersioning.getStatus(),LifeUniConstant.STATUS_PENDING)){
            courseDto.setHasPublishedVersion(false);
        }
    }
    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromCourseVersioningToCourseDto")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromCourseVersioningToCourseDtoList(List<CourseVersioning> list);

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
    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "version",target = "version", qualifiedByName = "fromEntityToAdminDto")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @BeanMapping(ignoreByDefault = true,qualifiedByName = "afterMappingToCourseDtoForClient")
    @Named("fromEntityToCourseDtoForClient")
    CourseDto fromEntityToCourseDtoForClient(Course course);
    @AfterMapping
    @Named("afterMappingToCourseDtoForClient")
    default void afterMappingToCourseDtoForClient(Course course, @MappingTarget CourseDto courseDto) {
        if (course.getExpert() != null && Boolean.TRUE.equals(course.getExpert().getIsSystemExpert())) {
            courseDto.setExpert(null);
        }
    }
    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromEntityToCourseDtoForClient")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromEntityToCourseDtoListForClient(List<Course> list);

    @Mapping(source = "id",target = "courseId")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToElasticCategory")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToElasticExpert")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "modifiedBy", target = "modifiedBy")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToElasticCourse")
    ElasticCourse fromEntityToElasticCourse(Course course);

    @IterableMapping(elementTargetType = ElasticCourse.class,qualifiedByName = "fromEntityToElasticCourse")
    @BeanMapping(ignoreByDefault = true)
    List<ElasticCourse> fromEntityToElasticCourseList(List<Course> list);

    @Mapping(source = "courseId",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "field", target = "elasticField",qualifiedByName = "fromElasticEntityToElasticCategoryDto")
    @Mapping(source = "expert", target = "elasticExpert",qualifiedByName = "fromElasticEntityToElasticExpertDto")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromElasticEntityToCourseDto")
    CourseDto fromElasticEntityToCourseDto(ElasticCourse elasticCourse);

    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromElasticEntityToCourseDto")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromElasticEntityToCourseDtoList(List<ElasticCourse> list);

    @Mapping(source = "courseId",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "field", target = "elasticField",qualifiedByName = "fromElasticEntityToElasticCategoryDto")
    @Mapping(source = "expert", target = "elasticExpert",qualifiedByName = "fromElasticEntityToElasticExpertDto")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @Mapping(source = "createdDate", target = "createdDate")
    @BeanMapping(ignoreByDefault = true,qualifiedByName = "afterMappingToElasticCourseDtoForClient")
    @Named("fromElasticEntityToCourseDtoForClient")
    CourseDto fromElasticEntityToCourseDtoForClient(ElasticCourse elasticCourse);
    @AfterMapping
    @Named("afterMappingToElasticCourseDtoForClient")
    default void afterMappingToCourseDtoForClient(ElasticCourse elasticCourse, @MappingTarget CourseDto courseDto) {
        if (elasticCourse.getExpert() != null && Boolean.TRUE.equals(elasticCourse.getExpert().getIsSystemExpert())) {
            courseDto.setExpert(null);
        }
    }
    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromElasticEntityToCourseDtoForClient")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromElasticEntityToCourseDtoListForClient(List<ElasticCourse> list);
    

    @Mapping(source = "id",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForClient")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseDtoAutoComplete")
    CourseDto fromEntityToCourseDtoAutoComplete(Course course);

    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromEntityToCourseDtoAutoComplete")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromEntityToCourseDtoAutoCompleteList(List<Course> list);

    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "kind", target = "kind")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateCourseFormToEntity")
    Course fromCreateCourseFormToEntity(CreateCourseForm createCourseForm);

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
    void fromUpdateCourseFormToEntity(UpdateCourseForm updateCourseForm, @MappingTarget Course course);

    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToElasticCategory")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToElasticExpert")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateCourseEntityToElasticEntity")
    void updateCourseEntityToElasticEntity(Course course, @MappingTarget ElasticCourse elasticCourse);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForCourseTransaction")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseDtoForCourseTransaction")
    CourseDto fromEntityToCourseDtoForCourseTransaction(Course course);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseDtoForPeriodDetail")
    CourseDto fromEntityToCourseDtoForPeriodDetail(Course course);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForCourseTransaction")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @BeanMapping(ignoreByDefault = true,qualifiedByName = "afterMappingToCourseDtoForCourseTransactionClient")
    @Named("fromEntityToCourseDtoForCourseTransactionClient")
    CourseDto fromEntityToCourseDtoForCourseTransactionForClient(Course course);
    @AfterMapping
    @Named("afterMappingToCourseDtoForCourseTransactionClient")
    default void afterMappingToCourseDtoForCourseTransactionForClient(Course course, @MappingTarget CourseDto courseDto) {
        if (course.getExpert() != null && Boolean.TRUE.equals(course.getExpert().getIsSystemExpert())) {
            courseDto.setExpert(null);
        }
    }

    @Mapping(source = "id",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForCategoryHomeClient")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @BeanMapping(ignoreByDefault = true,qualifiedByName = "afterMappingToCourseDtoForCategoryHomeClient")
    @Named("fromEntityToCourseDtoForCategoryHomeClient")
    CourseDto fromEntityToCourseDtoForCategoryHomeClient(Course course);
    @AfterMapping
    @Named("afterMappingToCourseDtoForCategoryHomeClient")
    default void afterMappingToCourseDtoForForCategoryHomeClient(Course course, @MappingTarget CourseDto courseDto) {
        if (course.getExpert() != null && Boolean.TRUE.equals(course.getExpert().getIsSystemExpert())) {
            courseDto.setExpert(null);
        }
    }

    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromEntityToCourseDtoForCategoryHomeClient")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromEntityToCourseDtoForCategoryHomeClientList(List<Course> list);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForClient")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "version", target = "version", qualifiedByName = "fromEntityToVersionDtoForMyCourse")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseDtoForMyCourse")
    CourseDto fromEntityToCourseDtoForMyCourse(Course course);

    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromEntityToCourseDtoForMyCourse")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromEntityToCourseDtoForMyCourseList(List<Course> list);

    @Mapping(source = "visualId",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForClient")
    @Mapping(source = "version", target = "version",qualifiedByName = "fromEntityToAdminDto")
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
    @Named("fromCourseVersioningToCourseDtoForMyCourse")
    CourseDto fromCourseVersioningToCourseDtoForMyCourse(CourseVersioning courseVersioning);


    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromCourseVersioningToCourseDtoForMyCourse")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromCourseVersioningToCourseDtoForMyCourseList(List<CourseVersioning> list);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseDtoForRegistration")
    CourseDto fromEntityToCourseDtoForRegistration(Course course);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "name",target = "name")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToCourseDtoForVisual")
    CourseDto fromEntityToCourseDtoForVisual(Course course);
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "totalReview", target = "totalReview")
    @Mapping(source = "averageStar", target = "averageStar")
    @Mapping(source = "totalStudyTime", target = "totalStudyTime")
    @Mapping(source = "soldQuantity", target = "soldQuantity")
    @Mapping(source = "totalLesson", target = "totalLesson")
    @Mapping(source = "isSellerCourse", target = "isSellerCourse")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCourseVersioningEntityToCourse")
    void fromCourseVersioningEntityToCourse(CourseVersioning courseVersioning,@MappingTarget Course course);

    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForClient")
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
    @Named("fromCourseVersioningToCourseDto")
    void fromCourseVersioningToCourseDto(CourseVersioning course,@MappingTarget CourseDto courseDto);

    @Mapping(source = "visualId",target = "id")
    @Mapping(source = "name",target = "name")
    @Mapping(source = "shortDescription",target = "shortDescription")
    @Mapping(source = "description",target = "description")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "banner", target = "banner")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "saleOff", target = "saleOff")
    @Mapping(source = "field", target = "field",qualifiedByName = "fromEntityToCategoryDto")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForClient")
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
    @Named("fromCourseVersioningEntityToCourseDto")
    CourseDto fromCourseVersioningEntityToCourseDto(CourseVersioning courseVersioning);

    @IterableMapping(elementTargetType = CourseDto.class,qualifiedByName = "fromCourseVersioningEntityToCourseDto")
    @BeanMapping(ignoreByDefault = true)
    List<CourseDto> fromCourseVersioningEntityToCourseDtoList(List<CourseVersioning> list);
}
