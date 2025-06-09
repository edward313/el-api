package com.easylearning.api.dto.revenueShare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MyRevenueDto implements MyRevenueDtoInterface {
    @ApiModelProperty(name = "totalMoney")
    private Double totalMoney;
    @ApiModelProperty(name = "totalPayoutMoney")
    private Double totalPayoutMoney;
}
