package com.easylearning.api.dto.registerPeriod;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.registerPayout.RegisterPayoutDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RegisterPeriodDto extends ABasicAdminDto {
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "startDate")
    private Date startDate;
    @ApiModelProperty(name = "endDate")
    private Date endDate;
    @ApiModelProperty(name = "totalPayout")
    private Double totalPayout;
    @ApiModelProperty(name = "totalTaxMoney")
    private Double totalTaxMoney;
    @ApiModelProperty(name = "state")
    private Integer state;
    @ApiModelProperty(name = "excelName")
    private String excelName;
    @ApiModelProperty(name = "registerPayouts")
    private List<RegisterPayoutDto> registerPayoutDtos;
}
