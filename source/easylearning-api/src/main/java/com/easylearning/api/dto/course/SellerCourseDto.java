package com.easylearning.api.dto.course;

import com.easylearning.api.dto.ResponseListDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SellerCourseDto {
    @ApiModelProperty(name = "courses")
    private ResponseListDto<List<CourseDto>> courses;
    @ApiModelProperty(name = "referralCode")
    private String referralCode;
}
