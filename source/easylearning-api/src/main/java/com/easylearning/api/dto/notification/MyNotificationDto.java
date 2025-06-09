package com.easylearning.api.dto.notification;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MyNotificationDto {
    @ApiModelProperty(name = "totalUnread")
    private Long totalUnread;
    @ApiModelProperty(name = "content")
    private List<NotificationDto> content ;
    @ApiModelProperty(name = "totalPages")
    private long totalPages;
    @ApiModelProperty(name = "totalElements")
    private long totalElements;
}
