package com.easylearning.api.dto.review;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AmountReviewDto {
    @ApiModelProperty(name = "kind")
    private Integer kind;
    @ApiModelProperty(name = "averageStar")
    private Float averageStar;
    @ApiModelProperty(name = "total")
    private Long total;
    @ApiModelProperty(name = "amountReview")
    private List<AmountReviewDetailDto> amountReview;
}
