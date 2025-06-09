package com.easylearning.api.form.monthlyPeriod;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReCalculateMonthlyPeriodForm {
    @NotNull(message = "periodId can not be null")
    @ApiModelProperty(name = "periodId")
    private Long periodId;
}
