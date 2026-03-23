package com.anotation.auth;

import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Triển khai {@link org.springframework.security.core.userdetails.UserDetailsService} cho Spring Security (form/login nội bộ hoặc luồng dùng UserDetails).
 * {@code loadUserByUsername} nhận username hoặc email, tra {@link com.anotation.user.UserRepository}; nếu không có thì ném {@link org.springframework.security.core.userdetails.UsernameNotFoundException}.
 * Mật khẩu trả về là {@code passwordHash} đã bcrypt trong DB; authority dạng {@code ROLE_<SystemRole>} (mặc định ROLE_USER nếu null).
 * Phục vụ tích hợp chuẩn Security; song song, API stateless chủ yếu dùng JWT qua {@link JwtAuthenticationFilter}.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + usernameOrEmail));

        // Map system role to Spring Security authority
        String authority = "ROLE_" + (user.getSystemRole() != null
                ? user.getSystemRole().name()
                : "USER");

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(authority)));
    }
}
