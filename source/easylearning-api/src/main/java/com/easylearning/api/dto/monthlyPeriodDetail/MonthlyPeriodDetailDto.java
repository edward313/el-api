package com.easylearning.api.dto.monthlyPeriodDetail;


import com.easylearning.api.dto.account.AccountDto;
import com.easylearning.api.dto.monthlyPeriod.MonthlyPeriodDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonthlyPeriodDetailDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "status")
    private Integer status;
    @ApiModelProperty(name = "account")
    private AccountDto account;
    @ApiModelProperty(name = "kind")
    private Integer kind;
    @ApiModelProperty(name = "totalMoney")
    private Double totalMoney;
    @ApiModelProperty(name = "totalRefMoney")
    private Double totalRefMoney;
    @ApiModelProperty(name = "period")
    private MonthlyPeriodDto period;
    @ApiModelProperty(name = "state")
    private Integer state;
}
