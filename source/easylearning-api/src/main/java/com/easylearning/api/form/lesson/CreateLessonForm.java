package com.easylearning.api.form.lesson;

import com.easylearning.api.validation.LessonKind;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class CreateLessonForm {
    @NotNull(message = "courseId can not be null")
    @ApiModelProperty(name = "courseId", required = true)
    private Long courseId;
    @NotEmpty(message = "name can not be null")
    @ApiModelProperty(name = "name", required = true)
    private String name;
    @ApiModelProperty(name = "isPreview")
    private Boolean isPreview;
    @LessonKind
    @NotNull(message = "kind can not be null")
    @ApiModelProperty(name = "kind", required = true)
    private Integer kind;
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
