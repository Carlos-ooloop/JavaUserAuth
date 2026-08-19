package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.RegisterUserCommand;
import com.ooloop.userauth.application.dto.RegisterUserResponse;
import com.ooloop.userauth.application.exceptions.UserAlreadyExistsException;
import com.ooloop.userauth.domain.model.Role;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.PasswordEncoder;
import com.ooloop.userauth.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserCaseTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RegisterUserCase registerUserCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        registerUserCase = new RegisterUserCase(userRepository, passwordEncoder);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand("test@email.com", "testuser", "password123");

        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        User savedUser = new User(1L, "testuser", "test@email.com", "encoded_password",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        RegisterUserResponse response = registerUserCase.execute(command);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("testuser", response.username());
        assertEquals("test@email.com", response.email());
        assertEquals("USER", response.role());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        RegisterUserCommand command = new RegisterUserCommand("existing@email.com", "testuser", "password123");

        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> registerUserCase.execute(command));

        assertTrue(exception.getMessage().contains("existing@email.com"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        RegisterUserCommand command = new RegisterUserCommand("new@email.com", "existinguser", "password123");

        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> registerUserCase.execute(command));

        assertTrue(exception.getMessage().contains("existinguser"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldEncodePasswordBeforeSaving() {
        RegisterUserCommand command = new RegisterUserCommand("test@email.com", "testuser", "raw_password");

        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("raw_password")).thenReturn("hashed_password");

        User savedUser = new User(1L, "testuser", "test@email.com", "hashed_password",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        registerUserCase.execute(command);

        verify(passwordEncoder).encode("raw_password");
    }

    @Test
    void shouldAssignDefaultUserRole() {
        RegisterUserCommand command = new RegisterUserCommand("test@email.com", "testuser", "password123");

        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        User savedUser = new User(1L, "testuser", "test@email.com", "encoded",
                Role.USER, true, LocalDateTime.now(), null);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        RegisterUserResponse response = registerUserCase.execute(command);

        assertEquals("USER", response.role());
    }
}
