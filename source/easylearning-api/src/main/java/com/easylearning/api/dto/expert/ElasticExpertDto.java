package com.easylearning.api.dto.expert;

import com.easylearning.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ElasticExpertDto extends ABasicAdminDto {
    @ApiModelProperty(name = "birthday")
    private Date birthday;

    // use for Elastic
    @ApiModelProperty(name ="fullName")
    private String fullName;

    @ApiModelProperty(name = "referralCode")
    private String referralCode;

    @ApiModelProperty(name = "isSystemExpert")
    private Boolean isSystemExpert;
}