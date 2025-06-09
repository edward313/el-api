package com.easylearning.api.service.payos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PayosConfirmForm {
    @ApiModelProperty(name = "success")
    Boolean success;
}
