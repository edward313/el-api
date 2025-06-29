package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.NotificationStateValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotificationStateValidation.class)
@Documented
public @interface NotificationState {
    boolean allowNull() default false;
    String message() default "State invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
