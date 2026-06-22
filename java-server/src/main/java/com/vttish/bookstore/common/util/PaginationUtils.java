package com.vttish.bookstore.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaginationUtils {
    private PaginationUtils() {
        throw new UnsupportedOperationException();
    }

    public static Pageable translate(Pageable pageable, Map<String, String> translations) {
        List<Sort.Order> orders = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            orders.add(order.withProperty(translations.getOrDefault(property, property)));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
    }
}
