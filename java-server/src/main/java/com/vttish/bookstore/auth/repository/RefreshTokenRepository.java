package com.vttish.bookstore.auth.repository;

import com.vttish.bookstore.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RefreshTokenRepository extends BaseTokenRepository<RefreshToken> {
    List<RefreshToken> findAllByFamilyId(UUID familyId);

    @Query("SELECT r FROM RefreshToken r WHERE r.user.id = :userId")
    List<RefreshToken> findAllByUserId(@Param("userId") UUID userId);
}
