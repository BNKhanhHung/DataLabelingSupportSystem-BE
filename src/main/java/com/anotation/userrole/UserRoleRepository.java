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

/**
 * Spring Data JPA cho {@link UserRole} (khóa chính {@link UUID}): kiểm tra trùng gán, phân trang theo user,
 * và kiểm tra user có role theo tên (không phân biệt hoa thường) — phục vụ ví dụ {@code TaskServiceImpl}.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    /**
     * {@code true} nếu đã tồn tại bản ghi cùng {@code userId} và {@code roleId}.
     */
    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);

    /**
     * Tất cả assignment của một user, phân trang.
     */
    Page<UserRole> findByUserId(UUID userId, Pageable pageable);

    /**
     * User có role với tên cho trước hay không (dùng khi validate Annotator/Reviewer).
     */
    boolean existsByUserIdAndRoleNameIgnoreCase(UUID userId, String roleName);
}
