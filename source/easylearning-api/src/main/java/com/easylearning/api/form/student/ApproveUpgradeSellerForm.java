package com.easylearning.api.form.student;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ApproveUpgradeSellerForm {
    @NotNull(message = "studentId can not be null")
    @ApiModelProperty(name = "studentId", required = true)
    private Long studentId;
}
