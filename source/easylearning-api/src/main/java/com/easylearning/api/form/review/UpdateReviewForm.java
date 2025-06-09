package com.easylearning.api.form.review;

import com.easylearning.api.validation.ReviewKind;
import com.easylearning.api.validation.ReviewStar;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateReviewForm {

    @NotNull(message = "id can not empty")
    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @NotNull(message = "status cannot be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;

    @NotNull(message = "star cannot be null")
    @ApiModelProperty(name = "star", required = true)
    @ReviewStar
    private Integer star;

    @ApiModelProperty(name = "message")
    private String message;
    @ApiModelProperty(name = "courseId")
    private Long courseId;
}
