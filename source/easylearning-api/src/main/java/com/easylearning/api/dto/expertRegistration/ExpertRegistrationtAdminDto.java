package com.easylearning.api.dto.expertRegistration;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.category.CategoryDto;
import com.easylearning.api.dto.nation.NationDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
public class ExpertRegistrationtAdminDto extends ABasicAdminDto {
    @ApiModelProperty(name = "address")
    private String address;
    @ApiModelProperty(name = "ward")
    private NationDto ward;
    @ApiModelProperty(name = "district")
    private NationDto district;
    @ApiModelProperty(name = "province")
    private NationDto province;
    @ApiModelProperty(name = "referralCode")
    private String referralCode;
    @ApiModelProperty(name = "field")
    private CategoryDto field;
    @ApiModelProperty(name = "phone")
    private String phone;
    @ApiModelProperty(name = "email")
    private String email;
    @ApiModelProperty(name = "avatar")
    private String avatar;
    @ApiModelProperty(name = "introduction")
    private String introduction;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
    @ApiModelProperty(name = "fullName")
    private String fullName;
}
