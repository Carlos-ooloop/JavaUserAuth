package com.ooloop.userauth.infraestructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ooloop.userauth.application.dto.*;
import com.ooloop.userauth.application.exceptions.AccessDeniedOperationException;
import com.ooloop.userauth.application.exceptions.UserNotFoundException;
import com.ooloop.userauth.application.usecase.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetAllUsersCase getAllUsersCase;

    @MockBean
    private GetUserByIdCase getUserByIdCase;

    @MockBean
    private PatchUserCase patchUserCase;

    @MockBean
    private PatchMyProfileCase patchMyProfileCase;

    @MockBean
    private PromoteToAdminCase promoteToAdminCase;

    @MockBean
    private RemoveAdminCase removeAdminCase;

    @MockBean
    private SoftDeleteUserCase softDeleteUserCase;

    // ========== GET /users ==========

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnAllUsersForAdmin() throws Exception {
        UserListResponse response = new UserListResponse(
                List.of(new UserResponse(1L, "user1", "user1@email.com", "USER", LocalDateTime.now())),
                1
        );
        when(getAllUsersCase.execute()).thenReturn(response);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.users[0].username").value("user1"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    // ========== GET /users/{id} ==========

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUserById() throws Exception {
        UserResponse response = new UserResponse(1L, "testuser", "test@email.com", "USER", LocalDateTime.now());
        when(getUserByIdCase.execute(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(getUserByIdCase.execute(999L)).thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    // ========== GET /users/me ==========

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void shouldReturnCurrentUser() throws Exception {
        UserResponse response = new UserResponse(1L, "testuser", "test@email.com", "USER", LocalDateTime.now());
        when(getUserByIdCase.executeByUsername("testuser")).thenReturn(response);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    // ========== PATCH /users/{id} ==========

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPatchUserRole() throws Exception {
        PatchUserCommand command = new PatchUserCommand(com.ooloop.userauth.domain.model.Role.ADMIN, null);
        UserResponse response = new UserResponse(1L, "testuser", "test@email.com", "ADMIN", LocalDateTime.now());
        when(patchUserCase.execute(eq(1L), any(PatchUserCommand.class))).thenReturn(response);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenPatchingNonexistentUser() throws Exception {
        PatchUserCommand command = new PatchUserCommand(com.ooloop.userauth.domain.model.Role.ADMIN, null);
        when(patchUserCase.execute(eq(999L), any(PatchUserCommand.class)))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    // ========== PATCH /users/me ==========

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void shouldPatchOwnProfile() throws Exception {
        PatchMyProfileCommand command = new PatchMyProfileCommand("newname", null);
        UserResponse response = new UserResponse(1L, "newname", "test@email.com", "USER", LocalDateTime.now());
        when(patchMyProfileCase.execute(eq("testuser"), any(PatchMyProfileCommand.class))).thenReturn(response);

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newname"));
    }

    // ========== PATCH /users/{id}/promote ==========

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPromoteUserToAdmin() throws Exception {
        UserResponse response = new UserResponse(1L, "testuser", "test@email.com", "ADMIN", LocalDateTime.now());
        when(promoteToAdminCase.execute(1L)).thenReturn(response);

        mockMvc.perform(patch("/users/1/promote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    // ========== PATCH /users/{id}/demote ==========

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDemoteAdmin() throws Exception {
        UserResponse response = new UserResponse(1L, "target", "target@email.com", "USER", LocalDateTime.now());
        when(removeAdminCase.execute(eq(1L), eq("admin"))).thenReturn(response);

        mockMvc.perform(patch("/users/1/demote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn403WhenAdminTriesToDemoteSelf() throws Exception {
        when(removeAdminCase.execute(eq(1L), eq("admin")))
                .thenThrow(new AccessDeniedOperationException("An admin cannot remove their own admin privileges"));

        mockMvc.perform(patch("/users/1/demote"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("An admin cannot remove their own admin privileges"));
    }

    // ========== DELETE /users/{id} ==========

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldSoftDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn403WhenAdminTriesToDeleteSelf() throws Exception {
        User admin = new com.ooloop.userauth.domain.model.User(
                1L, "admin", "admin@email.com", "pass",
                com.ooloop.userauth.domain.model.Role.ADMIN, true, LocalDateTime.now(), null
        );
        when(getUserByIdCase.execute(1L)).thenReturn(
                new UserResponse(1L, "admin", "admin@email.com", "ADMIN", LocalDateTime.now()));
        when(softDeleteUserCase.execute(eq(1L), eq("admin")))
                .thenThrow(new AccessDeniedOperationException("An admin cannot delete their own account"));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("An admin cannot delete their own account"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenDeletingNonexistentUser() throws Exception {
        when(softDeleteUserCase.execute(eq(999L), any()))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }
}
