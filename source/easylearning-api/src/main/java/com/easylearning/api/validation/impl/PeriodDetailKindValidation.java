package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.PeriodDetailKind;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PeriodDetailKindValidation implements ConstraintValidator<PeriodDetailKind, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(PeriodDetailKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer kind, ConstraintValidatorContext constraintValidatorContext) {
        if (kind == null && allowNull) {
            return true;
        }
        if (!Objects.equals(kind, LifeUniConstant.PERIOD_DETAIL_KIND_SELLER) &&
                !Objects.equals(kind, LifeUniConstant.PERIOD_DETAIL_KIND_EXPERT) ) {
            return false;
        }
        return true;
    }
}
