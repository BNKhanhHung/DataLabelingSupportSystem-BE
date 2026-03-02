package com.anotation.userrole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

    // Check duplicate: cùng user + role
    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);

    // Lấy tất cả assignments của 1 user
    Page<UserRole> findByUserId(UUID userId, Pageable pageable);

    // Role-name check: used by TaskServiceImpl to validate annotator/reviewer
    boolean existsByUserIdAndRoleNameIgnoreCase(UUID userId, String roleName);
}
