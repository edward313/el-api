package com.easylearning.api.form.completion;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CreateCompletionForm {
    @NotNull(message = "courseId can not be null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
    @NotNull(message = "lessonId can not be null")
    @ApiModelProperty(name = "lessonId", required = true)
    private Long lessonId;
    @Min(value = 0)
    @NotNull(message = "secondProgress can not be null")
    @ApiModelProperty(name = "secondProgress", required = true)
    private Long secondProgress;
}
