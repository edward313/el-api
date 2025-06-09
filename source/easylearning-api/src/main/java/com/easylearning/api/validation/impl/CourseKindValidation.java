package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.CourseKind;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class CourseKindValidation implements ConstraintValidator<CourseKind, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(CourseKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer kind, ConstraintValidatorContext constraintValidatorContext) {
        if (kind == null && allowNull) {
            return true;
        }
        if (!Objects.equals(kind, LifeUniConstant.COURSE_KIND_SINGLE) &&
                !Objects.equals(kind, LifeUniConstant.COURSE_KIND_COMBO) &&
                    !Objects.equals(kind, LifeUniConstant.COURSE_KIND_NO_LESSON)) {
            return false;
        }
        return true;
    }
}
