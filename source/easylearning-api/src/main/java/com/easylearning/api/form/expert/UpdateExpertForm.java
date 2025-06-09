package com.easylearning.api.form.expert;

import com.easylearning.api.validation.PhoneNumber;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UpdateExpertForm {

    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @ApiModelProperty(name = "phone",required = true)
    @PhoneNumber(allowNull = false)
    private String phone;
    @ApiModelProperty(name = "password")
    private String password;
    @NotEmpty(message = "fullName can not be null")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath ;
    @ApiModelProperty(name = "wardId")
    private Long wardId;
    @ApiModelProperty(name = "districtId")
    private Long districtId;
    @ApiModelProperty(name = "provinceId")
    private Long provinceId;
    @ApiModelProperty(name = "address")
    private String address;
    @NotNull(message = "status cannot be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
    @ApiModelProperty(name = "bankInfo")
    private String bankInfo;
    @ApiModelProperty(name = "identification")
    private String identification;
    @ApiModelProperty(name = "ordering")
    private Integer ordering;
    @ApiModelProperty(name = "isOutstanding")
    private Boolean isOutstanding;
}
