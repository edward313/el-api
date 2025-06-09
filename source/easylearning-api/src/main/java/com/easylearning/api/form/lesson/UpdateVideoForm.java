package com.easylearning.api.form.lesson;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
public class UpdateVideoForm {
    @NotNull(message = "lessonId can not be null")
    @ApiModelProperty(name = "lessonId", required = true)
    private Long lessonId;
    @NotEmpty(message = "content can not be null")
    @ApiModelProperty(name = "content", required = true)
    private String content;
}
