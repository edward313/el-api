package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.PromotionTypeValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PromotionTypeValidation.class)
@Documented
public @interface PromotionType {
    boolean allowNull() default false;
    String message() default "Type invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
