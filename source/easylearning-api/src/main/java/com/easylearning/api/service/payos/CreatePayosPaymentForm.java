package com.easylearning.api.service.payos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreatePayosPaymentForm {

    @NotEmpty(message = "amount is required")
    @ApiModelProperty(name = "amount",required = true)
    private Integer amount;

    @NotEmpty(message = "cancelUrl is required")
    @ApiModelProperty(name = "cancelUrl",required = true)
    private String cancelUrl;

    @NotEmpty(message = "description is required")
    @ApiModelProperty(name = "description",required = true)
    private String description;

    @NotNull(message = "orderCode is required")
    @ApiModelProperty(name = "orderCode", required = true)
    private Long orderCode;

    @NotEmpty(message = "returnUrl is required")
    @ApiModelProperty(name = "returnUrl",required = true)
    private String returnUrl;
//  5 field trên phải sắp xếp theo thứ tự alphabet để check valid

    @NotEmpty(message = "signature is required")
    @ApiModelProperty(name = "signature",required = true)
    private String signature;

    @ApiModelProperty(name = "expiredAt")
    private Long expiredAt;

    @ApiModelProperty(name = "buyerName")
    private String buyerName;

    @ApiModelProperty(name = "buyerEmail")
    private String buyerEmail;

    @ApiModelProperty(name = "buyerPhone")
    private String buyerPhone;

    @ApiModelProperty(name = "buyerAddress")
    private String buyerAddress;

    @ApiModelProperty(name = "item")
    private List<PayosItemsForm> items;
}
