package com.easylearning.api.form.lesson;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class MoveVideoFileForm {
    @NotEmpty(message = "url is required")
    @ApiModelProperty(name = "url", required = true)
    private String url;

    @NotNull(message = "courseId is required")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;

    @NotNull(message = "expertId is required")
    @ApiModelProperty(name = "expertId", required = true)
    private Long expertId;
}
