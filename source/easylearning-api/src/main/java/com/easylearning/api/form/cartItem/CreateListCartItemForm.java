package com.easylearning.api.form.cartItem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateListCartItemForm {
    @NotNull(message = "cartItems can not be null")
    @ApiModelProperty(name = "cartItems", required = true)
    List<@Valid CreateCartItemForm> cartItems;
}
