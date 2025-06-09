package com.easylearning.api.form.expert;

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
public class CreateExpertForm {
    @ApiModelProperty(name = "email")
    @Email
    private String email;
    @ApiModelProperty(name = "phone",required = true)
    @PhoneNumber
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
    @ApiModelProperty(name = "wardId", required = true)
    private Long wardId;
    @ApiModelProperty(name = "districtId", required = true)
    private Long districtId;
    @ApiModelProperty(name = "provinceId", required = true)
    private Long provinceId;
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
