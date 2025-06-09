package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.DataType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class DataTypeValidation implements ConstraintValidator<DataType,String> {
    private boolean allowNull;

    @Override
    public void initialize(DataType constraintAnnotation) { allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null && allowNull) {
            return true;
        }
        if(!Objects.equals(s, LifeUniConstant.DATA_TYPE_INT) &&
                !Objects.equals(s, LifeUniConstant.DATA_TYPE_OBJECT) &&
                !Objects.equals(s,LifeUniConstant.DATA_TYPE_BOOLEAN) &&
                !Objects.equals(s, LifeUniConstant.DATA_TYPE_STRING) &&
                !Objects.equals(s, LifeUniConstant.DATA_TYPE_DOUBLE) &&
                !Objects.equals(s, LifeUniConstant.DATA_TYPE_PERCENT)) {
            return false;
        }
        return true;
    }
}
