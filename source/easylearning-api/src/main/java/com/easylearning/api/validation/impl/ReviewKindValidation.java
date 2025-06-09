package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.ReviewKind;
import org.apache.commons.lang.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ReviewKindValidation implements ConstraintValidator<ReviewKind,Integer> {
    private boolean allowNull;
    @Override
    public void initialize(ReviewKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (integer == null && allowNull){
            return true;
        }
        if (!ObjectUtils.equals(LifeUniConstant.REVIEW_KIND_COURSE, integer) &&
                !ObjectUtils.equals(LifeUniConstant.REVIEW_KIND_EXPERT, integer) &&
                !ObjectUtils.equals(LifeUniConstant.REVIEW_KIND_SYSTEM, integer)){
            return false;
        }
        return true;
    }
}
