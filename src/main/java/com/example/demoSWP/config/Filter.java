package com.example.demoSWP.config;

import com.example.demoSWP.entity.Account;
import com.example.demoSWP.enums.Role;
import com.example.demoSWP.exception.exceptions.AuthenticationException;
import com.example.demoSWP.service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;
    @Autowired
    TokenService tokenService;

    // CẬP NHẬT DANH SÁCH NÀY ĐỂ KHỚP VỚI SecurityConfig.java
    private final List<String> PUBLIC_API = List.of(
            "/api/login",
            "/api/register",
            "/api/registrations",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v2/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            // Thêm các API khác mà bạn đã định nghĩa là public trong SecurityConfig (nếu có)

            "/api/blogs/**",
            "/api/service/**",
            "/api/posts/**",
            "/uploads/**",
            "/api/slots/available-slots",
            "/api/slots/available-dates",
            "/api/forgot-pasword",
            "/api/doctors"

    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println("=== FILTER DEBUG ===");
        System.out.println("Authorization header: " + request.getHeader("Authorization"));
        System.out.println("Request URI: " + path + " Method: " + request.getMethod());
        System.out.println("All headers:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            System.out.println("  " + headerName + ": " + request.getHeader(headerName));
        });
        System.out.println("==================");

        boolean isPublicApi = PUBLIC_API.stream().anyMatch(api -> pathMatcher.match(api, path));

        if (isPublicApi) {
            System.out.println("DEBUG: Request matches public API, skipping filter logic.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getToken(request);
            if (token == null) {
                System.out.println("DEBUG: No token found for non-public API path: " + path);
                resolver.resolveException(request, response, null, new AuthenticationException("Token not found!"));
                return;
            }

            System.out.println("DEBUG: Token found: " + token.substring(0, Math.min(token.length(), 20)) + "..."); // Print partial token for debug

            Account account = null;
            try {
                account = tokenService.extractAccount(token);
                System.out.println("DEBUG: Account extracted from token. Email: " + (account != null ? account.getEmail() : "null"));
                System.out.println("DEBUG: Account role: " + (account != null && account.getRole() != null ? account.getRole().name() : "null"));
                System.out.println("DEBUG: Account authorities (from getAuthorities()): " + (account != null ? account.getAuthorities() : "null"));
            } catch (ExpiredJwtException e) {
                System.out.println("DEBUG: Token expired!");
                resolver.resolveException(request, response, null, new AuthException("Token expired!"));
                return;
            } catch (MalformedJwtException e) {
                System.out.println("DEBUG: MalformedJwtException caught: " + e.getMessage());
                resolver.resolveException(request, response, null, new AuthException("Invalid Token!"));
                return;
            } catch (Exception e) {
                System.out.println("DEBUG: Token parsing error: " + e.getMessage());
                resolver.resolveException(request, response, null, new AuthException("Token parsing failed!"));
                return;
            }

            if (account == null) {
                System.out.println("DEBUG: Account not found from token after extraction!");
                resolver.resolveException(request, response, null, new AuthenticationException("Account not found!"));
                return;
            }

            System.out.println("DEBUG: Before setting authentication in SecurityContextHolder.");

            UsernamePasswordAuthenticationToken authenToken =
                    new UsernamePasswordAuthenticationToken(account, token, account.getAuthorities());
            authenToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenToken);

            System.out.println("Authentication set successfully for: " + account.getEmail());
            System.out.println("Authorities set in SecurityContext: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities()); // Xác nhận authorities sau khi set

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("DEBUG: General exception in filter: " + e.getMessage());
            resolver.resolveException(request, response, null, e); // Để resolver xử lý các Exception khác
        }
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }
}