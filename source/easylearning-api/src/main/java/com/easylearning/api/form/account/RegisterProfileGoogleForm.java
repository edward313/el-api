package com.easylearning.api.form.account;

import com.easylearning.api.validation.PhoneNumber;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@ApiModel
public class RegisterProfileGoogleForm {
    @NotEmpty(message = "code can not be null")
    @ApiModelProperty(name = "code", required = true)
    private String code;
    @NotEmpty(message = "platformUserId can not be null")
    @ApiModelProperty(name = "platformUserId", required = true)
    private String platformUserId;
    @PhoneNumber(allowNull = true)
    @ApiModelProperty(name = "phone")
    private String phone;
    @NotEmpty(message = "password can not be null")
    @ApiModelProperty(name = "password", required = true)
    private String password;
    @ApiModelProperty(name = "address")
    private String address;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
    @ApiModelProperty(name = "referralCode")
    private String referralCode;
    @ApiModelProperty(name = "identification")
    private String identification;
}
