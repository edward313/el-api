package com.easylearning.api.validation;

import com.easylearning.api.validation.impl.ReviewKindValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReviewKindValidation.class)
@Documented
public @interface ReviewKind {
    boolean allowNull() default false;
    String message() default "Review kind invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
