package com.ooloop.userauth.infraestructure.adapter.in.web;

import com.ooloop.userauth.application.dto.LoginCommand;
import com.ooloop.userauth.application.dto.LoginResultCommand;
import com.ooloop.userauth.application.dto.RegisterUserCommand;
import com.ooloop.userauth.application.dto.RegisterUserResponse;
import com.ooloop.userauth.application.usecase.LoginUserCase;
import com.ooloop.userauth.application.usecase.RegisterUserCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserCase registerUserCase;
    private final LoginUserCase loginUserCase;

    public AuthController(RegisterUserCase registerUserCase, LoginUserCase loginUserCase) {
        this.registerUserCase = registerUserCase;
        this.loginUserCase = loginUserCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserResponse register(@Valid @RequestBody RegisterUserCommand command) {
        return registerUserCase.execute(command);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResultCommand> login(@Valid @RequestBody LoginCommand loginCommand) {
        LoginResultCommand result = loginUserCase.execute(
                new LoginCommand(loginCommand.username(), loginCommand.password()));
        return ResponseEntity.ok(result);
    }
}
