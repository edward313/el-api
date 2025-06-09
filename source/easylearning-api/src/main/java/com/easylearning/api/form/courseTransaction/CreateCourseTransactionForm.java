package com.easylearning.api.form.courseTransaction;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateCourseTransactionForm {
    @NotNull(message = "courseId can not null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
    @ApiModelProperty(name = "refSellCode")
    private String refSellCode;
    @ApiModelProperty(name = "browserCode")
    private String browserCode;

}
