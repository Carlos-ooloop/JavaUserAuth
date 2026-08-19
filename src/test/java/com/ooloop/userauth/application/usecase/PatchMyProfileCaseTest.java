package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.PatchMyProfileCommand;
import com.ooloop.userauth.application.dto.UserResponse;
import com.ooloop.userauth.application.exceptions.UserAlreadyExistsException;
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

class PatchMyProfileCaseTest {

    private UserRepository userRepository;
    private PatchMyProfileCase patchMyProfileCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        patchMyProfileCase = new PatchMyProfileCase(userRepository);
    }

    @Test
    void shouldUpdateUsername() {
        User user = new User(1L, "oldname", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByUsername("oldname")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newname")).thenReturn(false);

        User updatedUser = new User(1L, "newname", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        PatchMyProfileCommand command = new PatchMyProfileCommand("newname", null);
        UserResponse response = patchMyProfileCase.execute("oldname", command);

        assertEquals("newname", response.username());
    }

    @Test
    void shouldUpdateEmail() {
        User user = new User(1L, "testuser", "old@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);

        User updatedUser = new User(1L, "testuser", "new@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        PatchMyProfileCommand command = new PatchMyProfileCommand(null, "new@email.com");
        UserResponse response = patchMyProfileCase.execute("testuser", command);

        assertEquals("new@email.com", response.email());
    }

    @Test
    void shouldThrowWhenNewUsernameAlreadyExists() {
        User user = new User(1L, "current", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByUsername("current")).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        PatchMyProfileCommand command = new PatchMyProfileCommand("taken", null);

        assertThrows(UserAlreadyExistsException.class,
                () -> patchMyProfileCase.execute("current", command));
    }

    @Test
    void shouldThrowWhenNewEmailAlreadyExists() {
        User user = new User(1L, "testuser", "old@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@email.com")).thenReturn(true);

        PatchMyProfileCommand command = new PatchMyProfileCommand(null, "taken@email.com");

        assertThrows(UserAlreadyExistsException.class,
                () -> patchMyProfileCase.execute("testuser", command));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        PatchMyProfileCommand command = new PatchMyProfileCommand("newname", null);

        assertThrows(UserNotFoundException.class,
                () -> patchMyProfileCase.execute("nonexistent", command));
    }

    @Test
    void shouldNotUpdateWhenSameValues() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        PatchMyProfileCommand command = new PatchMyProfileCommand("testuser", "test@email.com");
        UserResponse response = patchMyProfileCase.execute("testuser", command);

        assertEquals("testuser", response.username());
        assertEquals("test@email.com", response.email());
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
    }
}
