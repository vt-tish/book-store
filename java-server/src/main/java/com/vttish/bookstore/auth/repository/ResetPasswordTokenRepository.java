package com.vttish.bookstore.auth.repository;

import com.vttish.bookstore.auth.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ResetPasswordTokenRepository extends BaseTokenRepository<ResetPasswordToken> {

    @Query("SELECT r FROM ResetPasswordToken r WHERE r.user.id = :userId")
    Optional<ResetPasswordToken> findByUserId(@Param("userId") UUID userId);
}
