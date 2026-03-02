package com.anotation.userrole;

import com.anotation.common.PageResponse;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.role.Role;
import com.anotation.role.RoleRepository;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMapper userRoleMapper;

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

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserRoleResponse> getAll(Pageable pageable) {
        return PageResponse.from(userRoleRepository.findAll(pageable), userRoleMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleResponse getById(UUID id) {
        return userRoleMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserRoleResponse> getByUser(UUID userId, Pageable pageable) {
        return PageResponse.from(userRoleRepository.findByUserId(userId, pageable),
                userRoleMapper::toResponse);
    }

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

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        userRoleRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private UserRole findOrThrow(UUID id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("UserRole not found with id: " + id));
    }
}
