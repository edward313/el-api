package com.easylearning.api.dto.wallet;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckWalletBalanceDto {
    @ApiModelProperty(name = "balance")
    private Double balance;
}
