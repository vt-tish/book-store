package com.vttish.bookstore.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPriceRangeValidator.class)
public @interface ValidPriceRange {
    String message() default "Minimum price cannot be greater than maximum price";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
