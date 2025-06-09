package com.easylearning.api.dto.categoryHome;

import com.easylearning.api.dto.category.CategoryDto;
import com.easylearning.api.dto.course.CourseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CategoryHomeClientDto {
    @ApiModelProperty(name = "category")
    private CategoryDto category;
    @ApiModelProperty(name = "courses")
    private List<CourseDto> courses;
}
