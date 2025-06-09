package com.easylearning.api.form.slideshowScheduler;

import com.easylearning.api.validation.SlideShowAction;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateSlideShowForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;

    @NotEmpty(message = "image can not be null")
    @ApiModelProperty(name = "image", required = true)
    private String image;

    @ApiModelProperty(name = "mobileImage")
    private String mobileImage;

    @ApiModelProperty(name = "title")
    private String title;

    @ApiModelProperty(name = "description")
    private String description;

    @ApiModelProperty(name = "url")
    private String url;

    @NotNull(message = "action can not be null")
    @SlideShowAction
    @ApiModelProperty(name = "action", required = true)
    private Integer action;

    @ApiModelProperty(name = "description")
    private Integer ordering;

    @ApiModelProperty(name = "status")
    private Integer status;
}
