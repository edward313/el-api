package com.easylearning.api.form.expertRegistraion;

import com.easylearning.api.validation.PhoneNumber;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel
public class CreateExpertRegistrationForm {
    @NotEmpty(message = "address can not null")
    @ApiModelProperty(name = "address", required = true)
    private String address;
    @ApiModelProperty(name = "referralCode")
    private String referralCode;
    @ApiModelProperty(name = "introduction")
    private String introduction;
    @NotNull(message = "fieldId can not null")
    @ApiModelProperty(name = "field", required = true)
    private Long fieldId;
    @PhoneNumber(allowNull = false)
    @ApiModelProperty(name = "phone", required = true)
    private String phone;
    @Email
    @NotEmpty(message = "email can not be null")
    @ApiModelProperty(name = "email", required = true)
    private String email;
    @ApiModelProperty(name = "avatar")
    private String avatar;
    @NotNull(message = "wardId can not null")
    @ApiModelProperty(name = "wardId", required = true)
    private Long wardId;
    @NotNull(message = "districtId can not null")
    @ApiModelProperty(name = "districtId", required = true)
    private Long districtId;
    @NotNull(message = "provinceId can not null")
    @ApiModelProperty(name = "provinceId", required = true)
    private Long provinceId;
    @NotEmpty(message = "fullName can not null")
    @ApiModelProperty(name = "fullName",example = "Tam Nguyen",required = true)
    private String fullName;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
}
