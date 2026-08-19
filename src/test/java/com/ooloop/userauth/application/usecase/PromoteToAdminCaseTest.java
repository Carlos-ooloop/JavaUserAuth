package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.UserResponse;
import com.ooloop.userauth.application.exceptions.UserNotFoundException;
import com.ooloop.userauth.domain.model.Role;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PromoteToAdminCaseTest {

    private UserRepository userRepository;
    private PromoteToAdminCase promoteToAdminCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        promoteToAdminCase = new PromoteToAdminCase(userRepository);
    }

    @Test
    void shouldPromoteUserToAdmin() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));

        User updatedUser = new User(1L, "testuser", "test@email.com", "pass",
                Role.ADMIN, true, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponse response = promoteToAdminCase.execute(1L);

        assertEquals("ADMIN", response.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByID(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> promoteToAdminCase.execute(999L));
    }

    @Test
    void shouldPromoteEvenIfAlreadyAdmin() {
        User admin = new User(1L, "admin", "admin@email.com", "pass",
                Role.ADMIN, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(admin));
        when(userRepository.save(any(User.class))).thenReturn(admin);

        UserResponse response = promoteToAdminCase.execute(1L);

        assertEquals("ADMIN", response.role());
    }

    @Test
    void shouldCallSaveExactlyOnce() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        promoteToAdminCase.execute(1L);

        verify(userRepository, times(1)).save(any(User.class));
    }
}
