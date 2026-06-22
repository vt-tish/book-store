package com.vttish.bookstore.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    private static final String PHONE_PATTERN = "^\\d{3}-\\d{3}-\\d{4}$";

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        if (phone == null || phone.isEmpty()) {
            return true;
        }

        return phone.matches(PHONE_PATTERN);
    }
}
