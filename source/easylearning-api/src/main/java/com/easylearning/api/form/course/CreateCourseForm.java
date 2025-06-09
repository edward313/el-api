package com.easylearning.api.form.course;

import com.easylearning.api.validation.CourseKind;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateCourseForm {
    @NotEmpty(message = "name can not be null")
    @ApiModelProperty(name = "name", required = true)
    private String name;

    @NotEmpty(message = "description can not be null")
    @ApiModelProperty(name = "description", required = true)
    private String description;

    @NotEmpty(message = "shortDescription can not be null")
    @ApiModelProperty(name = "shortDescription", required = true)
    private String shortDescription;

    @NotEmpty(message = "avatar can not be null")
    @ApiModelProperty(name = "avatar", required = true)
    private String avatar;

    @NotEmpty(message = "banner can not be null")
    @ApiModelProperty(name = "banner", required = true)
    private String banner;
    @Min(value = 0, message = "price must be greater than or equal to 0")
    @NotNull(message = "price can not be null")
    @ApiModelProperty(name = "price", required = true)
    private Double price;

    @Min(value = 0, message = "saleOff must be greater than or equal to 0")
    @Max(value = 100, message = "saleOff must be less than or equal to 100")
    @ApiModelProperty(name = "saleOff")
    private Integer saleOff;

    @NotNull(message = "fieldId can not be null")
    @ApiModelProperty(name = "fieldId", required = true)
    private Long fieldId;

    @NotNull(message = "kind can not be null")
    @CourseKind
    @ApiModelProperty(name = "kind", required = true)
    private Integer kind;

    @ApiModelProperty(name = "childrenDetails")
    private ChildrenDetailForm[] childrenDetails;

    @ApiModelProperty(name = "isSellerCourse")
    private Boolean isSellerCourse;
}
