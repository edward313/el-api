package com.easylearning.api.dto.expert;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExpertInfoDto {
    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "fullName")
    private String fullName;

    @ApiModelProperty(name = "avatar")
    private String avatar;

    @ApiModelProperty(name = "cover")
    private String cover;

    @ApiModelProperty(name = "introduction")
    private String introduction;

    @ApiModelProperty(name = "totalCourse")
    private String totalCourse;

    @ApiModelProperty(name = "totalLessonTime")
    private String totalLessonTime;

    @ApiModelProperty(name = "totalStudent")
    private String totalStudent;

    @ApiModelProperty(name = "ordering")
    private Integer ordering;

    @ApiModelProperty(name = "isOutstanding")
    private Boolean isOutstanding;

    @ApiModelProperty(name = "identification")
    private String identification;
}
