package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.PromotionKind;
import org.apache.commons.lang.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PromotionKindValidation  implements ConstraintValidator<PromotionKind,Integer> {

    private boolean allowNull;
    @Override
    public void initialize(PromotionKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (integer == null && allowNull){
            return true;
        }
        if (!ObjectUtils.equals(LifeUniConstant.PROMOTION_KIND_MONEY, integer) &&
                !ObjectUtils.equals(LifeUniConstant.PROMOTION_KIND_PERCENT, integer)){
            return false;
        }
        return true;
    }
}
