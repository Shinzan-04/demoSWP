package com.example.demoSWP.config;


import com.example.demoSWP.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays; // Cần thiết để sử dụng Arrays.asList
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize
public class SecurityConfig {

    @Autowired
    Filter filter; // Đảm bảo đây là Filter tùy chỉnh của bạn

    @Autowired
    AuthenticationService authenticationService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Vô hiệu hóa CSRF cho API stateless
                // Cấu hình CORS để sử dụng CorsConfigurationSource đã định nghĩa
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers("/api/login",
                                        "/api/register",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/v2/api-docs/**",
                                        "/swagger-resources/**",
                                        "/webjars/**",
                                        "/api/doctors",
                                        "/api/medical-histories/customer/**"

                                        ).permitAll() // Cho phép các endpoint này công khai
                                // RẤT QUAN TRỌNG: Cho phép các yêu cầu OPTIONS cho tất cả các đường dẫn.
                                // Các yêu cầu OPTIONS là preflight request của CORS và cần được cho phép trước khi xác thực.
                             //   .requestMatchers("/api/doctors/**").authenticated() // ✅ yêu cầu token cho /api/doctors/{id}, PUT, etc.
                                .requestMatchers("/uploads/**").permitAll() // ✅ không cần đăng nhập
                                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // THÊM DÒNG NÀY
                                .anyRequest().authenticated() // Tất cả các endpoint khác đều yêu cầu xác thực
                )
                .userDetailsService(authenticationService) // Cấu hình UserDetailsService
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sử dụng phiên không trạng thái cho JWT
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class) // Thêm bộ lọc JWT tùy chỉnh
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Cho phép gửi cookie/header xác thực
        // Cần thiết lập AllowedOrigins, nếu frontend của bạn chạy trên nhiều cổng hoặc domain, hãy thêm tất cả vào đây
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000")); // THAY ĐỔI THEO FRONTEND CỦA BẠN
        // Đảm bảo tất cả các header mà frontend gửi (bao gồm Authorization và Content-Type) đều được cho phép
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept")); // Thêm Accept vào đây
        // Đảm bảo tất cả các phương thức HTTP cần thiết được cho phép, ĐẶC BIỆT LÀ PATCH và OPTIONS
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // ĐẢM BẢO PATCH VÀ OPTIONS ĐƯỢC CHO PHÉP
        // Các header được phép phơi bày ra bên ngoài trình duyệt (nếu có header tùy chỉnh bạn muốn frontend đọc)
        config.setExposedHeaders(List.of("Authorization"));
        // Thời gian tồn tại của kết quả preflight request trong cache của trình duyệt
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Áp dụng cấu hình CORS cho tất cả các đường dẫn
        return source;
    }
}
