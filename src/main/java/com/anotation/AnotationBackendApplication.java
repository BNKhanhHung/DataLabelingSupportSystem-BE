package com.anotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Điểm vào (main) của ứng dụng Spring Boot backend hỗ trợ gán nhãn dữ liệu (package {@code com.anotation}).
 * Bật quét component Spring, tự động cấu hình web, JPA, bảo mật và các module trong classpath.
 * {@code @EnableScheduling}: cho phép các tác vụ theo lịch (nếu có bean {@code @Scheduled}) chạy nền.
 * Chạy lớp này khởi động embedded server (mặc định thường là cổng 8080) và nạp các controller như {@code /api/auth}, {@code /api/annotations}, v.v.
 * Tên lớp giữ đúng chính tả dự án ({@code Anotation}) để khớp artifact hiện có.
 */
@SpringBootApplication
@EnableScheduling
public class AnotationBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnotationBackendApplication.class, args);
    }
}
