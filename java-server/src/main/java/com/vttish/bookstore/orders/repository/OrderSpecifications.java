package com.vttish.bookstore.orders.repository;

import com.vttish.bookstore.orders.dto.OrderFilterRequestDto;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.entity.enums.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderSpecifications {

    private OrderSpecifications() {
        throw new UnsupportedOperationException();
    }

    public static Specification<Order> byFilter(OrderFilterRequestDto filter) {
        Specification<Order> spec = Specification.unrestricted();

        if (filter == null) {
            return spec;
        }

        if (filter.clientId() != null) {
            spec = spec.and(hasClientId(filter.clientId()));
        }

        if (filter.employeeId() != null) {
            spec = spec.and(hasEmployeeId(filter.employeeId()));
        }

        if (filter.status() != null) {
            spec = spec.and(hasStatus(filter.status()));
        }

        if (filter.minPrice() != null) {
            spec = spec.and(totalPriceGreaterThanOrEqualTo(filter.minPrice()));
        }

        if (filter.maxPrice() != null) {
            spec = spec.and(totalPriceLessThanOrEqualTo(filter.maxPrice()));
        }

        return spec;
    }

    private static Specification<Order> hasClientId(UUID clientId) {
        return (root, query, cb) ->
                cb.equal(root.get("client").get("id"), clientId);
    }

    private static Specification<Order> hasEmployeeId(UUID employeeId) {
        return (root, query, cb) ->
                cb.equal(root.get("employee").get("id"), employeeId);
    }

    private static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    private static Specification<Order> totalPriceGreaterThanOrEqualTo(BigDecimal minPrice) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("totalPrice"), minPrice);
    }

    private static Specification<Order> totalPriceLessThanOrEqualTo(BigDecimal maxPrice) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("totalPrice"), maxPrice);
    }
}
