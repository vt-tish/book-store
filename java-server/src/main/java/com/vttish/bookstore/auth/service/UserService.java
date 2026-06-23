package com.vttish.bookstore.auth.service;

import com.vttish.bookstore.auth.entity.User;

import java.util.UUID;

public interface UserService {
    User getRefById(UUID id);
    void block(UUID userId);
    void unblock(UUID userId);
}
