package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.AppKind;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class AppKindValidation implements ConstraintValidator<AppKind, Integer> {
    private boolean allowNull;
    @Override
    public void initialize(AppKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer app, ConstraintValidatorContext constraintValidatorContext) {
        if (app == null && allowNull){
            return true;
        }
        if (!Objects.equals(app, LifeUniConstant.CLIENT_APP)
            && !Objects.equals(app, LifeUniConstant.PORTAL_APP)) {
            return false;
        }
        return true;
    }
}
