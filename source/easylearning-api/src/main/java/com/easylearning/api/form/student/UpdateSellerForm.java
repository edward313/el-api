package com.easylearning.api.form.student;

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
public class UpdateSellerForm {

    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long  id;
    @NotEmpty(message = "fullName cant not be null")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
    @ApiModelProperty(name = "phone")
    @PhoneNumber(allowNull = true)
    private String phone;
    @ApiModelProperty(name = "password")
    private String password;
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;
    @NotNull(message = "status cannot be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
    @ApiModelProperty(name = "address")
    private String address;
    @ApiModelProperty(name = "wardId")
    private Long wardId;
    @ApiModelProperty(name = "districtId")
    private Long districtId;
    @ApiModelProperty(name = "provinceId")
    private Long provinceId;
    @NotNull(message = "isSeller cannot be null")
    @ApiModelProperty(name = "isSeller", required = true)
    private Boolean isSeller;
    @ApiModelProperty(name = "bankInfo")
    private String bankInfo;
    @ApiModelProperty(name = "identification")
    private String identification;
}
