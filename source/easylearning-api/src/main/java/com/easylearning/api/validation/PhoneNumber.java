package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.PhoneNumberValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidation.class)
@Documented
public @interface PhoneNumber {
    boolean allowNull() default false;
    String message() default "phone number empty or invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
