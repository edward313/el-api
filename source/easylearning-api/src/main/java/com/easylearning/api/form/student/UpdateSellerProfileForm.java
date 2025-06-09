package com.easylearning.api.form.student;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@ApiModel
public class UpdateSellerProfileForm {
    @NotEmpty(message = "fullName cant not be null")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
    @NotEmpty(message = "oldPassword cannot be empty")
    @ApiModelProperty(name = "oldPassword", required = true)
    private String oldPassword;
    @ApiModelProperty(name = "newPassword")
    private String newPassword;
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;
    @ApiModelProperty(name = "address")
    private String address;
    @ApiModelProperty(name = "wardId")
    private Long wardId;
    @ApiModelProperty(name = "districtId")
    private Long districtId;
    @ApiModelProperty(name = "provinceId")
    private Long provinceId;
    @ApiModelProperty(name = "bankInfo")
    private String bankInfo;
    @ApiModelProperty(name = "identification")
    private String identification;
    @ApiModelProperty(name = "referralCode")
    private String referralCode;
}
