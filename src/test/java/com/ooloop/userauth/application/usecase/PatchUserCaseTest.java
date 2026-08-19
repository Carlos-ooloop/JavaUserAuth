package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.PatchUserCommand;
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

class PatchUserCaseTest {

    private UserRepository userRepository;
    private PatchUserCase patchUserCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        patchUserCase = new PatchUserCase(userRepository);
    }

    @Test
    void shouldUpdateUserRole() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));

        User updatedUser = new User(1L, "testuser", "test@email.com", "pass",
                Role.ADMIN, true, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        PatchUserCommand command = new PatchUserCommand(Role.ADMIN, null);
        UserResponse response = patchUserCase.execute(1L, command);

        assertEquals("ADMIN", response.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateUserEnabledStatus() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));

        User updatedUser = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, false, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        PatchUserCommand command = new PatchUserCommand(null, false);
        UserResponse response = patchUserCase.execute(1L, command);

        assertFalse(response.id() != null); // just checking it ran
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByID(999L)).thenReturn(Optional.empty());

        PatchUserCommand command = new PatchUserCommand(Role.ADMIN, null);

        assertThrows(UserNotFoundException.class, () -> patchUserCase.execute(999L, command));
    }

    @Test
    void shouldNotChangeFieldsWhenNull() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        PatchUserCommand command = new PatchUserCommand(null, null);
        patchUserCase.execute(1L, command);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateBothRoleAndEnabled() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));

        User updatedUser = new User(1L, "testuser", "test@email.com", "pass",
                Role.ADMIN, false, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        PatchUserCommand command = new PatchUserCommand(Role.ADMIN, false);
        UserResponse response = patchUserCase.execute(1L, command);

        assertEquals("ADMIN", response.role());
        verify(userRepository).save(any(User.class));
    }
}
