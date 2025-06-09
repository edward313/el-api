package com.easylearning.api.dto.booking;

import lombok.Data;

@Data
public class PaymentInfoDto {
    private Long bookingId;
    private String bankInfo;
    private Double totalMoney;
}
