package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.exceptions.AccessDeniedOperationException;
import com.ooloop.userauth.application.exceptions.UserNotFoundException;
import com.ooloop.userauth.domain.model.Role;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class SoftDeleteUserCaseTest {

    private UserRepository userRepository;
    private SoftDeleteUserCase softDeleteUserCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        softDeleteUserCase = new SoftDeleteUserCase(userRepository);
    }

    @Test
    void shouldSoftDeleteUser() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));

        softDeleteUserCase.execute(1L, "admin");

        verify(userRepository).softDelete(1L);
    }

    @Test
    void shouldThrowWhenAdminTriesToDeleteOwnAccount() {
        User admin = new User(1L, "admin", "admin@email.com", "pass",
                Role.ADMIN, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(admin));

        AccessDeniedOperationException exception = assertThrows(AccessDeniedOperationException.class,
                () -> softDeleteUserCase.execute(1L, "admin"));

        assertTrue(exception.getMessage().contains("cannot delete their own"));
        verify(userRepository, never()).softDelete(anyLong());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByID(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> softDeleteUserCase.execute(999L, "admin"));
    }

    @Test
    void shouldAllowAdminToDeleteOtherUsers() {
        User user = new User(1L, "otheruser", "other@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));

        softDeleteUserCase.execute(1L, "admin");

        verify(userRepository).softDelete(1L);
    }

    @Test
    void shouldNotCallSoftDeleteWhenUserIsCurrentUser() {
        User user = new User(1L, "sameuser", "same@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));

        try {
            softDeleteUserCase.execute(1L, "sameuser");
        } catch (AccessDeniedOperationException e) {
            // expected
        }

        verify(userRepository, never()).softDelete(anyLong());
    }
}
