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

    @Mapping(target = "clientId", source = "order.client.id")
    @Mapping(target = "clientEmail", source = "order.client.email")
    @Mapping(target = "employeeId", source = "order.employee.id")
    @Mapping(target = "employeeEmail", source = "order.employee.email")
    AdminOrderDetailsResponseDto toAdminOrderDetailsDto(
            Order order,
            @Context Map<UUID, BookTranslation> translationMap
    );

    @Mapping(target = "subtotalPrice", expression = "java(calculateSubtotal(orderItem))")
    @Mapping(target = "bookName", expression = "java(getBookName(orderItem, translationMap))")
    @Mapping(target = "bookAuthor", expression = "java(getBookAuthor(orderItem, translationMap))")
    OrderItemDto toOrderItem(OrderItem orderItem, @Context Map<UUID, BookTranslation> translationMap);

    default BigDecimal calculateSubtotal(OrderItem orderItem) {
        if (orderItem.getPricePerUnit() == null || orderItem.getQuantity() == null) {
            return null;
        }
        return orderItem.getPricePerUnit().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
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
