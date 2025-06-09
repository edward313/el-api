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
public class ApproveExpertRegistrationForm {
    @NotNull(message = "id can not null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @ApiModelProperty(name = "email")
    @Email
    private String email;
    @PhoneNumber(allowNull = false)
    @ApiModelProperty(name = "phone",required = true)
    private String phone;
    @NotEmpty(message = "fullName can not be null")
    @ApiModelProperty(name = "fullName",example = "Tam Nguyen", required = true)
    private String fullName;
    private String avatarPath;
    @NotNull(message = "status can not be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
    @ApiModelProperty(name = "referralCode")
    private String referralCode ;
    @NotNull(message = "wardId can not null")
    @ApiModelProperty(name = "wardId", required = true)
    private Long wardId;
    @NotNull(message = "districtId can not null")
    @ApiModelProperty(name = "districtId", required = true)
    private Long districtId;
    @NotNull(message = "provinceId can not null")
    @ApiModelProperty(name = "provinceId", required = true)
    private Long provinceId;
    @NotEmpty(message = "address can not null")
    @ApiModelProperty(name = "address", required = true)
    private String address;
    @ApiModelProperty(name = "bankInfo")
    private String bankInfo;
    @ApiModelProperty(name = "identification")
    private String identification;
    @NotEmpty(message = "password cant not be null")
    @ApiModelProperty(name = "password", required = true)
    private String password;
}
