package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.NewsKind;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class NewsKindValidation implements ConstraintValidator<NewsKind,Integer> {

    private boolean allowNull;

    @Override
    public void initialize(NewsKind constraintAnnotation) { allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Integer kind, ConstraintValidatorContext constraintValidatorContext) {
        if(kind == null && allowNull) {
            return true;
        }
        if(!Objects.equals(kind, LifeUniConstant.NEWS_KIND_INTRODUCTION)) {
            return false;
        }
        return true;
    }
}
