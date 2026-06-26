package com.vttish.bookstore.common.events;

import com.vttish.bookstore.auth.entity.enums.Role;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        Role role
) {}
