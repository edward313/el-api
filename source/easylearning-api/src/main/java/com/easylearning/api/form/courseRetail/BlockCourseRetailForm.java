package com.easylearning.api.form.courseRetail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BlockCourseRetailForm {
    @NotNull(message = "courseRetailId can not be null")
    @ApiModelProperty(name = "courseRetailId", required = true)
    private Long courseRetailId;
}
