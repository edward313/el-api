package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.BookingPayoutStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class BookingPayoutStatusValidation implements ConstraintValidator<BookingPayoutStatus, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(BookingPayoutStatus constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer status, ConstraintValidatorContext constraintValidatorContext) {
        if (status == null && allowNull) {
            return true;
        }
        if (!Objects.equals(status, LifeUniConstant.BOOKING_PAYOUT_STATUS_UNPAID) &&
                !Objects.equals(status, LifeUniConstant.BOOKING_PAYOUT_STATUS_PAID) ) {
            return false;
        }
        return true;
    }
}
