package com.easylearning.api.form.booking;

import com.easylearning.api.validation.BookingState;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ApprovalBookingForm {
    @NotNull(message = "id can not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @NotNull(message = "state can not be null")
    @ApiModelProperty(name = "state", required = true)
    @BookingState
    private Integer state;
}
