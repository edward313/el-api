package com.easylearning.api.form.nation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class UpdateNationForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @NotEmpty(message = "name cant not be null")
    @ApiModelProperty(name = "name", required = true)
    private String name;
    @NotEmpty(message = "postCode cant not be null")
    @ApiModelProperty(name = "postCode", required = true)
    private String postCode;
    @NotNull(message = "status cant not be null")
    @ApiModelProperty(name = "status", required = true)
    private Long status;
    @ApiModelProperty(name = "parentId")
    private Long parentId;
}
