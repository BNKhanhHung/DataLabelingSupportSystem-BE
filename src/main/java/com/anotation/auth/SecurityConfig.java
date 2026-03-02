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
 * - ADMIN & MANAGER : full access to all APIs.
 * - USER : only login, profile (users/me), labeling (annotations), review (review-feedbacks),
 *   and read own tasks (GET tasks by annotator/id) for workflow.
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
                                "/v3/api-docs/**")
                        .permitAll()

                        // ── USER: profile (đăng nhập xong xem thông tin mình) ─────
                        .requestMatchers("/api/users/me", "/api/users/me/**").authenticated()

                        // ── USER: đánh nhãn (annotations) ────────────────────────
                        .requestMatchers("/api/annotations", "/api/annotations/**").authenticated()

                        // ── USER: review (review-feedbacks) ─────────────────────
                        .requestMatchers("/api/review-feedbacks", "/api/review-feedbacks/**").authenticated()

                        // ── USER: xem task của mình (annotator/reviewer) ─────────
                        .requestMatchers(HttpMethod.GET, "/api/tasks/annotator/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tasks/reviewer/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tasks/*").authenticated()

                        // ── ADMIN & MANAGER only — quản lý users, roles, projects, tasks, ...
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/roles/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/projects/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/tasks/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/user-roles/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/labels/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/datasets/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/data-items/**").hasAnyRole("ADMIN", "MANAGER")

                        // ── Any other API (future) — ADMIN & MANAGER only ─────────
                        .anyRequest().hasAnyRole("ADMIN", "MANAGER"))
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:50337", "http://127.0.0.1:50337", "http://localhost:5173", "http://127.0.0.1:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
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
