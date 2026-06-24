package com.vttish.bookstore.cart.mapper;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.entity.BookTranslation;
import com.vttish.bookstore.cart.dto.CartItemDto;
import com.vttish.bookstore.cart.dto.CartResponseDto;
import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.entity.CartItem;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    @Mapping(target = "bookId", source = "cartItem.book.id")
    @Mapping(target = "bookName", expression = "java(getBookName(cartItem, translationMap))")
    @Mapping(target = "previewUrl", source = "cartItem.book.previewUrl")
    @Mapping(target = "pricePerUnit", source = "cartItem.book.price")
    @Mapping(target = "subtotalPrice", expression = "java(calculateSubtotal(cartItem))")
    @Mapping(target = "isAvailable", expression = "java(!cartItem.getBook().isArchived())")
    CartItemDto toCartItemDto(CartItem cartItem, @Context Map<UUID, BookTranslation> translationMap);

    default CartResponseDto toCartResponseDto(Cart cart, Map<UUID, BookTranslation> translationMap) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<CartItemDto> itemDtos = new ArrayList<>();

        if (cart != null && cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                CartItemDto itemDto = toCartItemDto(item, translationMap);

                if (itemDto.subtotalPrice() != null) {
                    totalPrice = totalPrice.add(itemDto.subtotalPrice());
                }

                itemDtos.add(itemDto);
            }
        }

        return new CartResponseDto(itemDtos, totalPrice);
    }

    default BigDecimal calculateSubtotal(CartItem item) {
        BigDecimal price = item.getBook().getPrice();
        if (price == null || item.getQuantity() == null) {
            return null;
        }
        return price.multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    default String getBookName(CartItem item, @Context Map<UUID, BookTranslation> translationMap) {
        BookTranslation translation = translationMap.get(item.getBook().getId());
        return translation != null ? translation.getName() : null;
    }
}
