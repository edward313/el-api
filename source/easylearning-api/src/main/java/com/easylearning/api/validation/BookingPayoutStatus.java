package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.BookingPayoutStatusValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingPayoutStatusValidation.class)
@Documented
public @interface BookingPayoutStatus {
    boolean allowNull() default false;
    String message() default "Booking payout status invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
