package com.easylearning.api.dto.notification;

import lombok.Data;

@Data
public class ApproveAndRejectCourseNotificationMessage {
    private Long notificationId;
    private Long courseId;
    private String courseName;
}
