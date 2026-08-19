package com.ooloop.userauth.infraestructure.adapter.in.web;

import com.ooloop.userauth.application.dto.RegisterUserCommand;
import com.ooloop.userauth.application.dto.RegisterUserResponse;
import com.ooloop.userauth.application.usecase.RegisterUserCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserCase registerUserCase;

    public AuthController(RegisterUserCase registerUserCase) {
        this.registerUserCase = registerUserCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserResponse register(@RequestBody RegisterUserCommand command){

        return registerUserCase.execute(command);
    }
}
