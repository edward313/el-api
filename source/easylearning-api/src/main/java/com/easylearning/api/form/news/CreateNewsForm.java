package com.easylearning.api.form.news;

import com.easylearning.api.model.Category;
import com.easylearning.api.validation.NewsKind;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateNewsForm {
    @NotNull(message = "categoryId cant not be null")
    @ApiModelProperty(name = "categoryId", required = true)
    private Long categoryId;

    @NotNull(message = "kind cant not be null")
    @NewsKind
    @ApiModelProperty(name = "kind", required = true)
    private Integer kind; // 1 introduction

    @NotEmpty(message = "content cant not be null")
    @ApiModelProperty(name = "content", required = true)
    private String content;

    @NotEmpty(message = "title cant not be null")
    @ApiModelProperty(name = "title", required = true)
    private String title;

    @ApiModelProperty(name = "description")
    private String description;

    @ApiModelProperty(name = "mobileBanner")
    private String mobileBanner;

    @ApiModelProperty(name = "banner")
    private String banner;

    @ApiModelProperty(name = "avatar")
    private String avatar;

    @ApiModelProperty(name = "status")
    private String status;
}
