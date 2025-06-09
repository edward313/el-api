package com.easylearning.api.dto.lessonVersioning;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.lesson.LessonDto;
import com.easylearning.api.dto.version.VersionDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LessonVersioningDetailDto extends ABasicAdminDto {
    @ApiModelProperty(name = "thumbnail")
    private String thumbnail;
    @ApiModelProperty(name = "status")
    private Integer status;
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "totalLesson")
    private Integer totalLesson;
    @ApiModelProperty(name = "totalStudyTime")
    private Long totalStudyTime;
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
    @ApiModelProperty(name ="isDone")
    private Boolean isDone = false;
    @ApiModelProperty(name ="course")
    private CourseDto course;
    @ApiModelProperty(name = "videoDuration")
    private Long videoDuration;
    @ApiModelProperty(name = "state")
    private Integer state;
    @ApiModelProperty(name = "videoUrl")
    private String videoUrl;
    @ApiModelProperty(name = "secondProgress")
    private Long secondProgress;
    @ApiModelProperty(name ="version")
    private VersionDto version;
    @ApiModelProperty(name ="visualId")
    private Long visualId;
}
