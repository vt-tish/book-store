package com.vttish.bookstore.orders.mapper;

import com.vttish.bookstore.books.entity.BookTranslation;
import com.vttish.bookstore.orders.dto.AdminOrderDetailsResponseDto;
import com.vttish.bookstore.orders.dto.OrderItemDto;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.OrderItem;
import com.vttish.bookstore.orders.entity.Order;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderDetailsResponseDto toOrderDetailsDto(Order order, @Context Map<UUID, BookTranslation> translationMap);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientEmail", source = "client.email")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeEmail", source = "employee.email")
    AdminOrderDetailsResponseDto toAdminOrderDetailsDto(
            Order order,
            @Context Map<UUID, BookTranslation> translationMap
    );

    @Mapping(target = "subtotalPrice", expression = "java(calculateSubtotal(orderItem))")
    @Mapping(target = "bookName", expression = "java(getBookName(orderItem, translationMap))")
    @Mapping(target = "bookAuthor", expression = "java(getBookAuthor(orderItem, translationMap))")
    OrderItemDto toOrderItemDto(OrderItem orderItem, @Context Map<UUID, BookTranslation> translationMap);

    default BigDecimal calculateSubtotal(OrderItem item) {
        if (item.getPricePerUnit() == null || item.getQuantity() == null) {
            return null;
        }
        return item.getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    default String getBookName(OrderItem item, @Context Map<UUID, BookTranslation> translationMap) {
        BookTranslation translation = translationMap.get(item.getBook().getId());
        return translation != null ? translation.getName() : null;
    }

    default String getBookAuthor(OrderItem item, @Context Map<UUID, BookTranslation> translationMap) {
        BookTranslation translation = translationMap.get(item.getBook().getId());
        return translation != null ? translation.getAuthor() : null;
    }
}
