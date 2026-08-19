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
import static org.mockito.Mockito.*;

class GetUserByIdCaseTest {

    private UserRepository userRepository;
    private GetUserByIdCase getUserByIdCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        getUserByIdCase = new GetUserByIdCase(userRepository);
    }

    @Test
    void shouldReturnUserById() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByID(1L)).thenReturn(Optional.of(user));

        UserResponse response = getUserByIdCase.execute(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("testuser", response.username());
        assertEquals("test@email.com", response.email());
        assertEquals("USER", response.role());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByID(999L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> getUserByIdCase.execute(999L));

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    void shouldReturnUserByUsername() {
        User user = new User(1L, "testuser", "test@email.com", "pass",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserResponse response = getUserByIdCase.executeByUsername("testuser");

        assertEquals("testuser", response.username());
    }

    @Test
    void shouldThrowWhenUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> getUserByIdCase.executeByUsername("nonexistent"));
    }

    @Test
    void shouldNotReturnDeletedUser() {
        when(userRepository.findByID(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> getUserByIdCase.execute(1L));
        verify(userRepository).findByID(1L);
    }
}
