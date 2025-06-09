package com.easylearning.api.form.version;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SubmitVersionForm {
    @NotNull(message = "versionId cant not be null")
    @ApiModelProperty(name = "versionId", required = true)
    Long versionId;
    @NotEmpty(message = "reviewNote can not be empty")
    @ApiModelProperty(name = "reviewNote", required = true)
    private String reviewNote;
}
