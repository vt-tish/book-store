package com.vttish.bookstore.auth.repository;

import com.vttish.bookstore.auth.entity.ResetPasswordToken;
import com.vttish.bookstore.auth.entity.VerifyToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VerifyTokenRepository extends BaseTokenRepository<VerifyToken> {

    @Query("SELECT v FROM VerifyToken v WHERE v.user.id = :userId")
    Optional<VerifyToken> findByUserId(@Param("userId") UUID userId);
}
