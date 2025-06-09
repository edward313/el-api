package com.easylearning.api.form.courseRetail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RegisterRetailForm {
    @NotNull(message = "courseId can not be null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
}
