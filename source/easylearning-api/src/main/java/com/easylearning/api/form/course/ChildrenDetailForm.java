package com.easylearning.api.form.course;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChildrenDetailForm {
    @ApiModelProperty(name = "childrenId")
    @NotNull(message = "childrenId can not be null")
    private Long childrenId;

    @ApiModelProperty(name = "priceInCombo")
    @NotNull(message = "priceInCombo can not be null")
    private Double priceInCombo;
}