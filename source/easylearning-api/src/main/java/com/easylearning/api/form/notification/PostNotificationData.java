package com.easylearning.api.form.notification;

import com.easylearning.api.form.BaseDataForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostNotificationData extends BaseDataForm {
    private Integer kind;
    private String message;
    private Long userId;
    private String cmd;
    private Integer appKind;
}
