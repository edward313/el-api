package com.easylearning.api.form.categoryHome;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateCategoryHomeForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @NotNull(message = "courseId cant not be null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
}
