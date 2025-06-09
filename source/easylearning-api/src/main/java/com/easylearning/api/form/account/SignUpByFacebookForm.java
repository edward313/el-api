package com.easylearning.api.form.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
@Data
@ApiModel
public class SignUpByFacebookForm {
    @NotEmpty(message = "code can not be null")
    @ApiModelProperty(name = "code", required = true)
    private String code;
    @NotEmpty(message = "phone can not be null")
    @ApiModelProperty(name = "phone",required = true)
    private String phone;
}
