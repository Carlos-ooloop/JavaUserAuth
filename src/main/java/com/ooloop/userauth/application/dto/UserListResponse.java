package com.ooloop.userauth.application.dto;

import java.util.List;

public record UserListResponse(List<UserResponse> users, int total) {
}
