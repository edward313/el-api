package com.easylearning.api.form.student;

import com.easylearning.api.validation.PhoneNumber;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@ApiModel
public class SignUpStudentForm {
    @NotEmpty(message = "email can not be null")
    @ApiModelProperty(name = "email",required = true)
    @Email
    private String email;
    @ApiModelProperty(name = "phone")
    @PhoneNumber(allowNull = true)
    private String phone;
    @NotEmpty(message = "password cant not be null")
    @ApiModelProperty(name = "password", required = true)
    private String password;
    @NotEmpty(message = "fullName cant not be null")
    @ApiModelProperty(name = "fullName",example = "Tam Nguyen",required = true)
    private String fullName;
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
    @ApiModelProperty(name = "referralCode")
    private String referralCode;
    @ApiModelProperty(name = "identification")
    private String identification;
}
