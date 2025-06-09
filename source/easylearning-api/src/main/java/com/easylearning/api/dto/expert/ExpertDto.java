package com.easylearning.api.dto.expert;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.account.AccountDto;
import com.easylearning.api.dto.nation.NationDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ExpertDto extends ABasicAdminDto {
    @ApiModelProperty(name = "birthday")
    private Date birthday;

    @ApiModelProperty(name = "account")
    private AccountDto account;

    @ApiModelProperty(name = "referralCode")
    private String referralCode;

    @ApiModelProperty(name = "myReferralCode")
    private String  myReferralCode;

    @ApiModelProperty(name = "isSystemExpert")
    private Boolean isSystemExpert;

    @ApiModelProperty(name = "address")
    private String address;

    @ApiModelProperty(name = "ward")
    private NationDto ward;

    @ApiModelProperty(name = "district")
    private NationDto district;

    @ApiModelProperty(name = "province")
    private NationDto province;

    @ApiModelProperty(name = "bankInfo")
    private String bankInfo;

    @ApiModelProperty(name = "identification")
    private String identification;

    @ApiModelProperty(name = "ordering")
    private Integer ordering;

    @ApiModelProperty(name = "isOutstanding")
    private Boolean isOutstanding;

    @ApiModelProperty(name = "totalCourse")
    private Integer totalCourse;

    @ApiModelProperty(name = "totalLessonTime")
    private Integer totalLessonTime;

    @ApiModelProperty(name = "totalStudent")
    private Integer totalStudent;
}
