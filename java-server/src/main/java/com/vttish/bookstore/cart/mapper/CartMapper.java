package com.vttish.bookstore.cart.mapper;

import com.vttish.bookstore.cart.dto.CartDto;
import com.vttish.bookstore.cart.dto.CartItemDto;
import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CartMapper {
    public CartDto toCartDto(Cart cart, Map<UUID, BigDecimal> prices) {
        if (cart == null) {
            return CartDto.empty();
        }

        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            boolean isAvailable = prices.containsKey(item.getBookId());
            BigDecimal price = prices.getOrDefault(item.getBookId(), item.getPricePerUnit());
            Integer quantity = item.getQuantity();
            boolean hasChanged = price.compareTo(item.getPricePerUnit()) != 0;

            BigDecimal subtotalPrice = price.multiply(BigDecimal.valueOf(quantity));

            itemDtos.add(new CartItemDto(
                    item.getBookId(),
                    quantity,
                    price,
                    hasChanged ? item.getPricePerUnit() : null,
                    subtotalPrice,
                    isAvailable
            ));

            if (isAvailable) {
                totalPrice = totalPrice.add(subtotalPrice);
            }
        }

        return new CartDto(itemDtos, totalPrice);
    }
}
