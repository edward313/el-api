package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.NotificationState;
import org.apache.commons.lang.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotificationStateValidation  implements ConstraintValidator<NotificationState,Integer> {

    private boolean allowNull;
    @Override
    public void initialize(NotificationState constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (integer == null && allowNull){
            return true;
        }
        if (!ObjectUtils.equals(LifeUniConstant.NOTIFICATION_STATE_SENT, integer) &&
                !ObjectUtils.equals(LifeUniConstant.NOTIFICATION_STATE_READ, integer)){
            return false;
        }
        return true;
    }
}
