package com.easylearning.api.form.student;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckCouponForm {
    @NotEmpty(message = "coupon can not be empty")
    @ApiModelProperty(name = "couponCode", required = true)
    private String couponCode;
}
