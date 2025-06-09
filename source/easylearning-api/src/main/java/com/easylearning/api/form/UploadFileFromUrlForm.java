package com.easylearning.api.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UploadFileFromUrlForm {
    @NotEmpty(message = "type is required")
    @ApiModelProperty(name = "type", required = true)
    private String type ;
    @NotEmpty(message = "url is required")
    @ApiModelProperty(name = "url", required = true)
    private String url;
    @NotNull(message = "accountId is required")
    @ApiModelProperty(name = "accountId", required = true)
    private Long accountId;
}
