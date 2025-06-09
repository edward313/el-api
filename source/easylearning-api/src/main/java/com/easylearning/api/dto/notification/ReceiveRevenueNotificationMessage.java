package com.easylearning.api.dto.notification;

import lombok.Data;

@Data
public class ReceiveRevenueNotificationMessage {
    private Long notificationId;
    private Double revenueMoney;
    private String courseName;
}
