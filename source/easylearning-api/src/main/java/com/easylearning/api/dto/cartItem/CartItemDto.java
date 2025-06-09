package com.easylearning.api.dto.cartItem;


import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CartItemDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "student")
    private StudentDto student;
    @ApiModelProperty(name = "course")
    private CourseDto course;
    @ApiModelProperty(name = "timeCreated")
    private Date timeCreated;
    @ApiModelProperty(name = "sellCode")
    private String sellCode;
}
