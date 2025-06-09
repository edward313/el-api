package com.easylearning.api.dto.monthlyPeriod;


import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.monthlyPeriodDetail.MonthlyPeriodDetailAdminDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MonthlyPeriodAdminDto extends ABasicAdminDto {
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
    @ApiModelProperty(name = "state")
    private List<MonthlyPeriodDetailAdminDto> periodDetails;
    @ApiModelProperty(name = "excelName")
    private String excelName;
}
