package com.anotation.auth;

import com.anotation.exception.BadRequestException;
import com.anotation.user.SystemRole;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ xác thực đăng nhập: tìm user theo username hoặc email, kiểm tra mật khẩu bằng {@link org.springframework.security.crypto.password.PasswordEncoder}.
 * Phát hành JWT chứa id user, username và tên vai ({@link com.anotation.user.SystemRole}) qua {@link JwtUtil#generateToken}; trả về {@link AuthResponse}.
 * Giao dịch chỉ đọc ({@code @Transactional(readOnly = true)}) vì không ghi DB trong luồng login.
 * Tạo tài khoản hàng loạt hoặc có role đặc biệt do Admin/Manager thực hiện qua {@code /api/users}; đăng ký công khai USER nằm ở {@link AuthController#register}.
 * Lỗi đăng nhập thống nhất dưới dạng {@link com.anotation.exception.BadRequestException} để không lộ chi tiết user tồn tại hay không.
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

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), systemRole.name());
    }

}
