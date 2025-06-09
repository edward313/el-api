package com.easylearning.api.form.review;


import com.easylearning.api.validation.ReviewKind;
import com.easylearning.api.validation.ReviewStar;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateReviewForm {
    @ApiModelProperty(name = "courseId")
    private Long courseId;

    @NotNull(message = "kind can not null")
    @ReviewKind
    @ApiModelProperty(name = "kind", required = true)
    private Integer kind;

    @NotNull(message = "star cannot be null")
    @ApiModelProperty(name = "star", required = true)
    @ReviewStar
    private Integer star;

    @ApiModelProperty(name = "message")
    private String message;

    @ApiModelProperty(name = "expertId")
    private Long expertId;

    @ApiModelProperty(name = "studentId")
    private Long studentId;
}
