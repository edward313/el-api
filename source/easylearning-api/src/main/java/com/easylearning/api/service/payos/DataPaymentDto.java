package com.easylearning.api.service.payos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DataPaymentDto {
    @ApiModelProperty(name = "accountNumber")
    private String accountNumber;
    @ApiModelProperty(name = "accountName")
    private String accountName;
    @ApiModelProperty(name = "amount")
    private Integer amount;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "orderCode")
    private String orderCode;
    @ApiModelProperty(name = "currency")
    private String currency;
    @ApiModelProperty(name = "paymentLinkId")
    private String paymentLinkId;
    @ApiModelProperty(name = "status")
    private String status;
    @ApiModelProperty(name = "checkoutUrl")
    private String checkoutUrl;
    @ApiModelProperty(name = "qrCode")
    private String qrCode;
    @ApiModelProperty(name = "expiredAt")
    private String expiredAt;
}
