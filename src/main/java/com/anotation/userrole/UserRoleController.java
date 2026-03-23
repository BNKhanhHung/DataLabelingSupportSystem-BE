package com.anotation.userrole;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API quản lý bản ghi {@link UserRole}: gán role nghiệp vụ (Annotator, Reviewer, …) cho user.
 * <p>
 * Base path {@code /api/user-roles}: liệt kê, tra cứu theo id, theo user, tạo gán mới (POST) và xóa gán (DELETE).
 */
@RestController
@RequestMapping("/api/user-roles")
@Tag(name = "UserRole", description = "User-Role-Project assignment APIs")
public class UserRoleController {

    private final UserRoleService userRoleService;

    /**
     * @param userRoleService dịch vụ user-role
     */
    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * GET {@code /api/user-roles} — tất cả assignment phân trang.
     */
    @GetMapping
    @Operation(summary = "Get all user-role assignments",
            description = "Sort hợp lệ: id (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<UserRoleResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userRoleService.getAll(pageable)); // 200
    }

    /**
     * GET {@code /api/user-roles/{id}} — một assignment theo UUID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID")
    public ResponseEntity<UserRoleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userRoleService.getById(id)); // 200
    }

    /**
     * GET {@code /api/user-roles/user/{userId}} — mọi role đã gán cho user đó, phân trang.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all roles assigned to a user",
            description = "Sort hợp lệ: id (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<UserRoleResponse>> getByUser(
            @PathVariable UUID userId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userRoleService.getByUser(userId, pageable)); // 200
    }

    /**
     * POST {@code /api/user-roles} — gán role cho user; 201 Created nếu thành công.
     */
    @PostMapping
    @Operation(summary = "Assign a role to user")
    public ResponseEntity<UserRoleResponse> assign(@Valid @RequestBody UserRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userRoleService.assign(request)); // 201
    }

    /**
     * DELETE {@code /api/user-roles/{id}} — gỡ một assignment; 204 No Content.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a role assignment")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userRoleService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
