package com.easylearning.api.form.version;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ApproveVersionForm {
    @NotNull(message = "versionId cant not be null")
    @ApiModelProperty(name = "versionId", required = true)
    Long versionId;
}
