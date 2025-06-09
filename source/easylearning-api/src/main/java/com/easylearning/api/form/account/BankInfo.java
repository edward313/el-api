package com.easylearning.api.form.account;

import lombok.Data;

@Data
public class BankInfo {
    private String bankNumber;
    private String bankNameCustomer;
    private String bankName;
    private String bankAgency;
}
