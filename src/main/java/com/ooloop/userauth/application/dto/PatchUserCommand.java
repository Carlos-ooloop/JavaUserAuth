package com.ooloop.userauth.application.dto;

import com.ooloop.userauth.domain.model.Role;

public record PatchUserCommand(Role role, Boolean enabled) {
}
