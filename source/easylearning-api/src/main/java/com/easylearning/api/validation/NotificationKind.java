package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.NotificationKindValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotificationKindValidation.class)
@Documented
public @interface NotificationKind {
    boolean allowNull() default false;
    String message() default  "kind invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
