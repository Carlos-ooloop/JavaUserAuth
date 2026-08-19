package com.ooloop.userauth.application.usecase;

import com.ooloop.userauth.application.dto.UserListResponse;
import com.ooloop.userauth.domain.model.Role;
import com.ooloop.userauth.domain.model.User;
import com.ooloop.userauth.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetAllUsersCaseTest {

    private UserRepository userRepository;
    private GetAllUsersCase getAllUsersCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        getAllUsersCase = new GetAllUsersCase(userRepository);
    }

    @Test
    void shouldReturnAllActiveUsers() {
        List<User> users = List.of(
                new User(1L, "user1", "user1@email.com", "pass", Role.USER, true, LocalDateTime.now(), null),
                new User(2L, "user2", "user2@email.com", "pass", Role.ADMIN, true, LocalDateTime.now(), null)
        );
        when(userRepository.findAllActive()).thenReturn(users);

        UserListResponse response = getAllUsersCase.execute();

        assertEquals(2, response.total());
        assertEquals(2, response.users().size());
        assertEquals("user1", response.users().get(0).username());
        assertEquals("user2", response.users().get(1).username());
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        when(userRepository.findAllActive()).thenReturn(Collections.emptyList());

        UserListResponse response = getAllUsersCase.execute();

        assertEquals(0, response.total());
        assertTrue(response.users().isEmpty());
    }

    @Test
    void shouldNotReturnDeletedUsers() {
        List<User> activeUsers = List.of(
                new User(1L, "active", "active@email.com", "pass", Role.USER, true, LocalDateTime.now(), null)
        );
        when(userRepository.findAllActive()).thenReturn(activeUsers);

        UserListResponse response = getAllUsersCase.execute();

        assertEquals(1, response.total());
        verify(userRepository).findAllActive();
    }

    @Test
    void shouldMapUserFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.of(2025, 1, 15, 10, 30);
        List<User> users = List.of(
                new User(1L, "admin", "admin@email.com", "pass", Role.ADMIN, true, now, null)
        );
        when(userRepository.findAllActive()).thenReturn(users);

        UserListResponse response = getAllUsersCase.execute();

        var user = response.users().get(0);
        assertEquals(1L, user.id());
        assertEquals("admin", user.username());
        assertEquals("admin@email.com", user.email());
        assertEquals("ADMIN", user.role());
        assertEquals(now, user.createdAt());
    }
}
