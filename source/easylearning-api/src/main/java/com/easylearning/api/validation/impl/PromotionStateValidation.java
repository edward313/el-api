package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.PromotionState;
import org.apache.commons.lang.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PromotionStateValidation  implements ConstraintValidator<PromotionState,Integer> {

    private boolean allowNull;
    @Override
    public void initialize(PromotionState constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (integer == null && allowNull){
            return true;
        }
        if (!ObjectUtils.equals(LifeUniConstant.PROMOTION_STATE_CREATED, integer) &&
                !ObjectUtils.equals(LifeUniConstant.PROMOTION_STATE_RUNNING, integer) &&
                    !ObjectUtils.equals(LifeUniConstant.PROMOTION_STATE_CANCEL, integer) &&
                        !ObjectUtils.equals(LifeUniConstant.PROMOTION_STATE_END, integer)){
            return false;
        }
        return true;
    }
}
