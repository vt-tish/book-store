package com.vttish.bookstore.orders.mapper;

import com.vttish.bookstore.orders.dto.OrderItemDto;
import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.OrderItem;
import com.vttish.bookstore.orders.entity.Order;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderDetailsResponseDto toOrderDetailsDto(Order order);

    @Mapping(target = "totalItems", expression = "java(order.getItems().size())")
    OrderCardResponseDto toOrderCardDto(Order order);

    @Mapping(target = "subtotalPrice", expression = "java(calculateSubtotal(orderItem))")
    OrderItemDto toOrderItem(OrderItem orderItem);

    default BigDecimal calculateSubtotal(OrderItem orderItem) {
        if (orderItem.getPricePerUnit() == null || orderItem.getQuantity() == null) {
            return null;
        }

        return orderItem.getPricePerUnit()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }
}
