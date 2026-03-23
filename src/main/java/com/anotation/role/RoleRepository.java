package com.anotation.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA cho {@link Role}: kiểm tra trùng tên và tra cứu theo tên.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * @param name tên role
     * @return {@code true} nếu đã có bản ghi với tên đó
     */
    boolean existsByName(String name);

    /**
     * Kiểm tra tên đã dùng bởi bản ghi khác (khi cập nhật).
     *
     * @param name tên mới
     * @param id   id bản ghi hiện tại được loại trừ
     * @return {@code true} nếu trùng với role khác
     */
    boolean existsByNameAndIdNot(String name, UUID id);

    /**
     * Tìm role theo tên chính xác.
     *
     * @param name tên
     * @return optional
     */
    java.util.Optional<Role> findByName(String name);
}
