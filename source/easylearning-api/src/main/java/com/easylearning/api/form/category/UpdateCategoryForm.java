package com.easylearning.api.form.category;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateCategoryForm {

    @NotNull(message = "categoryId cant not be null")
    @ApiModelProperty(name = "categoryId", required = true)
    private Long categoryId;

    @NotEmpty(message = "name cant not be null")
    @ApiModelProperty(name = "name", required = true)
    private String name;

    @ApiModelProperty(name = "description", required = false)
    private String description;

    @ApiModelProperty(name = "image", required = false)
    private String image;

    @NotNull(message = "ordering cant not be null")
    @ApiModelProperty(name = "ordering", required = true)
    private Integer ordering;

}
