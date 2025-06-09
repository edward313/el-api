package com.easylearning.api.validation;


import com.easylearning.api.validation.impl.DataTypeValidation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DataTypeValidation.class)
@Documented
public @interface DataType {
    boolean allowNull() default false;
    String message() default  "DataType invalid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
