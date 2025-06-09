package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.BookingState;
import com.easylearning.api.validation.CourseKind;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class BookingStateValidation implements ConstraintValidator<BookingState, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(BookingState constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer state, ConstraintValidatorContext constraintValidatorContext) {
        if (state == null && allowNull) {
            return true;
        }
        if (!Objects.equals(state, LifeUniConstant.BOOKING_STATE_UNPAID) &&
                !Objects.equals(state, LifeUniConstant.BOOKING_STATE_PAID) &&
                    !Objects.equals(state, LifeUniConstant.BOOKING_STATE_REJECT) ) {
            return false;
        }
        return true;
    }
}
