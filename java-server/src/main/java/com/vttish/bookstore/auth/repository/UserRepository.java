package com.vttish.bookstore.auth.repository;

import com.vttish.bookstore.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.isVerified = false AND u.createdAt < :cutoffTime")
    int deleteUnverifiedOlderThan(@Param("cutoffTime") Instant cutoffTime);
}
