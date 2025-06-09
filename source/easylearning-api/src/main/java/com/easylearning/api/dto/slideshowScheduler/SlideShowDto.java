package com.easylearning.api.dto.slideshowScheduler;

import com.easylearning.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SlideShowDto extends ABasicAdminDto {
    @ApiModelProperty(name = "title")
    private String title;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "image")
    private String image;
    @ApiModelProperty(name = "mobileImage")
    private String mobileImage;
    @ApiModelProperty(name = "url")
    private String url;
    @ApiModelProperty(name = "ordering")
    private Integer ordering;
    @ApiModelProperty(name = "action")
    private Integer action;
}
