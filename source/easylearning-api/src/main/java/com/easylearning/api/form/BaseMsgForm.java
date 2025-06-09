package com.easylearning.api.form;

import lombok.Data;

@Data
public class BaseMsgForm<T> {
    private String cmd;
    private String app;
    private T data;
}
