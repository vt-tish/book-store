package com.vttish.bookstore.cart.service.impl;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.service.BookService;
import com.vttish.bookstore.cart.dto.AddCartItemDto;
import com.vttish.bookstore.cart.dto.CartDto;
import com.vttish.bookstore.cart.dto.UpdateCartDto;
import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.entity.CartItem;
import com.vttish.bookstore.cart.mapper.CartMapper;
import com.vttish.bookstore.cart.repository.CartRepository;
import com.vttish.bookstore.cart.service.CartService;
import com.vttish.bookstore.common.exception.EmptyPayloadException;
import com.vttish.bookstore.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final BookService bookService;

    @Override
    public CartDto get(UUID ownerId) {
        Cart cart = cartRepository.findByOwnerId(ownerId).orElse(null);

        if (cart == null) {
            return CartDto.empty();
        }

        List<UUID> bookIds = cart.getItems().stream()
                .map(CartItem::getBookId)
                .distinct()
                .toList();

        Map<UUID, BigDecimal> prices = bookService.getPricesByIds(bookIds);

        if (bookIds.size() != prices.size()) {
            throw new NotFoundException("One or more books are not found by ids");
        }

        return CartMapper.toCartDto(cart, prices);
    }

    @Override
    @Transactional
    public CartDto update(UUID ownerId, UpdateCartDto updateCartDto) {
        if (updateCartDto.areUpdatableFieldsEmpty()) {
            throw new EmptyPayloadException("No fields were provided to update");
        }

        Cart cart = cartRepository.findByOwnerId(ownerId).orElseGet(() ->
                cartRepository.save(new Cart(ownerId))
        );

        List<UUID> exclude = updateCartDto.excludeBookIds();
        if (exclude != null && !exclude.isEmpty()) {
            cart.getItems().removeIf(cartItem -> exclude.contains(cartItem.getBookId()));
        }

        List<AddCartItemDto> addItems = updateCartDto.cartItems();
        boolean hasAdditions = addItems != null && !addItems.isEmpty();

        Set<UUID> bookIds = cart.getItems().stream()
                .map(CartItem::getBookId)
                .collect(Collectors.toSet());

        if (hasAdditions) {
            addItems.forEach(item -> bookIds.add(item.bookId()));
        }

        Map<UUID, BigDecimal> prices = bookService.getPricesByIds(new ArrayList<>(bookIds));

        if (hasAdditions) {
            for (AddCartItemDto addItem : addItems) {
                UUID id = addItem.bookId();

                if (!prices.containsKey(id)) {
                    throw new NotFoundException(Book.class, id);
                }

                CartItem item = cart.getItems().stream()
                        .filter(cartItem -> cartItem.getBookId().equals(id))
                        .findFirst()
                        .orElse(null);

                if (item != null) {
                    item.setQuantity(addItem.quantity());
                } else {
                    cart.addItem(new CartItem(
                            id,
                            prices.get(id),
                            addItem.quantity()
                    ));
                }
            }
        }

        return CartMapper.toCartDto(cartRepository.save(cart), prices);
    }
}
