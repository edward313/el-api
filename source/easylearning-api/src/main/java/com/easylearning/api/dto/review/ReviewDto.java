package com.easylearning.api.dto.review;

import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.expert.ExpertDto;
import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ReviewDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "message")
    private String message;
    @ApiModelProperty(name = "studentInfo")
    private StudentDto studentInfo;
    @ApiModelProperty(name = "courseInfo")
    private CourseDto courseInfo;
    @ApiModelProperty(name = "kind")
    private Integer kind;
    @ApiModelProperty(name = "star")
    private Integer star;
    @ApiModelProperty(name = "status")
    private Integer status;
    @ApiModelProperty(name = "createdDate")
    private Date createdDate;
    @ApiModelProperty(name = "expert")
    private ExpertDto expert;
}
