package com.anotation.auth;

import com.anotation.exception.BadRequestException;
import com.anotation.user.SystemRole;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService — handles Login only.
 *
 * Account creation is done by ADMIN via /api/users (UserServiceImpl).
 * There is NO self-registration in this system.
 */
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ── Login ────────────────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {
        // 1. Find user by username or email
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new BadRequestException("Invalid username/email or password."));

        // 2. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid username/email or password.");
        }

        // 3. Generate JWT with systemRole
        SystemRole systemRole = user.getSystemRole() != null
                ? user.getSystemRole()
                : SystemRole.USER;
        String token = jwtUtil.generateToken(
                user.getId(), user.getUsername(), systemRole.name());

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }

}
