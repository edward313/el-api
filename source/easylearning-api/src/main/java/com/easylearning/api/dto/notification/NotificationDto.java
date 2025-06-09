package com.easylearning.api.dto.notification;

import com.easylearning.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NotificationDto extends ABasicAdminDto {
    @ApiModelProperty(name = "idUser")
    private Long idUser;
    @ApiModelProperty(name = "state")
    private Integer state;
    @ApiModelProperty(name = "kind")
    private Integer kind;
    @ApiModelProperty(name = "msg")
    private String msg;
    @ApiModelProperty(name = "refId")
    private String refId;
}
