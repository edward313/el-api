package com.easylearning.api.form.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel
public class VerifyFacebookForm {
    @NotEmpty(message = "token can not be null")
    @ApiModelProperty(name = "token", required = true)
    private String token;
}