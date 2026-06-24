package com.vttish.bookstore.cart.repository;

import com.vttish.bookstore.cart.dto.CartItemDto;
import com.vttish.bookstore.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    @Query("""
        SELECT new com.vttish.bookstore.cart.dto.CartItemDto(
            c.id,
            b.id,
            t.name,
            b.previewUrl,
            c.quantity,
            b.price,
            (b.price * c.quantity),
            (b.isArchived = false)
        )
        FROM CartItem c
        JOIN c.book b
        JOIN b.translations t
        WHERE c.cart.id = :cartId
        AND t.languageCode = :lang
    """)
    List<CartItemDto> findAllWithLocalizedBooks(@Param("cartId") UUID cartId, @Param("lang") String lang);

    boolean existsByBookId(UUID bookId);
}
