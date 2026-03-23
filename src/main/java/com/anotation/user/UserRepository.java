package com.anotation.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Spring Data JPA cho entity {@link User}: truy vấn theo username/email, kiểm tra tồn tại và lọc theo tập {@link SystemRole}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Tất cả user có {@link SystemRole} thuộc tập cho trước (ví dụ gửi thông báo cho Manager/Admin).
     *
     * @param roles tập role
     * @return danh sách user
     */
    List<User> findBySystemRoleIn(Set<SystemRole> roles);

    /**
     * Tìm user theo username (đăng nhập).
     */
    Optional<User> findByUsername(String username);

    /**
     * Tìm user theo email.
     */
    Optional<User> findByEmail(String email);

    /**
     * {@code true} nếu đã có user với email này.
     */
    boolean existsByEmail(String email);

    /**
     * {@code true} nếu đã có user với username này.
     */
    boolean existsByUsername(String username);

    /**
     * Trùng email với user khác (loại trừ một id — dùng khi cập nhật).
     */
    boolean existsByEmailAndIdNot(String email, UUID id);

    /**
     * Trùng username với user khác (loại trừ một id).
     */
    boolean existsByUsernameAndIdNot(String username, UUID id);
}
