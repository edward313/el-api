package com.easylearning.api.validation.impl;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.validation.BookingPaymentMethod;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class BookingPaymentMethodValidation implements ConstraintValidator<BookingPaymentMethod, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(BookingPaymentMethod constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer method, ConstraintValidatorContext constraintValidatorContext) {
        if (method == null && allowNull) {
            return true;
        }
        if (!Objects.equals(method, LifeUniConstant.BOOKING_PAYMENT_METHOD_BANKING) &&
                    !Objects.equals(method, LifeUniConstant.BOOKING_PAYMENT_METHOD_WALLET) ) {
            return false;
        }
        return true;
    }
}
