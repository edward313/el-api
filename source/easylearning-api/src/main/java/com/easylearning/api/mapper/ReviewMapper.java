package com.easylearning.api.mapper;

import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.review.ReviewAdminDto;
import com.easylearning.api.dto.review.ReviewDto;
import com.easylearning.api.form.review.CreateReviewForm;
import com.easylearning.api.form.review.UpdateReviewForm;
import com.easylearning.api.model.Course;
import com.easylearning.api.model.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",  unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {StudentMapper.class,CourseMapper.class, ExpertMapper.class})
public interface ReviewMapper {
    @Mapping(source = "star", target = "star")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "kind", target = "kind")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromCreateFormToEntity")
    Review fromCreateFormToEntity(CreateReviewForm createForm);

    @Mapping(source = "star", target = "star")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromUpdateFormToEntity")
    void fromUpdateFormToEntity(UpdateReviewForm updateForm, @MappingTarget Review review);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "star", target = "star")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "student", target = "studentInfo", qualifiedByName = "fromStudentToDtoForReview")
    @Mapping(source = "course", target = "courseInfo", qualifiedByName = "fromEntityToCourseDtoForClient")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForCategoryHomeClient")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToReviewAdminDto")
    ReviewAdminDto fromEntityToReviewAdminDto(Review review);

    @IterableMapping(elementTargetType = ReviewAdminDto.class, qualifiedByName = "fromEntityToReviewAdminDto")
    List<ReviewAdminDto> fromEntityListToAdminDtoList(List<Review> reviews);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "star", target = "star")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "course", target = "courseInfo", qualifiedByName = "fromEntityToCourseDtoForClient")
    @Mapping(source = "student", target = "studentInfo", qualifiedByName = "fromStudentToDtoForReview")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForCategoryHomeClient")
    @BeanMapping(ignoreByDefault = true,qualifiedByName = "afterMappingToReviewDtoForAutoComplete")
    @Named("fromEntityToDtoAutoComplete")
    ReviewDto fromEntityToDtoAutoComplete(Review review);
    @AfterMapping
    @Named("afterMappingToReviewDtoForAutoComplete")
    default void afterMappingToReviewDtoForAutoComplete(Review review, @MappingTarget ReviewDto reviewDto) {
        if (review.getExpert() != null && Boolean.TRUE.equals(review.getExpert().getIsSystemExpert())) {
            reviewDto.setExpert(null);
        }
    }

    @IterableMapping(elementTargetType = ReviewDto.class, qualifiedByName = "fromEntityToDtoAutoComplete")
    List<ReviewDto> fromEntityListToAutoCompleteList(List<Review> reviews);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "star", target = "star")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "course", target = "courseInfo", qualifiedByName = "fromEntityToCourseDtoForClient")
    @Mapping(source = "student", target = "studentInfo", qualifiedByName = "fromStudentToDtoForReview")
    @Mapping(source = "expert", target = "expert",qualifiedByName = "fromEntityToExpertDtoForCategoryHomeClient")
    @BeanMapping(ignoreByDefault = true,qualifiedByName = "afterMappingToGetMyDto")
    @Named("fromEntityToGetMyDto")
    ReviewDto fromEntityToGetMyDto(Review review);
    @AfterMapping
    @Named("afterMappingToGetMyDto")
    default void afterMappingToGetMyDto(Review review, @MappingTarget ReviewDto reviewDto) {
        if (review.getExpert() != null && Boolean.TRUE.equals(review.getExpert().getIsSystemExpert())) {
            reviewDto.setExpert(null);
        }
    }

    @IterableMapping(elementTargetType = ReviewDto.class, qualifiedByName = "fromEntityToGetMyDto")
    List<ReviewDto> fromEntityToGetMyReviewDtoList(List<Review> reviews);
}
