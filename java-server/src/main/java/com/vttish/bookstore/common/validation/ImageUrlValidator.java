package com.vttish.bookstore.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageUrlValidator implements ConstraintValidator<ImageUrl, String> {
    private static final String IMAGE_URL_PATTERN =
            "(?i)^https?://([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}.*\\.(png|jpg|jpeg|gif|webp|svg)(\\?.*)?$";

    @Override
    public boolean isValid(String imageUrl, ConstraintValidatorContext constraintValidatorContext) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return true;
        }

        return imageUrl.matches(IMAGE_URL_PATTERN);
    }
}
