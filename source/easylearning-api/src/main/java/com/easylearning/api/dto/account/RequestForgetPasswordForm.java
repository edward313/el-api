package com.easylearning.api.dto.account;

import com.easylearning.api.validation.AppKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class RequestForgetPasswordForm {

    @NotEmpty(message = "Email can not be null.")
    @Email
    @ApiModelProperty(name = "email", required = true)
    private String email;
    @AppKind
    @NotNull(message = "app can not be null")
    @ApiModelProperty(name = "app", required = true)
    private Integer app;
    @ApiModelProperty(name = "accountKind")
    private Integer accountKind;
}
