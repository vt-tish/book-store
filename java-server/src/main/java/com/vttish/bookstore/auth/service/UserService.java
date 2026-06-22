package com.vttish.bookstore.auth.service;

import java.util.UUID;

public interface UserService {
    void block(UUID userId);
    void unblock(UUID userId);
}
