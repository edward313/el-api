package com.easylearning.api.validation;



import com.easylearning.api.validation.impl.ReviewStarValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReviewStarValidation.class)
@Documented
public @interface ReviewStar {
    boolean allowNull() default false;
    String message() default  "Star invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
