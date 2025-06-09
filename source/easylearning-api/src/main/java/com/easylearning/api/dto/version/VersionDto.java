package com.easylearning.api.dto.version;


import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.course.CourseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class VersionDto extends ABasicAdminDto {
    @ApiModelProperty(name = "courseId")
    private Long courseId;
    @ApiModelProperty(name = "date")
    private Date date;
    @ApiModelProperty(name = "versionCode")
    private Integer versionCode;
    @ApiModelProperty(name = "state")
    private Integer state;
    @ApiModelProperty(name = "reviewNote")
    private String reviewNote;
    @ApiModelProperty(name = "reasonReject")
    private String reasonReject;
}
