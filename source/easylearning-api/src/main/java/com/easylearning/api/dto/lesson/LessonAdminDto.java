package com.easylearning.api.dto.lesson;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.version.VersionDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



@Data
public class LessonAdminDto extends ABasicAdminDto {
    @ApiModelProperty(name = "name")
    private String name;

    @ApiModelProperty(name = "thumbnail")
    private String thumbnail;

    @ApiModelProperty(name = "duration")
    private Integer duration;

    @ApiModelProperty(name = "isPreview")
    private Boolean isPreview;

    @ApiModelProperty(name = "kind")
    private Integer kind;

    @ApiModelProperty(name = "content")
    private String content;

    @ApiModelProperty(name = "urlDocument")
    private String urlDocument;

    @ApiModelProperty(name = "description")
    private String description;

    @ApiModelProperty(name = "ordering")
    private Integer ordering;

    @ApiModelProperty(name ="course")
    private CourseDto course;

    @ApiModelProperty(name = "state")
    private Integer state;

    @ApiModelProperty(name = "videoDuration")
    private Long videoDuration;

    @ApiModelProperty(name = "videoUrl")
    private String videoUrl;

    @ApiModelProperty(name ="version")
    private VersionDto version;
}
