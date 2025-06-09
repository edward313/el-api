package com.easylearning.api.form.student;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class UpgradeSellerForm {
    @NotEmpty(message = "referralCode cannot be null")
    @ApiModelProperty(name = "referralCode", required = true)
    private String referralCode;
}
