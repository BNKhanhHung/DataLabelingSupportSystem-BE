package com.anotation.config;

import com.anotation.user.SystemRole;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Tự động tạo tài khoản Admin mặc định khi chạy lần đầu.
 * Nếu đã có admin rồi thì bỏ qua.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Nếu chưa có user admin nào → tạo mặc định
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@system.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setStatus("ACTIVE");
            admin.setSystemRole(SystemRole.ADMIN);

            userRepository.save(admin);
            log.info("=== Da tao tai khoan Admin mac dinh ===");
            log.info("    Username: admin");
            log.info("    Password: admin123");
            log.info("    Role: ADMIN");
            log.info("========================================");
        } else {
            log.info("Admin da ton tai, bo qua tao mac dinh.");
        }
    }
}
