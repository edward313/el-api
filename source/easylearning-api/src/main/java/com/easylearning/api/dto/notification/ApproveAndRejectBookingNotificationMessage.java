package com.easylearning.api.dto.notification;

import lombok.Data;

@Data
public class ApproveAndRejectBookingNotificationMessage {
    private Long notificationId;
    private Long bookingId;
    private Integer bookingState;
    private String code;
}
