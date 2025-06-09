package com.easylearning.api.form.version;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class EditRejectVersionForm {
    @NotNull(message = "versionId cant not be null")
    @ApiModelProperty(name = "versionId", required = true)
    Long versionId;
}
