package com.easylearning.api.dto.news;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.category.CategoryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewsDto extends ABasicAdminDto {
    @ApiModelProperty(name = "category")
    private CategoryDto category;

    @ApiModelProperty(name = "kind")
    private Integer kind; // 1 introduction

    @ApiModelProperty(name = "content")
    private String content;

    @ApiModelProperty(name = "title")
    private String title;

    @ApiModelProperty(name = "description")
    private String description;

    @ApiModelProperty(name = "mobileBanner")
    private String mobileBanner;

    @ApiModelProperty(name = "banner")
    private String banner;

    @ApiModelProperty(name = "avatar")
    private String avatar;
}
