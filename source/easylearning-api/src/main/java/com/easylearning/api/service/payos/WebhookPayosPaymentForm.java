package com.easylearning.api.service.payos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WebhookPayosPaymentForm {
    @ApiModelProperty(name = "code")
    private String code;
    @ApiModelProperty(name = "desc")
    private String desc ;
    @ApiModelProperty(name = "data")
    private PayosDataForm data;
    @ApiModelProperty(name = "signature")
    private String signature;
}
