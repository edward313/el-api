package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.BookingStateValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingStateValidation.class)
@Documented
public @interface BookingState {
    boolean allowNull() default false;
    String message() default "Booking state invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
