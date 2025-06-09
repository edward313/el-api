package com.easylearning.api.dto.revenueShare;

import com.easylearning.api.dto.ABasicAdminDto;
import com.easylearning.api.dto.account.AccountDto;
import com.easylearning.api.dto.courseTransaction.CourseTransactionDto;
import com.easylearning.api.dto.expert.ExpertDto;
import com.easylearning.api.dto.student.StudentDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RevenueShareDto extends ABasicAdminDto {
    @ApiModelProperty(name = "courseTransaction")
    private CourseTransactionDto courseTransaction;
    @ApiModelProperty(name = "sourceAccount")
    private AccountDto sourceAccount;
    @ApiModelProperty(name = "refKind")
    private Integer refKind; //1 refStudent, 2 refSeller
    @ApiModelProperty(name = "seller")
    private StudentDto seller;
    @ApiModelProperty(name = "expert")
    private ExpertDto expert;
    @ApiModelProperty(name = "kind")
    private Integer kind;
    @ApiModelProperty(name = "ratioShare")
    private Integer ratioShare;
    @ApiModelProperty(name = "revenueMoney")
    private Integer revenueMoney;
    @ApiModelProperty(name = "payoutStatus")
    private Integer payoutStatus;
}
