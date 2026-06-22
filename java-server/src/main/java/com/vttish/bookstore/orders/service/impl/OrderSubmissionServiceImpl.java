package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.books.dto.OrderBookView;
import com.vttish.bookstore.books.service.BookQueryService;
import com.vttish.bookstore.cart.dto.CartResponseDto;
import com.vttish.bookstore.cart.dto.CartItemDto;
import com.vttish.bookstore.cart.service.CartManagementService;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.OrderItem;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.exception.EmptyCartException;
import com.vttish.bookstore.orders.exception.UnavailableBookException;
import com.vttish.bookstore.orders.mapper.OrderMapper;
import com.vttish.bookstore.orders.repository.OrderRepository;
import com.vttish.bookstore.orders.service.OrderSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderSubmissionServiceImpl implements OrderSubmissionService {
    private final OrderRepository orderRepository;
    private final CartManagementService cartManagementService;
    private final BookQueryService bookQueryService;
    private final OrderMapper mapper;

    @Override
    @Transactional
    public OrderDetailsResponseDto submitByClientId(UUID clientId) {
        CartResponseDto cartResponseDto = cartManagementService.get(clientId, "uk");

        if (cartResponseDto.cartItems().isEmpty()) {
            throw new EmptyCartException();
        }

        cartResponseDto.cartItems().forEach(cartItem -> {
            if (!cartItem.isAvailable()) {
                throw new UnavailableBookException();
            }
        });

        Set<UUID> ids = cartResponseDto.cartItems().stream()
                .map(CartItemDto::bookId)
                .collect(Collectors.toSet());

        Map<UUID, OrderBookView> books = bookQueryService.getBooksForOrder(ids);

        Order order = new Order(clientId, cartResponseDto.totalPrice());

        for (CartItemDto cartItem : cartResponseDto.cartItems()) {
            OrderBookView book = books.get(cartItem.bookId());

            order.addItem(new OrderItem(
                    book.getId(),
                    book.getName(),
                    book.getAuthor(),
                    cartItem.pricePerUnit(),
                    cartItem.quantity()
            ));
        }

        cartManagementService.clear(clientId);
        return mapper.toOrderDetailsDto(orderRepository.save(order));
    }
}
