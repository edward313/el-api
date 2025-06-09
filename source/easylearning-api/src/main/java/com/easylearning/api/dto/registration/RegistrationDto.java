package com.easylearning.api.dto.registration;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.student.StudentAdminDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
public class RegistrationDto extends ABasicAdminDto {
    @ApiModelProperty(name = "isFinished")
    private Boolean isFinished;
    @ApiModelProperty(name = "student")
    private StudentAdminDto student;
    @ApiModelProperty(name = "course")
    private CourseDto course;
}

