package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.LessonKindValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LessonKindValidation.class)
@Documented
public @interface LessonKind {
    boolean allowNull() default false;
    String message() default  "Kind invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
