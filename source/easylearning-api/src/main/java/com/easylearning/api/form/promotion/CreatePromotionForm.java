package com.easylearning.api.form.promotion;

import com.easylearning.api.validation.PromotionKind;
import com.easylearning.api.validation.PromotionState;
import com.easylearning.api.validation.PromotionType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class CreatePromotionForm {
    @NotEmpty(message = "name can not be null")
    @ApiModelProperty(name = "name", required = true)
    private String name;

    @ApiModelProperty(name = "description")
    private String description;

    @ApiModelProperty(name = "startDate")
    private Date startDate;

    @ApiModelProperty(name = "endDate")
    private Date endDate;

    @PromotionKind
    @NotNull(message = "kind can not be null")
    @ApiModelProperty(name = "kind", required = true)
    private Integer kind;

    @PromotionType
    @NotNull(message = "type can not be null")
    @ApiModelProperty(name = "type", required = true)
    private Integer type;

    @PromotionState
    @NotNull(message = "state can not be null")
    @ApiModelProperty(name = "state", required = true)
    private Integer state;

    @ApiModelProperty(name = "discountValue")
    private Double discountValue;

    @ApiModelProperty(name = "limitValue")
    private Double limitValue;

    @NotNull(message = "quantity can not be null")
    @ApiModelProperty(name = "quantity", required = true)
    private Integer quantity;

    @NotNull(message = "status can not be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;

    @NotEmpty(message = "prefix can not be empty")
    @ApiModelProperty(name = "prefix", required = true)
    private String prefix;

    @NotNull(message = "numRandom can not be null")
    @ApiModelProperty(name = "numRandom", required = true)
    private Integer numRandom;
}
