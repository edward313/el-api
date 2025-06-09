package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.PayoutPeriodStateValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PayoutPeriodStateValidation.class)
@Documented
public @interface PayoutPeriodState {
    boolean allowNull() default false;
    String message() default "Payout Period state invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
