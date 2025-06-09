package com.easylearning.api.dto.notification;

import lombok.Data;

@Data
public class SignUpStudentNotificationMessage {
    private Long notificationId;
    private String fullName;
    private String avatar;
}
