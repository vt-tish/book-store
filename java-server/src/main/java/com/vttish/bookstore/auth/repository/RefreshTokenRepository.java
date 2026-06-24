package com.vttish.bookstore.auth.repository;

import com.vttish.bookstore.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RefreshTokenRepository extends BaseTokenRepository<RefreshToken> {

    @Modifying
    @Query("UPDATE RefreshToken r SET r.status = 'REVOKED' WHERE r.user.id = :userId")
    void revokeAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.status = 'REVOKED' WHERE r.familyId = :familyId")
    void revokeAllByFamilyId(@Param("familyId") UUID familyId);
}
