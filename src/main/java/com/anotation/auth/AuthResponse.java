package com.anotation.auth;

import java.util.UUID;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private UUID userId;
    private String username;
    private String email;
    private String systemRole;

    public AuthResponse(String token, UUID userId, String username, String email, String systemRole) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.systemRole = systemRole;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }
}
