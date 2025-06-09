package com.easylearning.api.dto.promotion;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckValidPromotionCodeDto {
    @ApiModelProperty(name = "isValid")
    private Boolean isValid;
    @ApiModelProperty(name = "quantityUsed")
    private Integer quantityUsed;
    @ApiModelProperty(name = "promotion")
    private PromotionDto promotion;
}
