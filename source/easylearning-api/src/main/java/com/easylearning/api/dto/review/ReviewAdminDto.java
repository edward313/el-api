package com.easylearning.api.dto.review;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.expert.ExpertDto;
import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReviewAdminDto extends ABasicAdminDto {
    @ApiModelProperty(name = "star")
    private Integer star;
    @ApiModelProperty(name = "message")
    private String message;
    @ApiModelProperty(name = "kind")
    private String kind;
    @ApiModelProperty(name = "studentInfo")
    private StudentDto studentInfo;
    @ApiModelProperty(name = "courseInfo")
    private CourseDto courseInfo;
    @ApiModelProperty(name = "expert")
    private ExpertDto expert;
}
