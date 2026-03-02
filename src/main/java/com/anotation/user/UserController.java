package com.anotation.user;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<PageResponse<UserResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id)); // 200
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request)); // 201
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user")
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.update(id, request)); // 200
    }

    @PatchMapping("/me/password")
    @Operation(summary = "Change current user's password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
