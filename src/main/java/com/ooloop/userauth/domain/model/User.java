package com.ooloop.userauth.domain.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String email;
    private String password;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;

    public Long getId() {

        return id;
    }


    public String getEmail() {
        return email;
    }



    public String getPassword() {
        return password;
    }


    public Role getRole() {
        return role;
    }



    public boolean isEnabled() {
        return enabled;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }



    public User(Long id, String email, String password, Role role, boolean enabled, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }


}
