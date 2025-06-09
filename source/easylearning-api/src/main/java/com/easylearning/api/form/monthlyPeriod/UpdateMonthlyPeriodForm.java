package com.easylearning.api.form.monthlyPeriod;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UpdateMonthlyPeriodForm {
    @NotNull(message = "id can not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @NotEmpty(message = "name can not be null")
    @ApiModelProperty(name = "name", required = true)
    private String name;
    @NotNull(message = "startDate can not be null")
    @ApiModelProperty(name = "startDate", required = true)
    private Date startDate;
    @NotNull(message = "endDate can not be null")
    @ApiModelProperty(name = "endDate", required = true)
    private Date endDate;
    @NotNull(message = "status can not be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;

}
