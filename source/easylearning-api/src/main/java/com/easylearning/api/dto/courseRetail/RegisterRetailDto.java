package com.easylearning.api.dto.courseRetail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegisterRetailDto {
    @ApiModelProperty(name = "refCode")
    private String refCode;
}
