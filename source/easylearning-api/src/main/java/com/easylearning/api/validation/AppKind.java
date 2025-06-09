package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.AppKindValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AppKindValidation.class)
@Documented
public @interface AppKind {
    boolean allowNull() default false;
    String message() default "Invalid app kind";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
