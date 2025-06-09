package com.easylearning.api.form.lesson;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RetryProcessVideoLessonForm {
    @NotNull(message = "lessonId can not be null")
    @ApiModelProperty(name = "lessonId", required = true)
    private Long lessonId;
    @ApiModelProperty(name = "tsSecond")
    private String tsSecond;
    @ApiModelProperty(name = "cutStart")
    private String cutStart;
    @ApiModelProperty(name = "cutEnd")
    private String cutEnd;
    @ApiModelProperty(name = "thumbnail")
    private String thumbnail;
}
