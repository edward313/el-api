package com.easylearning.api.dto.course;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.category.CategoryDto;
import com.easylearning.api.dto.category.ElasticCategoryDto;
import com.easylearning.api.dto.expert.ElasticExpertDto;
import com.easylearning.api.dto.expert.ExpertDto;
import com.easylearning.api.dto.lesson.LessonDetailDto;
import com.easylearning.api.dto.version.VersionDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CourseDto extends ABasicAdminDto {
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "isBuy")
    private Boolean isBuy;
    @ApiModelProperty(name = "isSystemCourse")
    private Boolean isSystemCourse;
    @ApiModelProperty(name = "averageStar")
    private Float averageStar;
    @ApiModelProperty(name = "totalReview")
    private Integer totalReview;
    @ApiModelProperty(name = "totalLesson")
    private Integer totalLesson;
    @ApiModelProperty(name = "totalStudyTime")
    private Long totalStudyTime;
    @ApiModelProperty(name = "soldQuantity")
    private Integer soldQuantity;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "shortDescription")
    private String shortDescription;
    @ApiModelProperty(name = "avatar")
    private String avatar;
    @ApiModelProperty(name = "banner")
    private String banner;
    @ApiModelProperty(name = "price")
    private Double price;
    @ApiModelProperty(name = "saleOff")
    private Integer saleOff;
    @ApiModelProperty(name = "field")
    private CategoryDto field;
    @ApiModelProperty(name = "expert")
    private ExpertDto expert;
    @ApiModelProperty(name = "field")
    private ElasticCategoryDto elasticField;
    @ApiModelProperty(name = "expert")
    private ElasticExpertDto elasticExpert;
    @ApiModelProperty(name = "kind")
    private Integer kind;
    @ApiModelProperty(name = "isSellerCourse")
    private Boolean isSellerCourse;
    @ApiModelProperty(name = "percent")
    private Float percent;
    @ApiModelProperty(name = "isProcessing")
    private Boolean isProcessing;
    @ApiModelProperty(name = "lessons")
    private List<LessonDetailDto> lessons;
    @ApiModelProperty(name = "comboList")
    private List<ComboDto> comboList;
    @ApiModelProperty(name ="version")
    private VersionDto version;
    @ApiModelProperty(name ="hasPublishedVersion")
    private Boolean hasPublishedVersion;
}
