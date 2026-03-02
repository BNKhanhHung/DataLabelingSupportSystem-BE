package com.anotation.auth;

import com.anotation.user.UserCreateRequest;
import com.anotation.user.UserResponse;
import com.anotation.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth Controller — Login and public register (for testing).
 *
 * Admin creates user accounts via POST /api/users (requires ADMIN).
 * POST /api/auth/register is public, for testing only — no authorization required.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login and public register API")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username/email and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "[Public - for testing] Create a new user account without authorization")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }
}
