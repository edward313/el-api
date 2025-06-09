package com.easylearning.api.form.version;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class RejectVersionForm {
    @NotNull(message = "versionId cant not be null")
    @ApiModelProperty(name = "versionId", required = true)
    Long versionId;
    @NotEmpty(message = "reason can not be empty")
    @ApiModelProperty(name = "reason", required = true)
    private String reason;
}
