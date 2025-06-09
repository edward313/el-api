package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.CourseKindValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CourseKindValidation.class)
@Documented
public @interface CourseKind {
    boolean allowNull() default false;
    String message() default "course kind invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
