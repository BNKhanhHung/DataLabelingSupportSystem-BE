package com.anotation.role;

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
 * REST API CRUD vai trò ({@link Role}) tại {@code /api/roles}.
 * <p>
 * Vai trò định nghĩa quyền/nhãn nghiệp vụ cấp dự án (ví dụ Manager, Annotator, Reviewer). Gán
 * cho user thường thực hiện qua thực thể liên kết user–role (ngoài controller này).
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role", description = "Role management APIs")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(summary = "Get all roles",
            description = "Dùng page, size, sort. Sort hợp lệ: id, name, description (ví dụ: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<RoleResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(roleService.getAll(pageable)); // 200
    }

    /**
     * Chi tiết role theo id.
     *
     * @param id UUID role
     * @return HTTP 200
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<RoleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getById(id)); // 200
    }

    /**
     * Tạo role mới; tên phải unique.
     *
     * @param request body validate
     * @return HTTP 201 và {@link RoleResponse}
     */
    @PostMapping
    @Operation(summary = "Create a new role")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.create(request)); // 201
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a role")
    public ResponseEntity<RoleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.update(id, request)); // 200
    }

    /**
     * Xóa role theo id.
     *
     * @param id UUID
     * @return HTTP 204
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
