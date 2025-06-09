package com.easylearning.api.dto.booking;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateBookingDto {
    @ApiModelProperty(name = "bookingId")
    private Long bookingId;
}
