package com.easylearning.api.dto.courseTransaction;

import com.easylearning.api.dto.account.AccountDto;
import com.easylearning.api.model.Account;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MyTransactionDto{
    @ApiModelProperty(name = "revenueMoney")
    private Double revenueMoney = 0.0;
    @ApiModelProperty(name = "referredAccount")
    private AccountDto referredAccount;
    @ApiModelProperty(name = "courseTransaction")
    private CourseTransactionDto courseTransaction;
}
