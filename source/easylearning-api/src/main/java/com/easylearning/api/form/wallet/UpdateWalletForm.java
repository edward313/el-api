package com.easylearning.api.form.wallet;

import com.easylearning.api.validation.WalletKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
@ApiModel
public class UpdateWalletForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long  id;
    @NotNull(message = "kind can not be null")
    @WalletKind
    @ApiModelProperty(name = "kind", required = true)
    private Integer kind;
    @NotEmpty(message = "balance can not be null")
    @ApiModelProperty(name = "balance", required = true)
    private String balance;
    @NotEmpty(message = "walletNumber can not be null")
    @ApiModelProperty(name = "walletNumber", required = true)
    private String walletNumber;
    @NotNull(message = "status cannot be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
}
