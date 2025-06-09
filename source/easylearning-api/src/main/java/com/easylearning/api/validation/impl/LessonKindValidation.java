package com.easylearning.api.validation.impl;


import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.LessonKind;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class LessonKindValidation implements ConstraintValidator<LessonKind,Integer> {

    private boolean allowNull;

    @Override
    public void initialize(LessonKind constraintAnnotation) { allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Integer kind, ConstraintValidatorContext constraintValidatorContext) {
        if(kind == null && allowNull) {
            return true;
        }
        if(!Objects.equals(kind, LifeUniConstant.LESSON_KIND_TEXT) &&
                !Objects.equals(kind, LifeUniConstant.LESSON_KIND_VIDEO) &&
                    !Objects.equals(kind,LifeUniConstant.LESSON_KIND_SECTION)) {
            return false;
        }
        return true;
    }
}
