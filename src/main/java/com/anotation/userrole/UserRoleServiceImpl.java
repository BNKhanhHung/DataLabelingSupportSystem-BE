package com.anotation.userrole;

import com.anotation.common.PageResponse;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.role.Role;
import com.anotation.role.RoleRepository;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Triển khai {@link UserRoleService}: validate tồn tại {@link User} và {@link Role}, chống trùng {@code (user, role)},
 * và xử lý sort an toàn khi client gửi field sort không hợp lệ.
 */
@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMapper userRoleMapper;

    /**
     * @param userRoleRepository persistence user-role
     * @param userRepository     tải user
     * @param roleRepository     tải role định nghĩa
     * @param userRoleMapper     map entity → DTO
     */
    public UserRoleServiceImpl(
            UserRoleRepository userRoleRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleMapper userRoleMapper) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleMapper = userRoleMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserRoleResponse> getAll(Pageable pageable) {
        try {
            return PageResponse.from(userRoleRepository.findAll(pageable), userRoleMapper::toResponse);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(userRoleRepository.findAll(safe), userRoleMapper::toResponse);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserRoleResponse getById(UUID id) {
        return userRoleMapper.toResponse(findOrThrow(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserRoleResponse> getByUser(UUID userId, Pageable pageable) {
        try {
            return PageResponse.from(userRoleRepository.findByUserId(userId, pageable),
                    userRoleMapper::toResponse);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(userRoleRepository.findByUserId(userId, safe),
                    userRoleMapper::toResponse);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRoleResponse assign(UserRoleRequest request) {
        // 1. Validate: User phải tồn tại
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.getUserId()));

        // 2. Validate: Role phải tồn tại
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role not found: " + request.getRoleId()));

        // 3. Prevent duplicate assignment
        if (userRoleRepository.existsByUserIdAndRoleId(
                request.getUserId(), request.getRoleId())) {
            throw new DuplicateException(
                    "This user already has this role.");
        }

        // 4. Create and save
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        return userRoleMapper.toResponse(userRoleRepository.save(userRole));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        userRoleRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    /**
     * Tải {@link UserRole} theo id hoặc ném {@link NotFoundException}.
     */
    private UserRole findOrThrow(UUID id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("UserRole not found with id: " + id));
    }
}
