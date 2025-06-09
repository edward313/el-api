package com.easylearning.api.validation;

import com.easylearning.api.validation.impl.WalletKindValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = WalletKindValidation.class)
@Documented
public @interface WalletKind {
    boolean allowNull() default false;
    String message() default "Wallet kind invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
