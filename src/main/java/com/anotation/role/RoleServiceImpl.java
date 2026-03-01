package com.anotation.role;

import com.anotation.common.PageResponse;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> getAll(Pageable pageable) {
        try {
            return PageResponse.from(roleRepository.findAll(pageable), roleMapper::toResponse);
        } catch (PropertyReferenceException e) {
            // Sort property không tồn tại (vd: sort=string) → dùng mặc định sort theo id
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(roleRepository.findAll(safe), roleMapper::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(UUID id) {
        return roleMapper.toResponse(findOrThrow(id));
    }

    @Override
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new DuplicateException("Role name already exists: " + request.getName());
        }
        Role role = roleMapper.toEntity(request);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse update(UUID id, RoleRequest request) {
        Role role = findOrThrow(id);

        if (roleRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new DuplicateException("Role name already exists: " + request.getName());
        }

        roleMapper.updateEntity(request, role);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        roleRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private Role findOrThrow(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));
    }
}
