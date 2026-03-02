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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security Configuration — Authorization Rules
 *
 * - ADMIN & MANAGER : full access (users, roles, projects, tasks, data items, etc.)
 * - USER : authenticated; project-level permissions via UserRole (Annotator/Reviewer)
 *   enforced in Service layer (AnnotationServiceImpl, ReviewFeedbackServiceImpl, etc.)
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

                        // ── Public endpoints ─────────────────────────────────────
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/swagger",
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/v3/api-docs/**")
                        .permitAll()

                        // ── Current user endpoints — authenticated ──────────────
                        .requestMatchers("/api/users/me/**").authenticated()

                        // ── ADMIN & MANAGER — quản lý users và toàn bộ APIs ──────
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "MANAGER")
                        // ── Roles, projects, tasks, etc. — authenticated ──────────
                        .requestMatchers("/api/roles/**").authenticated()

                        // ── All other endpoints — authenticated users ────────────
                        // Project-level authorization (Manager/Annotator/Reviewer)
                        // is enforced in Service layer, NOT here.
                        .anyRequest().authenticated())
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
