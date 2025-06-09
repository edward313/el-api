package com.easylearning.api.form.booking;

import com.easylearning.api.validation.BookingPaymentMethod;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateBookingForm {
    @ApiModelProperty(name = "promotionCode")
    private String promotionCode;
    @BookingPaymentMethod
    @NotNull(message = "paymentMethod can not be null")
    @ApiModelProperty(name = "paymentMethod", required = true)
    private Integer paymentMethod;
    @ApiModelProperty(name = "paymentData")
    private String paymentData;
    @ApiModelProperty(name = "sellCode")
    private String sellCode;
}
