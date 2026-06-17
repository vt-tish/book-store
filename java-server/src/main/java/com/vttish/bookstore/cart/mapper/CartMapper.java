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
            Integer quantity = item.getQuantity();
            boolean isAvailable = prices.containsKey(item.getBookId());
            BigDecimal pricePerUnit = null;
            BigDecimal subtotalPrice = null;

            if (isAvailable) {
                pricePerUnit = prices.get(item.getBookId());
                subtotalPrice = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
                totalPrice = totalPrice.add(subtotalPrice);
            }

            itemDtos.add(new CartItemDto(
                    item.getBookId(),
                    item.getBookName(),
                    quantity,
                    pricePerUnit,
                    subtotalPrice,
                    isAvailable
            ));
        }

        return new CartDto(itemDtos, totalPrice);
    }
}
