package com.easylearning.api.dto.courseTransaction;


import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.booking.BookingDto;
import com.easylearning.api.dto.course.CourseDto;
import com.easylearning.api.dto.expert.ExpertDto;
import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CourseTransactionAdminDto extends ABasicAdminDto {
    @ApiModelProperty(name = "student")
    private StudentDto student;
    @ApiModelProperty(name = "expert")
    private ExpertDto expert;
    @ApiModelProperty(name = "seller")
    private StudentDto seller;
    @ApiModelProperty(name = "course")
    private CourseDto course;
    @ApiModelProperty(name = "price")
    private Double price;
    @ApiModelProperty(name = "originalPrice")
    private Double originalPrice;
    @ApiModelProperty(name = "refSellCode")
    private String refSellCode;
    @ApiModelProperty(name = "booking")
    private BookingDto booking;
}
