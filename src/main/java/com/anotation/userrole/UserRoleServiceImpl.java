package com.anotation.userrole;

import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.project.Project;
import com.anotation.project.ProjectRepository;
import com.anotation.role.Role;
import com.anotation.role.RoleRepository;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProjectRepository projectRepository;
    private final UserRoleMapper userRoleMapper;

    public UserRoleServiceImpl(
            UserRoleRepository userRoleRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            ProjectRepository projectRepository,
            UserRoleMapper userRoleMapper) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.projectRepository = projectRepository;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleResponse> getAll() {
        return userRoleRepository.findAll()
                .stream()
                .map(userRoleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleResponse getById(UUID id) {
        return userRoleMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleResponse> getByProject(UUID projectId) {
        return userRoleRepository.findByProjectId(projectId)
                .stream()
                .map(userRoleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleResponse> getByUser(UUID userId) {
        return userRoleRepository.findByUserId(userId)
                .stream()
                .map(userRoleMapper::toResponse)
                .toList();
    }

    @Override
    public UserRoleResponse assign(UserRoleRequest request) {
        // 1. Validate: User phải tồn tại
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found: " + request.getUserId()));

        // 2. Validate: Role phải tồn tại
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role not found: " + request.getRoleId()));

        // 3. Validate: Project phải tồn tại
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new NotFoundException("Project not found: " + request.getProjectId()));

        // 4. Prevent duplicate assignment
        if (userRoleRepository.existsByUserIdAndRoleIdAndProjectId(
                request.getUserId(), request.getRoleId(), request.getProjectId())) {
            throw new DuplicateException(
                    "This user already has this role in the given project.");
        }

        // 5. Create and save
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setProject(project);

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
