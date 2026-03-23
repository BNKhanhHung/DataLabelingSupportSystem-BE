package com.anotation.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Bộ lọc Spring Security ({@code OncePerRequestFilter}) chạy trước chuỗi xử lý, gắn principal từ JWT cho mỗi request có header hợp lệ.
 * Đọc header {@code Authorization: Bearer <token>}, xác thực bằng {@link JwtUtil#isTokenValid}; lấy username và claim role để tạo {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}.
 * Authority trong ngữ cảnh: {@code ROLE_} + role trong token (mặc định USER) để khớp {@link SecurityConfig#securityFilterChain}.
 * Không thiết lập credentials trong token (password null); chi tiết request gắn qua {@link org.springframework.security.web.authentication.WebAuthenticationDetailsSource}.
 * Nếu không có token hoặc token sai, filter vẫn chuyển tiếp chuỗi để các rule {@code permitAll} hoặc anonymous xử lý.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                // Map system role → Spring Security authority
                // ADMIN → ROLE_ADMIN, USER → ROLE_USER
                String authority = "ROLE_" + (role != null ? role : "USER");

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority(authority)));
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
