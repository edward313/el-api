package com.easylearning.api.form.lesson;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateSortLessonForm {
    @NotNull(message = "id can not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @NotNull(message = "ordering can not be null")
    @ApiModelProperty(name = "ordering", required = true)
    private Integer ordering;
}
