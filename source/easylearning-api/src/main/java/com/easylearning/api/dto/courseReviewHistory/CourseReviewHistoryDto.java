package com.easylearning.api.dto.courseReviewHistory;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.version.VersionDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CourseReviewHistoryDto extends ABasicAdminDto {
    @ApiModelProperty(name = "date")
    private Date date;
    @ApiModelProperty(name = "state")
    private Integer state;
    @ApiModelProperty(name = "version")
    private VersionDto version;
    @ApiModelProperty(name = "reason")
    private String reason;
    @ApiModelProperty(name = "expertName")
    private String expertName;
    @ApiModelProperty(name ="course")
    private String courseName;
}
