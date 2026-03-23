package com.anotation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Bean {@link io.swagger.v3.oas.models.OpenAPI} cho Springdoc: mô tả API và bảo mật thử nghiệm trên Swagger UI.
 * Khai báo server mặc định {@code http://localhost:8080} (mô tả backend local); thêm {@link io.swagger.v3.oas.models.security.SecurityRequirement} global cho scheme {@code bearerAuth}.
 * Scheme HTTP Bearer, định dạng JWT; mô tả hướng dẫn dán token lấy từ {@code POST /api/auth/login}.
 * Giúp giao diện {@code /swagger-ui} gửi header {@code Authorization} đúng chuẩn khi gọi thử endpoint được bảo vệ.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("http://localhost:8080").description("Backend local")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Nhập JWT token (lấy từ POST /api/auth/login). Ví dụ: eyJhbGc...")));
    }
}
