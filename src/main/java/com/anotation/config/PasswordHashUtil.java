package com.anotation.config;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Lớp tiện ích độc lập (không phải bean Spring) để sinh chuỗi bcrypt tương thích {@link org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder} trong ứng dụng.
 * Dùng khi cần ghi trực tiếp cột {@code password_hash} trong DB (khôi phục tài khoản, seed SQL thủ công, hỗ trợ vận hành) mà không qua API đổi mật khẩu.
 * {@code main}: đọc mật khẩu từ tham số dòng lệnh hoặc mặc định {@code admin123}; in ra mật khẩu và hash với cost {@code gensalt(10)}.
 * Chạy Maven: {@code mvn exec:java -Dexec.mainClass="com.anotation.config.PasswordHashUtil" -Dexec.args="matKhauMoi"}; hoặc chạy từ IDE.
 * Không triển khai lên môi trường production như dịch vụ công khai; chỉ công cụ cục bộ an toàn.
 */
public class PasswordHashUtil {

    public static void main(String[] args) {
        String password = args.length > 0 ? args[0] : "admin123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println("Password: " + password);
        System.out.println("BCrypt hash (paste into password_hash column):");
        System.out.println(hash);
    }
}
