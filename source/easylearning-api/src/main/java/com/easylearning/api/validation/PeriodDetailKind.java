package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.PeriodDetailStateValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PeriodDetailStateValidation.class)
@Documented
public @interface PeriodDetailKind {
    boolean allowNull() default false;
    String message() default "Period Detail kind invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
