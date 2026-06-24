package com.vttish.bookstore.orders.service.impl;

import com.vttish.bookstore.auth.service.UserService;
import com.vttish.bookstore.books.entity.Book;
import com.vttish.bookstore.books.service.BookQueryService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderSubmissionServiceImpl implements OrderSubmissionService {
    private final OrderRepository orderRepository;
    private final CartQueryService cartQueryService;
    private final BookQueryService bookQueryService;
    private final UserService userService;
    private final CartManagementService cartManagementService;
    private final OrderMapper mapper;

    @Override
    @Transactional
    public OrderDetailsResponseDto submitByClientId(UUID clientId, String lang) {
        Cart cart = cartQueryService.getCartForSubmission(clientId);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        if (cart.getItems().stream().anyMatch(item -> item.getBook().isArchived())) {
            throw new UnavailableBookException();
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Book> books = new ArrayList<>();
        Order order = new Order(userService.getRefById(clientId));

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            BigDecimal itemPrice = book.getPrice();
            totalPrice = totalPrice.add(
                    itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()))
            );

            order.addItem(new OrderItem(book, itemPrice, item.getQuantity()));
            books.add(book);
        }

        order.setTotalPrice(totalPrice);
        cartManagementService.clear(cart);

        return mapper.toOrderDetailsDto(
                orderRepository.save(order),
                bookQueryService.getTranslations(books, lang)
        );
    }
}
