package com.easylearning.api.form.registration;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateRegistrationForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @NotNull(message = "studentId can not be null")
    @ApiModelProperty(name = "studentId", required = true)
    private Long studentId;
    @NotNull(message = "courseId can not be null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
    @NotNull(message = "isFinished can not be null")
    @ApiModelProperty(name = "isFinished", required = true)
    private Boolean isFinished;
}
