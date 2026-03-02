package com.anotation.userrole;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/user-roles")
@Tag(name = "UserRole", description = "User-Role-Project assignment APIs")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    @Operation(summary = "Get all user-role assignments")
    public ResponseEntity<PageResponse<UserRoleResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(userRoleService.getAll(pageable)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID")
    public ResponseEntity<UserRoleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userRoleService.getById(id)); // 200
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all roles assigned to a user")
    public ResponseEntity<PageResponse<UserRoleResponse>> getByUser(
            @PathVariable UUID userId,
            Pageable pageable) {
        return ResponseEntity.ok(userRoleService.getByUser(userId, pageable)); // 200
    }

    @PostMapping
    @Operation(summary = "Assign a role to user")
    public ResponseEntity<UserRoleResponse> assign(@Valid @RequestBody UserRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userRoleService.assign(request)); // 201
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a role assignment")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userRoleService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
