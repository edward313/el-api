package com.easylearning.api.dto.completion;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.lesson.LessonDto;
import com.easylearning.api.dto.student.StudentAdminDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CompletionDto extends ABasicAdminDto {
    @ApiModelProperty(name = "dateFinished")
    private Date dateFinished;
    @ApiModelProperty(name = "student")
    private StudentAdminDto student;
    @ApiModelProperty(name = "course")
    private CourseDto course;
    @ApiModelProperty(name = "lesson")
    private LessonDto lesson;
    @ApiModelProperty(name = "isFinished")
    private Boolean isFinished;
    @ApiModelProperty(name = "secondProgress")
    private Long secondProgress;
}
