package com.easylearning.api.form.expert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@ApiModel
public class UpdateExpertProfileForm {
    @ApiModelProperty(name = "newPassword")
    private String newPassword;

    @NotEmpty(message = "oldPassword cannot be empty")
    @ApiModelProperty(name = "oldPassword",required = true)
    private String oldPassword;

    @ApiModelProperty(name = "avatar")
    private String avatar;

    @ApiModelProperty(name = "birthday")
    private Date birthday;

    @ApiModelProperty(name = "bankInfo")
    private String bankInfo;

    @ApiModelProperty(name = "identification")
    private String identification;

    @ApiModelProperty(name = "wardId")
    private Long wardId;

    @ApiModelProperty(name = "districtId")
    private Long districtId;

    @ApiModelProperty(name = "provinceId")
    private Long provinceId;

    @ApiModelProperty(name = "address")
    private String address;
}
