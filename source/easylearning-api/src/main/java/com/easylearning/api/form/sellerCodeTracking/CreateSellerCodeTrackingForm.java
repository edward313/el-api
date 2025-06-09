package com.easylearning.api.form.sellerCodeTracking;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateSellerCodeTrackingForm {
    @NotEmpty(message = "sellCode can not be null")
    @ApiModelProperty(name = "sellCode", required = true)
    private String sellCode;
}
