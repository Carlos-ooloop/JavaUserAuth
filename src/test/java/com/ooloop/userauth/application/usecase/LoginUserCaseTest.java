package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.LoginCommand;
import com.ooloop.userauth.application.dto.LoginResultCommand;
import com.ooloop.userauth.application.exceptions.InvalidCredentialsException;
import com.ooloop.userauth.domain.model.Role;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.PasswordEncoder;
import com.ooloop.userauth.domain.port.TokenGenerator;
import com.ooloop.userauth.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginUserCaseTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private TokenGenerator tokenGenerator;
    private LoginUserCase loginUserCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        tokenGenerator = mock(TokenGenerator.class);
        loginUserCase = new LoginUserCase(userRepository, passwordEncoder, tokenGenerator);
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginCommand command = new LoginCommand("testuser", "password123");
        User user = new User(1L, "testuser", "test@email.com", "encoded_password",
                Role.USER, true, LocalDateTime.now(), null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(tokenGenerator.generate(user)).thenReturn("jwt_token_123");

        LoginResultCommand result = loginUserCase.execute(command);

        assertNotNull(result);
        assertEquals("jwt_token_123", result.token());
    }

    @Test
    void shouldThrowWhenUsernameNotFound() {
        LoginCommand command = new LoginCommand("nonexistent", "password123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> loginUserCase.execute(command));
    }

    @Test
    void shouldThrowWhenPasswordDoesNotMatch() {
        LoginCommand command = new LoginCommand("testuser", "wrongpassword");
        User user = new User(1L, "testuser", "test@email.com", "encoded_password",
                Role.USER, true, LocalDateTime.now(), null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> loginUserCase.execute(command));
    }

    @Test
    void shouldNotGenerateTokenWhenCredentialsAreInvalid() {
        LoginCommand command = new LoginCommand("testuser", "wrongpassword");
        User user = new User(1L, "testuser", "test@email.com", "encoded_password",
                Role.USER, true, LocalDateTime.now(), null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

        try {
            loginUserCase.execute(command);
        } catch (InvalidCredentialsException e) {
            // expected
        }

        verify(tokenGenerator, never()).generate(any());
    }

    @Test
    void shouldReturnTokenOnSuccessfulLogin() {
        LoginCommand command = new LoginCommand("testuser", "correct_password");
        User user = new User(1L, "testuser", "test@email.com", "hashed",
                Role.USER, true, LocalDateTime.now(), null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correct_password", "hashed")).thenReturn(true);
        when(tokenGenerator.generate(user)).thenReturn("valid_token");

        LoginResultCommand result = loginUserCase.execute(command);

        assertEquals("valid_token", result.token());
    }
}
