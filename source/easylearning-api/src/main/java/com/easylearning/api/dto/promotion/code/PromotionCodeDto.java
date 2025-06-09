package com.easylearning.api.dto.promotion.code;

import com.easylearning.api.dto.promotion.PromotionDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PromotionCodeDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "status")
    private Long status;
    @ApiModelProperty(name = "code")
    private String code;
    @ApiModelProperty(name = "quantityUsed")
    private Integer quantityUsed;
}
