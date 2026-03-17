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
 * Auth API: đăng nhập (JWT) và đăng ký công khai (chỉ tạo USER). Frontend gọi POST /api/auth/login (login.html); register dùng POST /api/auth/register. User khác do Admin tạo qua POST /api/users.
 * POST /login (username/email + password → token, user); POST /register (username, email, password → user).
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
    @Operation(summary = "Register a new user account (always USER role)")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest request) {
        // Public register luôn tạo USER — chỉ Admin mới set role khác qua POST
        // /api/users
        request.setSystemRole(null);
        // Force status to ACTIVE, preventing Swagger's "string" placeholder going to DB
        request.setStatus("ACTIVE");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }
}
