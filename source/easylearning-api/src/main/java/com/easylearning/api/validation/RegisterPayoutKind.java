package com.easylearning.api.validation;

import com.easylearning.api.validation.impl.RegisterPayoutKindValidation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegisterPayoutKindValidation.class)
@Documented
public @interface RegisterPayoutKind {
    boolean allowNull() default false;
    String message() default "Review kind invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
