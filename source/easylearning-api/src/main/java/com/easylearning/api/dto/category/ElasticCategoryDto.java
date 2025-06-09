package com.easylearning.api.dto.category;

import com.easylearning.api.dto.ABasicAdminDto;
import lombok.Data;

@Data
public class ElasticCategoryDto extends ABasicAdminDto {
    private String name;
    private String description;
    private String image;
    private Integer ordering;
    private Integer kind;
}