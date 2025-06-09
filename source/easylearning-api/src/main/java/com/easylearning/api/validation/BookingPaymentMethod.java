package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.BookingPaymentMethodValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingPaymentMethodValidation.class)
@Documented
public @interface BookingPaymentMethod {
    boolean allowNull() default false;
    String message() default "Booking payment method invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
