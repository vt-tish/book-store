package com.vttish.bookstore.cart.service.impl;

import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.cart.dto.AddCartItemRequestDto;
import com.vttish.bookstore.cart.dto.CartResponseDto;
import com.vttish.bookstore.cart.dto.UpdateCartItemRequestDto;
import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.entity.CartItem;
import com.vttish.bookstore.cart.exception.CartItemNotFoundException;
import com.vttish.bookstore.cart.exception.CartNotFoundException;
import com.vttish.bookstore.cart.mapper.CartMapper;
import com.vttish.bookstore.cart.repository.CartRepository;
import com.vttish.bookstore.cart.service.CartInitializer;
import com.vttish.bookstore.cart.service.CartManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartManagementServiceImpl implements CartManagementService {
    private final CartRepository cartRepository;
    private final BookQueryService bookQueryService;
    private final CartInitializer cartInitializer;
    private final CartMapper mapper;

    @Override
    public CartResponseDto get(UUID ownerId, String lang) {
        Optional<Cart> existingCart = cartRepository.findWithBooksByOwnerId(ownerId);

        return existingCart.map(cart ->
                        mapper.toCartResponseDto(cart, bookQueryService.getTranslations(getBooks(cart), lang))
                ).orElseGet(CartResponseDto::empty);
    }

    @Override
    @Transactional
    public CartResponseDto addItem(UUID ownerId, String lang, AddCartItemRequestDto addCartItemRequestDto) {
        Cart cart;

        try {
            cart = cartInitializer.getOrCreate(ownerId);
        } catch (DataIntegrityViolationException ex) {
            cart = cartRepository.findWithBooksByOwnerId(ownerId).orElseThrow(
                    CartNotFoundException::new
            );
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(addCartItemRequestDto.bookId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + addCartItemRequestDto.quantity());
        } else {
            Book book = bookQueryService.getByIdAvailable(addCartItemRequestDto.bookId());
            cart.addItem(new CartItem(book, addCartItemRequestDto.quantity()));
        }

        cart = cartRepository.save(cart);
        return mapper.toCartResponseDto(cart, bookQueryService.getTranslations(getBooks(cart), lang));
    }

    @Override
    @Transactional
    public CartResponseDto updateItem(
            UUID ownerId,
            UUID id,
            String lang,
            UpdateCartItemRequestDto updateCartItemRequestDto
    ) {
        Optional<Cart> existingCart = cartRepository.findWithBooksByOwnerId(ownerId);

        if (existingCart.isEmpty()) {
            throw new CartItemNotFoundException();
        }

        Cart cart = existingCart.get();
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(id))
                .findFirst()
                .orElseThrow(CartItemNotFoundException::new);

        if (updateCartItemRequestDto.quantity() <= 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(updateCartItemRequestDto.quantity());
        }

        cart = cartRepository.save(cart);
        return mapper.toCartResponseDto(cart, bookQueryService.getTranslations(getBooks(cart), lang));
    }

    @Override
    @Transactional
    public CartResponseDto removeItem(UUID ownerId, UUID id, String lang) {
        Optional<Cart> existingCart = cartRepository.findWithBooksByOwnerId(ownerId);

        if (existingCart.isEmpty()) {
            return CartResponseDto.empty();
        }

        Cart cart = existingCart.get();
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(id))
                .findFirst()
                .orElseThrow(CartItemNotFoundException::new);

        cart.removeItem(item);

        cart = cartRepository.save(cart);
        return mapper.toCartResponseDto(cart, bookQueryService.getTranslations(getBooks(cart), lang));
    }

    @Override
    @Transactional
    public void clear(UUID ownerId) {
        Optional<Cart> existingCart = cartRepository.findByOwnerId(ownerId);

        if (existingCart.isEmpty()) {
            return;
        }

        Cart cart = existingCart.get();
        cart.clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clear(Cart cart) {
        if (cart.getItems().isEmpty()) {
            return;
        }

        cart.clear();
        cartRepository.save(cart);
    }

    private List<Book> getBooks(Cart cart) {
        return cart.getItems().stream()
                .map(CartItem::getBook)
                .toList();
    }
}
