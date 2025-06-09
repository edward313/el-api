package com.easylearning.api.dto.cartItem;

import lombok.Data;

import java.util.List;

@Data
public class PaymentCartDto {
    private String bankInfo;
    private Boolean isUseSellCode;
    private Double couponMoney;
    private List<CartItemDto> cartItems;
}
