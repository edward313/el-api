package com.easylearning.api.dto.walletTransaction;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.wallet.WalletDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WalletTransactionDto extends ABasicAdminDto {
    @ApiModelProperty(name = "wallet")
    private WalletDto wallet;
    @ApiModelProperty(name = "balance")
    private Double money;
    @ApiModelProperty(name = "kind")
    private Integer kind;
    @ApiModelProperty(name = "state")
    private Integer state;
    @ApiModelProperty(name = "note")
    private String note;
    @ApiModelProperty(name = "lastBalance")
    private Double lastBalance;
}