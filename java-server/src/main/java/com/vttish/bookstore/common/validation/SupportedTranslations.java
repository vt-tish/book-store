package com.vttish.bookstore.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupportedTranslationsValidator.class)
public @interface SupportedTranslations {
    String message() default "Required translations are not provided";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
