package com.anotation.config;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Tiện ích một lần: in ra bcrypt hash của mật khẩu để copy vào cột password_hash trong DB.
 * Chỉ dùng khi cần đặt lại mật khẩu trực tiếp trong DB (dev/support).
 *
 * Chạy: mvn exec:java -Dexec.mainClass="com.anotation.config.PasswordHashUtil" -Dexec.args="matKhauMoi"
 * Hoặc chạy main() từ IDE, sửa PASSWORD bên dưới.
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
