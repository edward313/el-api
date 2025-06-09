package com.easylearning.api.dto.sellerCodeTracking;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SellerCodeTrackingAdminDto extends ABasicAdminDto {

    @ApiModelProperty(name = "student")
    private StudentDto student;

    @ApiModelProperty(name = "sellCode")
    private String sellCode;

    @ApiModelProperty(name = "browserCode")
    private String browserCode;
}
