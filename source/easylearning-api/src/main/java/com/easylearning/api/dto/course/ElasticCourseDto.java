package com.easylearning.api.dto.course;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.category.ElasticCategoryDto;
import com.easylearning.api.dto.expert.ElasticExpertDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ElasticCourseDto extends ABasicAdminDto {
    @ApiModelProperty(name = "name")
    private String name;
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
    private ElasticCategoryDto field;
    @ApiModelProperty(name = "expert")
    private ElasticExpertDto expert;
    @ApiModelProperty(name = "kind")
    private Integer kind;
}