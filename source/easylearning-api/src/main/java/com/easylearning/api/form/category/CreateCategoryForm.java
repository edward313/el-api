package com.easylearning.api.form.category;


import com.easylearning.api.validation.CategoryKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CreateCategoryForm {
    @NotEmpty(message = "name cant not be null")
    @ApiModelProperty(name = "name", required = true)
    private String name;

    @ApiModelProperty(name = "description", required = false)
    private String description;

    @ApiModelProperty(name = "image", required = false)
    private String image;

    @NotNull(message = "ordering cannot be null")
    @ApiModelProperty(name = "ordering", required = true)
    private Integer ordering;

    @ApiModelProperty(name = "kind", required = true)
    @CategoryKind(allowNull = false)
    private Integer kind;

}
