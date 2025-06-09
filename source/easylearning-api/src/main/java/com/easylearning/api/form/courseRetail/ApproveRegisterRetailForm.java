package com.easylearning.api.form.courseRetail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ApproveRegisterRetailForm {
    @NotNull(message = "registerRetailId can not be null")
    @ApiModelProperty(name = "registerRetailId", required = true)
    private Long registerRetailId;
}
