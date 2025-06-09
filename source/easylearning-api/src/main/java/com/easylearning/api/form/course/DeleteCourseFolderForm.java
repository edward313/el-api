package com.easylearning.api.form.course;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class DeleteCourseFolderForm {
    @NotNull(message = "expertId is required")
    @ApiModelProperty(name = "expertId", required = true)
    private Long expertId;
    @NotNull(message = "courseId is required")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
}
