package com.easylearning.api.form.notification;

import com.easylearning.api.validation.NotificationKind;
import com.easylearning.api.validation.NotificationState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class UpdateNotificationForm {

    @NotNull(message = "id cant not be null")
    @ApiModelProperty( name="id",required = true)
    private Long id;

    @NotNull(message = "idUser cant not be null")
    @ApiModelProperty( name="idUser", required = true)
    private Long idUser;

    @ApiModelProperty( name="refId")
    private String refId;

    @NotNull(message = "state cant not be null")
    @ApiModelProperty(  name="state" ,required = true)
    @NotificationState
    private Integer state;

    @ApiModelProperty( name="kind",required = true)
    @NotificationKind
    @NotNull(message = "kind cant not be null")
    private Integer kind;

    @NotEmpty(message = "msg cant not be empty")
    @ApiModelProperty( name="msg" ,required = true)
    private String msg;

    @NotNull(message = "status cant not be null")
    @ApiModelProperty( name="status",required = true)
    private Integer status;

}
