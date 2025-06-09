package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.NotificationKind;
import org.apache.commons.lang.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotificationKindValidation  implements ConstraintValidator<NotificationKind,Integer> {

    private boolean allowNull;
    @Override
    public void initialize(NotificationKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer kind, ConstraintValidatorContext constraintValidatorContext) {
        if (kind == null && allowNull){
            return true;
        }
        if (!ObjectUtils.equals(LifeUniConstant.NOTIFICATION_KIND_APPROVE_SELLER, kind) &&
                !ObjectUtils.equals(LifeUniConstant.NOTIFICATION_KIND_REJECT_SELLER, kind)){
            return false;
        }
        return true;
    }
}
