package com.easylearning.api.form.expert;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class DeleteExpertFolderForm {
    @NotNull(message = "expertId is required")
    @ApiModelProperty(name = "expertId", required = true)
    private Long expertId;
}
