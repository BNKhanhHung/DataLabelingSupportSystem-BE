package com.anotation.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security Configuration — Authorization Rules
 *
 * - USER (authenticated): GET trên các API (projects, tasks, labels, datasets, data-items, roles, user-roles, users);
 *   đổi mật khẩu (PATCH /api/users/me/password). Không được chỉnh sửa role, user-role, user (trừ đổi mật khẩu).
 * - ADMIN & MANAGER: full access (GET + POST/PUT/PATCH/DELETE) trên quản lý users, roles, user-roles, projects, tasks, labels, datasets, data-items.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── Public ──────────────────────────────────────────────
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/swagger",
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/api/uploads/**")
                        .permitAll()

                        // ── USER: profile + chỉ đổi mật khẩu (không chỉnh sửa user khác) ─
                        .requestMatchers("/api/users/me", "/api/users/me/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/users/me/password").authenticated()

                        // ── USER: GET trên mọi API (đọc only); không cho POST/PUT/PATCH/DELETE ở các rule sau ─
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tasks/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/labels/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/datasets/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/data-items/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/roles/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/user-roles/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated()

                        // ── Thông báo: mọi user đăng nhập đều xem và đánh dấu đọc ─
                        .requestMatchers("/api/notifications", "/api/notifications/**").authenticated()

                        // ── USER: đánh nhãn, review ─────────────────────────────
                        .requestMatchers("/api/annotations", "/api/annotations/**").authenticated()
                        .requestMatchers("/api/review-feedbacks", "/api/review-feedbacks/**").authenticated()

                        // ── USER (Annotator): nộp task để review; USER (Reviewer): hoàn tất review ─
                        .requestMatchers(HttpMethod.PATCH, "/api/tasks/*/submit").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/tasks/*/complete-review").authenticated()

                        // ── Chỉnh sửa (POST/PUT/PATCH/DELETE): chỉ ADMIN & MANAGER; không cho USER sửa role, user-role, user (trừ đổi mật khẩu đã cho ở trên) ─
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/roles/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/user-roles/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/projects/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/tasks/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/labels/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/datasets/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/data-items/**").hasAnyRole("ADMIN", "MANAGER")

                        // ── Còn lại (API tương lai) — ADMIN & MANAGER ─────────
                        .anyRequest().hasAnyRole("ADMIN", "MANAGER"))
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Cho phép frontend chạy từ nhiều origin (serve, Live Server, Vite, Cursor, ...)
        config.setAllowedOrigins(List.of(
                "http://localhost:3000", "http://127.0.0.1:3000",
                "http://localhost:5173", "http://127.0.0.1:5173",
                "http://localhost:5500", "http://127.0.0.1:5500",
                "http://localhost:5000", "http://127.0.0.1:5000",
                "http://localhost:8081", "http://127.0.0.1:8081",
                "http://localhost:4173", "http://127.0.0.1:4173",
                "http://localhost:50337", "http://127.0.0.1:50337"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
