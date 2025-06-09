package com.easylearning.api.dto.monthlyPeriod;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class MonthlyPeriodDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "status")
    private Integer status;
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "startDate")
    private Date startDate;
    @ApiModelProperty(name = "endDate")
    private Date endDate;
    @ApiModelProperty(name = "systemRevenue")
    private Double systemRevenue;
    @ApiModelProperty(name = "totalPayout")
    private Double totalPayout;
    @ApiModelProperty(name = "totalSaleOffMoney")
    private Double totalSaleOffMoney;
    @ApiModelProperty(name = "state")
    private Integer state;
}
