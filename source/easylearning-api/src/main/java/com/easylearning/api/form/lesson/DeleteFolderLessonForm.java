package com.easylearning.api.form.lesson;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteFolderLessonForm {
    @NotEmpty(message = "lessonId is required")
    @ApiModelProperty(name = "lessonId", required = true)
    private Long lessonId;

    @NotEmpty(message = "courseId is required")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;

    @NotEmpty(message = "expertId is required")
    @ApiModelProperty(name = "expertId", required = true)
    private Long expertId;
}
