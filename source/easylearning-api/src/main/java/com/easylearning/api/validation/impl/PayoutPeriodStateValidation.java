package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.PayoutPeriodState;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PayoutPeriodStateValidation implements ConstraintValidator<PayoutPeriodState, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(PayoutPeriodState constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer state, ConstraintValidatorContext constraintValidatorContext) {
        if (state == null && allowNull) {
            return true;
        }
        if (!Objects.equals(state, LifeUniConstant.MONTHLY_PERIOD_STATE_PENDING) &&
                !Objects.equals(state, LifeUniConstant.MONTHLY_PERIOD_STATE_CALCULATED) &&
                !Objects.equals(state, LifeUniConstant.MONTHLY_PERIOD_STATE_DONE) ) {
            return false;
        }
        return true;
    }
}
