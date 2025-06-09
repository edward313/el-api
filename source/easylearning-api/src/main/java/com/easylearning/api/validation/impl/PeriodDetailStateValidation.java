package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.BookingState;
import com.easylearning.api.validation.PeriodDetailState;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PeriodDetailStateValidation implements ConstraintValidator<PeriodDetailState, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(PeriodDetailState constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer state, ConstraintValidatorContext constraintValidatorContext) {
        if (state == null && allowNull) {
            return true;
        }
        if (!Objects.equals(state, LifeUniConstant.PERIOD_DETAIL_STATE_UNPAID) &&
                !Objects.equals(state, LifeUniConstant.PERIOD_DETAIL_STATE_PAID) ) {
            return false;
        }
        return true;
    }
}
