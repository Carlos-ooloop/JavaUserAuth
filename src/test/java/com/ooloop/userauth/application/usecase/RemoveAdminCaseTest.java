package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.UserResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RemoveAdminCaseTest {

    private UserRepository userRepository;
    private RemoveAdminCase removeAdminCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        removeAdminCase = new RemoveAdminCase(userRepository);
    }

    @Test
    void shouldRemoveAdminRoleFromAnotherAdmin() {
        User admin = new User(1L, "admin2", "admin2@email.com", "pass",
                Role.ADMIN, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(admin));

        User updatedUser = new User(1L, "admin2", "admin2@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponse response = removeAdminCase.execute(1L, "currentAdmin");

        assertEquals("USER", response.role());
    }

    @Test
    void shouldThrowWhenAdminTriesToRemoveOwnAdminRole() {
        User admin = new User(1L, "myadmin", "myadmin@email.com", "pass",
                Role.ADMIN, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(admin));

        AccessDeniedOperationException exception = assertThrows(AccessDeniedOperationException.class,
                () -> removeAdminCase.execute(1L, "myadmin"));

        assertTrue(exception.getMessage().contains("cannot remove their own"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByID(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> removeAdminCase.execute(999L, "admin"));
    }

    @Test
    void shouldDemoteAdminToUser() {
        User admin = new User(1L, "target", "target@email.com", "pass",
                Role.ADMIN, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(admin));

        User updatedUser = new User(1L, "target", "target@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponse response = removeAdminCase.execute(1L, "otherAdmin");

        assertEquals("USER", response.role());
    }

    @Test
    void shouldNotAllowSelfDemotion() {
        User admin = new User(1L, "selfadmin", "self@email.com", "pass",
                Role.ADMIN, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(admin));

        assertThrows(AccessDeniedOperationException.class,
                () -> removeAdminCase.execute(1L, "selfadmin"));
    }
}
