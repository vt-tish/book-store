package com.vttish.bookstore.common.validation;

import java.math.BigDecimal;

public interface PriceRange {
    BigDecimal minPrice();
    BigDecimal maxPrice();
}
