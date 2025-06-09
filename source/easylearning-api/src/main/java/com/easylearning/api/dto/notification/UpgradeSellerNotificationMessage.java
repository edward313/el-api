package com.easylearning.api.dto.notification;

import lombok.Data;

@Data
public class UpgradeSellerNotificationMessage {
    private Long notificationId;
    private String fullName;
    private String avatar;
}
