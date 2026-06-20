package com.vttish.bookstore.orders.mapper;

import com.vttish.bookstore.orders.dto.BookItemDto;
import com.vttish.bookstore.orders.dto.OrderCardResponseDto;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.BookItem;
import com.vttish.bookstore.orders.entity.Order;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderDetailsResponseDto toOrderDetailsDto(Order order);

    @Mapping(target = "totalItems", expression = "java(order.getBookItems().size())")
    OrderCardResponseDto toOrderCardDto(Order order);

    @Mapping(target = "subtotalPrice", expression = "java(calculateSubtotal(bookItem))")
    BookItemDto toBookItemDto(BookItem bookItem);

    default BigDecimal calculateSubtotal(BookItem bookItem) {
        if (bookItem.getPricePerUnit() == null || bookItem.getQuantity() == null) {
            return null;
        }

        return bookItem.getPricePerUnit()
                .multiply(BigDecimal.valueOf(bookItem.getQuantity()));
    }
}
