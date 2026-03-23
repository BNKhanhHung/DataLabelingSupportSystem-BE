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
 * REST controller xác thực và đăng ký công khai; tiền tố {@code /api/auth}.
 * {@code POST /login}: nhận {@link LoginRequest} (username hoặc email + mật khẩu), trả {@link AuthResponse} gồm JWT và thông tin user ({@link AuthService#login}).
 * {@code POST /register}: nhận {@link com.anotation.user.UserCreateRequest}, luôn tạo tài khoản vai trò USER (systemRole bị ghi đè {@code null} rồi service gán USER), status ACTIVE; trả {@link com.anotation.user.UserResponse} (201).
 * Đăng ký công khai bổ sung cho việc Admin/Manager tạo user qua {@code POST /api/users} với role tùy chỉnh.
 * Hai endpoint trên được {@link SecurityConfig} cho phép {@code permitAll}; các API khác yêu cầu Bearer token.
 * OpenAPI tag: {@code Authentication}.
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
