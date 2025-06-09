package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.RegisterPayoutKind;
import org.apache.commons.lang.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegisterPayoutKindValidation  implements ConstraintValidator<RegisterPayoutKind,Integer> {

    private boolean allowNull;
    @Override
    public void initialize(RegisterPayoutKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (integer == null && allowNull){
            return true;
        }
        return ObjectUtils.equals(LifeUniConstant.REGISTER_PAYOUT_KIND_INDIVIDUAL, integer);
    }
}
