package com.ooloop.userauth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginCommand(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") String password
) {
}
