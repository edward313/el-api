package com.easylearning.api.form;

import lombok.Data;

@Data
public class GoogleUserInfo {
    private String id;
    private String email;
    private String name;
    private String picture;
    private String locale;
}
