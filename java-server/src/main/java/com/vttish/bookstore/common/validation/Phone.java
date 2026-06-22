package com.vttish.bookstore.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    String message() default "Phone is not valid";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
