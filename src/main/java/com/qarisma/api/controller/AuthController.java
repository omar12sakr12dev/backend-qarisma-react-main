package com.qarisma.api.controller;

import com.qarisma.api.dto.request.LoginRequest;
import com.qarisma.api.dto.request.RegisterRequest;
import com.qarisma.api.dto.response.ApiResponse;
import com.qarisma.api.dto.response.AuthResponse;
import com.qarisma.api.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("API is running", null);
    }
}
