package com.qarisma.api.service;

import com.qarisma.api.dto.request.LoginRequest;
import com.qarisma.api.dto.request.RegisterRequest;
import com.qarisma.api.dto.response.ApiResponse;
import com.qarisma.api.dto.response.AuthResponse;
import com.qarisma.api.entity.User;
import com.qarisma.api.repository.UserRepository;
import com.qarisma.api.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Value("${security.cookie.name}")
    private String cookieName;

    @Value("${security.cookie.http-only}")
    private boolean httpOnly;

    public ApiResponse<String> register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error("اسم المستخدم موجود بالفعل");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.error("البريد الإلكتروني مسجل بالفعل");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
        return ApiResponse.success("تم إنشاء الحساب بنجاح", null);
    }

    public ApiResponse<AuthResponse> login(LoginRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            return ApiResponse.error("اسم المستخدم أو كلمة المرور غير صحيحة");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        // Set Cookie
        Cookie cookie = new Cookie(cookieName, jwt);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(false); // In prod set to true
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(jwt)
                .user(AuthResponse.UserDto.fromEntity(user))
                .build();

        return ApiResponse.success("تم تسجيل الدخول بنجاح", authResponse);
    }
}
