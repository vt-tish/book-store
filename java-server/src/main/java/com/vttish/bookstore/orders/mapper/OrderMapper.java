package com.vttish.bookstore.orders.mapper;

import com.vttish.bookstore.orders.dto.AdminOrderDetailsResponseDto;
import com.vttish.bookstore.orders.dto.OrderItemDto;
import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.OrderItem;
import com.vttish.bookstore.orders.entity.Order;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.email", target = "clientEmail")
    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.email", target = "employeeEmail")
    AdminOrderDetailsResponseDto toAdminOrderDetailsDto(Order order, @Context TranslationContext ctx);

    OrderDetailsResponseDto toOrderDetailsDto(Order order, @Context TranslationContext ctx);

    @Mapping(target = "subtotalPrice", expression = "java(calculateSubtotal(orderItem))")
    @Mapping(target = "bookName", expression = "java(orderItem.getBook().getName(ctx.lang(), ctx.defaultLang()))")
    @Mapping(target = "bookAuthor", expression = "java(orderItem.getBook().getAuthor(ctx.lang(), ctx.defaultLang()))")
    OrderItemDto toOrderItem(OrderItem orderItem, @Context TranslationContext ctx);

    default BigDecimal calculateSubtotal(OrderItem orderItem) {
        if (orderItem.getPricePerUnit() == null || orderItem.getQuantity() == null) {
            return null;
        }

        return orderItem.getPricePerUnit().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }
}
