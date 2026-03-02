package com.anotation.user;

import com.anotation.common.PageResponse;
import com.anotation.exception.BadRequestException;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * User management service — used by ADMIN to create/manage employee accounts.
 * Handles BCrypt password hashing automatically.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAll(Pageable pageable) {
        try {
            return PageResponse.from(userRepository.findAll(pageable), userMapper::toResponse);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(userRepository.findAll(safe), userMapper::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return userMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Invalid token or user not found."));
        return userMapper.toResponse(user);
    }

    @Override
    public void changePassword(String username, PasswordChangeRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Invalid token or user not found."));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Old password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        // 1. Check duplicate
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateException("Email already exists: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateException("Username already exists: " + request.getUsername());
        }

        // 2. Build entity
        User user = userMapper.toEntity(request);

        // 3. Hash password with BCrypt
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // 4. Set system role (default USER if not specified)
        if (request.getSystemRole() != null && !request.getSystemRole().isBlank()) {
            user.setSystemRole(SystemRole.valueOf(request.getSystemRole().toUpperCase()));
        }
        // else: defaults to SystemRole.USER via entity @PrePersist

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse update(UUID id, UserCreateRequest request) {
        User user = findOrThrow(id);

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateException("Email already exists: " + request.getEmail());
        }
        if (userRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
            throw new DuplicateException("Username already exists: " + request.getUsername());
        }

        userMapper.updateEntity(request, user);

        // Update password only if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        // Update system role if provided
        if (request.getSystemRole() != null && !request.getSystemRole().isBlank()) {
            user.setSystemRole(SystemRole.valueOf(request.getSystemRole().toUpperCase()));
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        userRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private User findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }
}
