package com.easylearning.api.service.payos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PayosItemsForm {
    @ApiModelProperty(name = "name")
    private String name ;
    @ApiModelProperty(name = "quantity")
    private Integer quantity;
    @ApiModelProperty(name = "price")
    private Integer price;
}
