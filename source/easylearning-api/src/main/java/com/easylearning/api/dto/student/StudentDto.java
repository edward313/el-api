package com.easylearning.api.dto.student;

import com.easylearning.api.dto.account.AccountDto;
import com.easylearning.api.dto.nation.NationDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
public class StudentDto{
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "status")
    private Integer status;
    @ApiModelProperty(name = "birthday")
    private Date birthday;
    @ApiModelProperty(name = "account")
    private AccountDto account;
    @ApiModelProperty(name = "address")
    private String address;
    @ApiModelProperty(name = "ward")
    private NationDto ward;
    @ApiModelProperty(name = "district")
    private NationDto district;
    @ApiModelProperty(name = "province")
    private NationDto province;
    @ApiModelProperty(name = "isSeller")
    private Boolean isSeller;
    @ApiModelProperty(name = "referralCode")
    private String referralCode;
    @ApiModelProperty(name = "myReferralCode")
    private String myReferralCode;
    @ApiModelProperty(name = "isSystemSeller")
    private Boolean isSystemSeller;
    @ApiModelProperty(name = "bankInfo")
    private String bankInfo;
    @ApiModelProperty(name = "identification")
    private String identification;
}
