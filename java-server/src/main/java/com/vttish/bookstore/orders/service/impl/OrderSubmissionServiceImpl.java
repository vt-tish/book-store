package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.auth.service.UserService;
import com.vttish.bookstore.cart.entity.Cart;
import com.vttish.bookstore.cart.entity.CartItem;
import com.vttish.bookstore.cart.service.CartManagementService;
import com.vttish.bookstore.cart.service.CartQueryService;
import com.vttish.bookstore.common.config.LocalizationProperties;
import com.vttish.bookstore.orders.dto.OrderDetailsResponseDto;
import com.vttish.bookstore.orders.entity.OrderItem;
import com.vttish.bookstore.orders.entity.Order;
import com.vttish.bookstore.orders.exception.EmptyCartException;
import com.vttish.bookstore.orders.exception.UnavailableBookException;
import com.vttish.bookstore.orders.mapper.OrderMapper;
import com.vttish.bookstore.orders.mapper.TranslationContext;
import com.vttish.bookstore.orders.repository.OrderRepository;
import com.vttish.bookstore.orders.service.OrderSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderSubmissionServiceImpl implements OrderSubmissionService {
    private final OrderRepository orderRepository;
    private final CartQueryService cartQueryService;
    private final UserService userService;
    private final CartManagementService cartManagementService;
    private final LocalizationProperties localizationProps;
    private final OrderMapper mapper;

    @Override
    @Transactional
    public OrderDetailsResponseDto submitByClientId(UUID clientId, String lang) {
        Cart cart = cartQueryService.getCartForSubmission(clientId);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        cart.getItems().forEach(cartItem -> {
            if (cartItem.getBook().isArchived()) {
                throw new UnavailableBookException();
            }
        });

        BigDecimal totalPrice = cart.getItems().stream()
                .map(item -> item.getBook().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(userService.getRefById(clientId), totalPrice);

        for (CartItem item : cart.getItems()) {
            order.addItem(new OrderItem(
                    item.getBook(),
                    item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                    item.getQuantity()
            ));
        }

        cartManagementService.clear(cart);
        return mapper.toOrderDetailsDto(
                orderRepository.save(order),
                new TranslationContext(lang, localizationProps.defaultLanguage())
        );
    }
}
