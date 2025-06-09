package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.PromotionStateValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PromotionStateValidation.class)
@Documented
public @interface PromotionState {
    boolean allowNull() default false;
    String message() default "State invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
