package com.vttish.bookstore.clients.repository;

import com.vttish.bookstore.clients.entity.Client;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    @Override
    @EntityGraph(attributePaths = { "user" })
    @NonNull Page<Client> findAll(@NonNull Pageable pageable);
}
