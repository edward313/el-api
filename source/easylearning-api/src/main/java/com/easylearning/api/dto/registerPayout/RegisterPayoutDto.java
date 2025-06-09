package com.easylearning.api.dto.registerPayout;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.account.AccountDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterPayoutDto extends ABasicAdminDto {
    @ApiModelProperty(name = "account")
    private AccountDto account;

    @ApiModelProperty(name = "accountKind")
    private Integer accountKind;

    @ApiModelProperty(name = "state")
    private Integer state;

    @ApiModelProperty(name = "money")
    private Double money;

    @ApiModelProperty(name = "note")
    private String note;

    @ApiModelProperty(name = "bankInfo")
    private String bankInfo;

    @ApiModelProperty(name = "taxPercent")
    private Integer taxPercent;
}

