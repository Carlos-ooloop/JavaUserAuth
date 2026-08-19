package com.ooloop.userauth.infraestructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ooloop.userauth.application.dto.LoginCommand;
import com.ooloop.userauth.application.dto.LoginResultCommand;
import com.ooloop.userauth.application.dto.RegisterUserCommand;
import com.ooloop.userauth.application.dto.RegisterUserResponse;
import com.ooloop.userauth.application.exceptions.InvalidCredentialsException;
import com.ooloop.userauth.application.exceptions.UserAlreadyExistsException;
import com.ooloop.userauth.application.usecase.LoginUserCase;
import com.ooloop.userauth.application.usecase.RegisterUserCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterUserCase registerUserCase;

    @MockBean
    private LoginUserCase loginUserCase;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand("test@email.com", "testuser", "password123");
        RegisterUserResponse response = new RegisterUserResponse(1L, "testuser", "test@email.com", "USER");

        when(registerUserCase.execute(any(RegisterUserCommand.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand("existing@email.com", "testuser", "password123");

        when(registerUserCase.execute(any(RegisterUserCommand.class)))
                .thenThrow(new UserAlreadyExistsException("existing@email.com"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists with: existing@email.com"));
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand("invalid-email", "testuser", "password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUsernameIsBlank() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand("test@email.com", "", "password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPasswordIsTooShort() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand("test@email.com", "testuser", "123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginCommand command = new LoginCommand("testuser", "password123");
        LoginResultCommand result = new LoginResultCommand("jwt_token_123");

        when(loginUserCase.execute(any(LoginCommand.class))).thenReturn(result);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt_token_123"));
    }

    @Test
    void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
        LoginCommand command = new LoginCommand("testuser", "wrongpassword");

        when(loginUserCase.execute(any(LoginCommand.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid Credentials"));
    }

    @Test
    void shouldReturn400WhenLoginUsernameIsBlank() throws Exception {
        LoginCommand command = new LoginCommand("", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenLoginPasswordIsBlank() throws Exception {
        LoginCommand command = new LoginCommand("testuser", "");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }
}
