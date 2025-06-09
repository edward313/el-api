package com.easylearning.api.form.registerPayout;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateRegisterPayoutForm {
    @NotNull(message = "money can not be null")
    @ApiModelProperty(name = "money", required = true)
    private Double money;

    @ApiModelProperty(name = "note")
    private String note;
}
