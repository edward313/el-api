package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.PromotionType;
import org.apache.commons.lang.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PromotionTypeValidation  implements ConstraintValidator<PromotionType,Integer> {

    private boolean allowNull;
    @Override
    public void initialize(PromotionType constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (integer == null && allowNull){
            return true;
        }
        if (!ObjectUtils.equals(LifeUniConstant.PROMOTION_TYPE_USE_ONE, integer) &&
                !ObjectUtils.equals(LifeUniConstant.PROMOTION_TYPE_USE_MULTIPLE, integer)){
            return false;
        }
        return true;
    }
}
