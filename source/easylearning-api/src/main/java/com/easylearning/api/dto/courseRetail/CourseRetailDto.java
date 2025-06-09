package com.easylearning.api.dto.courseRetail;


import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CourseRetailDto{
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "status")
    private Integer status;
    @ApiModelProperty(name = "seller")
    private StudentDto seller;
    @ApiModelProperty(name = "course")
    private CourseDto course;
}
