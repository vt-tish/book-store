package com.vttish.bookstore.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPriceRangeValidator implements ConstraintValidator<ValidPriceRange, PriceRange> {

    @Override
    public boolean isValid(PriceRange priceRange, ConstraintValidatorContext context) {
        if (priceRange.minPrice() == null || priceRange.maxPrice() == null) {
            return true;
        }

        return priceRange.minPrice().compareTo(priceRange.maxPrice()) <= 0;
    }
}
