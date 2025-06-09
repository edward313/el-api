package com.easylearning.api.dto.booking;


import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.courseTransaction.CourseTransactionAdminDto;
import com.easylearning.api.dto.promotion.PromotionDto;
import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BookingAdminDto extends ABasicAdminDto {
    @ApiModelProperty(name = "student")
    private StudentDto student;
    @ApiModelProperty(name = "totalMoney")
    private Double totalMoney;
    @ApiModelProperty(name = "saleOffMoney")
    private Double saleOffMoney;
    @ApiModelProperty(name = "couponMoney")
    private Double couponMoney;
    @ApiModelProperty(name = "state")
    private Integer state;
    @ApiModelProperty(name = "paymentMethod")
    private Integer paymentMethod;
    @ApiModelProperty(name = "paymentData")
    private String paymentData;
    @ApiModelProperty(name = "payoutStatus")
    private Integer payoutStatus;
    @ApiModelProperty(name = "code")
    private String code;
    @ApiModelProperty(name = "promotion")
    private PromotionDto promotion;
    @ApiModelProperty(name = "transactions")
    private List<CourseTransactionAdminDto> transactions;
}
