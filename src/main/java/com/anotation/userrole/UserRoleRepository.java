package com.anotation.userrole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// ============================================================
// CODE CŨ  - đã được refactor bên dưới
// ============================================================
//
// public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
//     // Interface rỗng, không có method nào
// }
//
// ============================================================
// CODE MỚI (refactored)
// ============================================================

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    // Check duplicate: cùng user + role + project
    boolean existsByUserIdAndRoleIdAndProjectId(UUID userId, UUID roleId, UUID projectId);

    // Lấy tất cả role của 1 user trong 1 project
    List<UserRole> findByUserIdAndProjectId(UUID userId, UUID projectId);

    // Lấy tất cả assignments của 1 project
    List<UserRole> findByProjectId(UUID projectId);

    // Lấy tất cả assignments của 1 user
    List<UserRole> findByUserId(UUID userId);

    // Role-name check: used by TaskServiceImpl to validate annotator/reviewer
    boolean existsByUserIdAndProjectIdAndRoleNameIgnoreCase(UUID userId, UUID projectId, String roleName);
}
