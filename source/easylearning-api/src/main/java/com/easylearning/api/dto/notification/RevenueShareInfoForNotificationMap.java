package com.easylearning.api.dto.notification;

import lombok.Data;

@Data
public class RevenueShareInfoForNotificationMap {
    private Long userId;
    private String courseName;
    private Double totalRevenueMoney;
}
