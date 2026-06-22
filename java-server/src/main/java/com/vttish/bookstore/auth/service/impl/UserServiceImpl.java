package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.exception.UserNotFoundException;
import com.vttish.bookstore.auth.repository.UserRepository;
import com.vttish.bookstore.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void block(UUID userId) {
        User user = getEntityById(userId);

        if (user.isBlocked()) {
            return;
        }

        user.setBlocked(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unblock(UUID userId) {
        User user = getEntityById(userId);

        if (!user.isBlocked()) {
            return;
        }

        user.setBlocked(false);
        userRepository.save(user);
    }

    private User getEntityById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }
}
