package com.easylearning.api.dto.referralSellerLog;

import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ReferralSellerLogDto {
    @ApiModelProperty(name = "id")
    private Long id;
    @ApiModelProperty(name = "usedTime")
    private Date usedTime;
    @ApiModelProperty(name = "student")
    private StudentDto student;
    @ApiModelProperty(name = "seller")
    private StudentDto seller;
}
