package com.vttish.bookstore.books.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BookPriceProvider {
    Optional<BigDecimal> getPriceById(UUID id);
    Map<UUID, BigDecimal> getPricesByIds(List<UUID> bookIds);
}
