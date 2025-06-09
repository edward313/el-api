package com.easylearning.api.service.payos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PayosDataForm {
    @ApiModelProperty(name = "accountNumber")
    private String accountNumber;
    @ApiModelProperty(name = "amount")
    private Integer amount;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "reference")
    private String reference;
    @ApiModelProperty(name = "transactionDateTime")
    private String transactionDateTime;
    @ApiModelProperty(name = "virtualAccountNumber")
    private String virtualAccountNumber;
    @ApiModelProperty(name = "counterAccountBankId")
    private String counterAccountBankId;
    @ApiModelProperty(name = "counterAccountBankName")
    private String counterAccountBankName;
    @ApiModelProperty(name = "counterAccountName")
    private String counterAccountName;
    @ApiModelProperty(name = "counterAccountNumber")
    private String counterAccountNumber;
    @ApiModelProperty(name = "virtualAccountName")
    private String virtualAccountName;
    @ApiModelProperty(name = "currency")
    private String currency;
    @ApiModelProperty(name = "orderCode")
    private Long orderCode;
    @ApiModelProperty(name = "paymentLinkId")
    private String paymentLinkId;
    @ApiModelProperty(name = "code")
    private String code;
    @ApiModelProperty(name = "desc")
    private String desc;
}
