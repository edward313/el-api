package com.easylearning.api.form.booking;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateFreeCourseBookingForm {
    @NotEmpty(message = "sellCode can not be null")
    @ApiModelProperty(name = "sellCode", required = true)
    private String sellCode;
    @NotNull(message = "courseId can not be null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
}
