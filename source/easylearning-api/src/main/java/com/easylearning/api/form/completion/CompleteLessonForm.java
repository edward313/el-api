package com.easylearning.api.form.completion;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CompleteLessonForm {
    @NotNull(message = "courseId can not be null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
    @NotNull(message = "lessonId can not be null")
    @ApiModelProperty(name = "lessonId", required = true)
    private Long lessonId;
}
