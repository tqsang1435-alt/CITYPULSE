package com.citypulse.auth.controller;

import com.citypulse.auth.dto.AuthResponse;
import com.citypulse.auth.dto.LoginRequest;
import com.citypulse.auth.dto.RegisterRequest;
import com.citypulse.auth.dto.UserResponse;
import com.citypulse.auth.service.AuthService;
import com.citypulse.common.response.ApiMessage;
import com.citypulse.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/v1/auth/register
     * Public endpoint to register a new user account.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/login
     * Public endpoint to authenticate user and receive a JWT access token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/auth/me
     * Authenticated endpoint to retrieve the current user's profile.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserResponse response = authService.getCurrentUser(principal);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/logout
     * Authenticated endpoint to sign out.
     * With stateless JWT, client deletes token upon receiving 200 OK.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiMessage> logout() {
        return ResponseEntity.ok(new ApiMessage("Đăng xuất thành công. Vui lòng xóa access token ở client storage."));
    }
}
