package com.easylearning.api.dto.promotion;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.promotion.code.PromotionCodeDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PromotionDto extends ABasicAdminDto {
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "startDate")
    private Date startDate;
    @ApiModelProperty(name = "endDate")
    private Date endDate;
    @ApiModelProperty(name = "kind")
    private Integer kind;
    @ApiModelProperty(name = "type")
    private Integer type;
    @ApiModelProperty(name = "discountValue")
    private Double discountValue;
    @ApiModelProperty(name = "limitValue")
    private Double limitValue;
    @ApiModelProperty(name = "quantity")
    private Integer quantity;
    @ApiModelProperty(name = "state")
    private Integer state;
    @ApiModelProperty(name = "prefix")
    private String prefix;
    @ApiModelProperty(name = "numRandom")
    private Integer numRandom;
    @ApiModelProperty(name = "promotionCodes")
    private List<PromotionCodeDto> promotionCodes;
}
