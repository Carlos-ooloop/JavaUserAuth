package com.ooloop.userauth.infraestructure.adapter.in.web;

import com.ooloop.userauth.application.dto.*;
import com.ooloop.userauth.application.usecase.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final GetAllUsersCase getAllUsersCase;
    private final GetUserByIdCase getUserByIdCase;
    private final PatchUserCase patchUserCase;
    private final PatchMyProfileCase patchMyProfileCase;
    private final PromoteToAdminCase promoteToAdminCase;
    private final RemoveAdminCase removeAdminCase;
    private final SoftDeleteUserCase softDeleteUserCase;

    public UserController(GetAllUsersCase getAllUsersCase, GetUserByIdCase getUserByIdCase,
                          PatchUserCase patchUserCase, PatchMyProfileCase patchMyProfileCase,
                          PromoteToAdminCase promoteToAdminCase, RemoveAdminCase removeAdminCase,
                          SoftDeleteUserCase softDeleteUserCase) {
        this.getAllUsersCase = getAllUsersCase;
        this.getUserByIdCase = getUserByIdCase;
        this.patchUserCase = patchUserCase;
        this.patchMyProfileCase = patchMyProfileCase;
        this.promoteToAdminCase = promoteToAdminCase;
        this.removeAdminCase = removeAdminCase;
        this.softDeleteUserCase = softDeleteUserCase;
    }

    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers() {
        return ResponseEntity.ok(getAllUsersCase.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(getUserByIdCase.execute(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(getUserByIdCase.executeByUsername(username));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable Long id,
                                                   @RequestBody PatchUserCommand command) {
        return ResponseEntity.ok(patchUserCase.execute(id, command));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> patchMyProfile(Authentication authentication,
                                                        @RequestBody PatchMyProfileCommand command) {
        return ResponseEntity.ok(patchMyProfileCase.execute(authentication.getName(), command));
    }

    @PatchMapping("/{id}/promote")
    public ResponseEntity<UserResponse> promoteToAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(promoteToAdminCase.execute(id));
    }

    @PatchMapping("/{id}/demote")
    public ResponseEntity<UserResponse> removeAdmin(@PathVariable Long id,
                                                     Authentication authentication) {
        return ResponseEntity.ok(removeAdminCase.execute(id, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                            Authentication authentication) {
        softDeleteUserCase.execute(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
