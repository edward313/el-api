package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.SlideShowActionValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SlideShowActionValidation.class)
@Documented
public @interface SlideShowAction {
    boolean allowNull() default false;
    String message() default "Action invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
