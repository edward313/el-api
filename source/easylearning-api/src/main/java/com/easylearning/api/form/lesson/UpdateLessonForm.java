package com.easylearning.api.form.lesson;

import com.easylearning.api.validation.LessonKind;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class UpdateLessonForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @NotEmpty(message = "name can not be null")
    @ApiModelProperty(name = "name", required = true)
    private String name;
    @ApiModelProperty(name = "isPreview")
    private Boolean isPreview;
    @ApiModelProperty(name = "content")
    private String content;
    @ApiModelProperty(name = "urlDocument")
    private String urlDocument;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "ordering")
    private Integer ordering;
    @NotNull(message = "status can not be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
}
