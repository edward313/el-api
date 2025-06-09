package com.easylearning.api.form.cartItem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateCartItemForm {
    @NotNull(message = "courseId can not be null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
    @ApiModelProperty(name = "sellCode")
    private String sellCode;
}
