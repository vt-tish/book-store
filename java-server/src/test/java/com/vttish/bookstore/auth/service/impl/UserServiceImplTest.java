package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.entity.User;
import com.vttish.bookstore.auth.entity.enums.Role;
import com.vttish.bookstore.auth.exception.UserNotFoundException;
import com.vttish.bookstore.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void block_ShouldSetBlockedToTrue_WhenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User("test@example.com", "pass", Role.CLIENT);
        user.setBlocked(false);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        userService.block(userId);
        
        assertTrue(user.isBlocked());
        verify(userRepository).save(user);
    }

    @Test
    void block_ShouldDoNothing_WhenUserIsAlreadyBlocked() {
        UUID userId = UUID.randomUUID();
        User user = new User("test@example.com", "pass", Role.CLIENT);
        user.setBlocked(true);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        userService.block(userId);
        
        assertTrue(user.isBlocked());
        verify(userRepository, never()).save(any());
    }

    @Test
    void unblock_ShouldSetBlockedToFalse_WhenUserIsBlocked() {
        UUID userId = UUID.randomUUID();
        User user = new User("test@example.com", "pass", Role.CLIENT);
        user.setBlocked(true);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        userService.unblock(userId);
        
        assertFalse(user.isBlocked());
        verify(userRepository).save(user);
    }

    @Test
    void unblock_ShouldDoNothing_WhenUserIsAlreadyUnblocked() {
        UUID userId = UUID.randomUUID();
        User user = new User("test@example.com", "pass", Role.CLIENT);
        user.setBlocked(false);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        userService.unblock(userId);
        
        assertFalse(user.isBlocked());
        verify(userRepository, never()).save(any());
    }

    @Test
    void block_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        assertThrows(UserNotFoundException.class, () -> userService.block(userId));
    }

    @Test
    void getRefById_ShouldReturnProxy() {
        UUID userId = UUID.randomUUID();
        User user = new User("test@example.com", "pass", Role.CLIENT);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        User result = userService.getRefById(userId);

        assertEquals(user, result);
    }
}
