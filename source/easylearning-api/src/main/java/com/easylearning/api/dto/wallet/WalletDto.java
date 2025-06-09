package com.easylearning.api.dto.wallet;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.account.AccountDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WalletDto extends ABasicAdminDto {
    @ApiModelProperty(name = "walletNumber")
    private String walletNumber;
    @ApiModelProperty(name = "balance")
    private Double balance;
    @ApiModelProperty(name = "minMoneyOut")
    private Double minMoneyOut;
    @ApiModelProperty(name = "minBalance")
    private Double minBalance;
    @ApiModelProperty(name = "kind")
    private Integer kind;
    @ApiModelProperty(name = "account")
    private AccountDto account;
    @ApiModelProperty(name = "taxPercent")
    private Integer taxPercent;
}
