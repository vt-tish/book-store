package com.vttish.bookstore.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageUrlValidator.class)
public @interface ImageUrl {
    String message() default "Invalid image url";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
