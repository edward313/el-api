package com.easylearning.api.dto.sellerCodeTracking;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SellerCodeTrackingDto {
    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "status")
    private Integer status;

    @ApiModelProperty(name = "student")
    private StudentDto student;

    @ApiModelProperty(name = "sellCode")
    private String sellCode;

    @ApiModelProperty(name = "browserCode")
    private String browserCode;
}
