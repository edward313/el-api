package com.easylearning.api.dto.course;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ComboDto {
    @ApiModelProperty(name = "course")
    private CourseDto course;
}
