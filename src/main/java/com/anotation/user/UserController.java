package com.anotation.user;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// ============================================================
// CODE CŨ (team có sẵn) - đã được refactor bên dưới
// ============================================================
//
// @RestController
// @RequestMapping("/users")
// public class UserController {
//     private final UserRepository repository;
//
//     public UserController(UserRepository repository) {
//         this.repository = repository;
//     }
//
//     @PostMapping
//     public User create(@Valid @RequestBody UserCreateRequest request) {
//         User user = new User();
//         user.setUsername(request.getUsername());
//         user.setEmail(request.getEmail());
//         user.setPasswordHash(request.getPasswordHash());
//         user.setStatus(request.getStatus());
//         return repository.save(user);
//     }
// }
//
// ============================================================
// CODE MỚI (refactored)
// ============================================================

/**
 * REST controller quản lý {@link User} dưới tiền tố {@code /api/users}.
 * <p>
 * Cung cấp: liệt kê phân trang, xem theo id, xem hồ sơ user hiện tại ({@code /me}), tạo mới, cập nhật,
 * đổi mật khẩu cho chính mình ({@code PATCH /me/password}) và xóa user. Phân quyền cụ thể do cấu hình Spring Security quy định.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    /**
     * @param userService dịch vụ nghiệp vụ user
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET {@code /api/users} — danh sách user có phân trang và sort (nên dùng field hợp lệ: id, username, email, …).
     */
    @GetMapping
    @Operation(summary = "Get all users",
            description = "Sort hợp lệ: id, username, email, status, systemRole, createdAt, updatedAt (vd: sort=username,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<UserResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable)); // 200
    }

    /**
     * GET {@code /api/users/{id}} — chi tiết một user.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id)); // 200
    }

    /**
     * GET {@code /api/users/me} — hồ sơ user ứng với principal hiện tại.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    /**
     * POST {@code /api/users} — tạo user mới; trả về 201 và body {@link UserResponse}.
     */
    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request)); // 201
    }

    /**
     * PUT {@code /api/users/{id}} — cập nhật toàn bộ các trường theo {@link UserCreateRequest} (theo quy ước API).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a user")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.update(id, request)); // 200
    }

    /**
     * PATCH {@code /api/users/me/password} — đổi mật khẩu; thành công trả 204 No Content.
     */
    @PatchMapping("/me/password")
    @Operation(summary = "Change current user's password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE {@code /api/users/{id}} — xóa user; trả 204.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        userService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build(); // 204
    }
}
