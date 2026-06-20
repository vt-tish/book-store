package com.vttish.bookstore.auth.repository;

import com.vttish.bookstore.auth.entity.BaseToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface BaseTokenRepository <T extends BaseToken> extends JpaRepository<T, UUID> {
    Optional<T> findByToken(String token);

    @Modifying
    @Query("DELETE FROM #{#entityName} t WHERE t.expiresAt < CURRENT_TIMESTAMP")
    int deleteAllExpired();
}
