package com.easylearning.api.dto.categoryHome;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.category.CategoryDto;
import com.easylearning.api.dto.course.CourseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CategoryHomeAdminDto extends ABasicAdminDto {
    @ApiModelProperty(name = "category")
    private CategoryDto category;
    @ApiModelProperty(name = "course")
    private CourseDto course;
}
