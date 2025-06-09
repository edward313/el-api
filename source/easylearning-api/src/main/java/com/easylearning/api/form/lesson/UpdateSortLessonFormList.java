package com.easylearning.api.form.lesson;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateSortLessonFormList {
    @NotNull(message = "sortForms can not be null")
    @ApiModelProperty(name = "sortForms", required = true)
    List<@Valid UpdateSortLessonForm> sortForms;
    @NotNull(message = "courseId can not be null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
}
