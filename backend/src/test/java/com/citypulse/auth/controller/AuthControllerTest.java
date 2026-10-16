package com.citypulse.auth.controller;

import com.citypulse.auth.dto.AuthResponse;
import com.citypulse.auth.dto.LoginRequest;
import com.citypulse.auth.dto.RegisterRequest;
import com.citypulse.auth.dto.UserResponse;
import com.citypulse.auth.service.AuthService;
import com.citypulse.common.exception.AccountLockedException;
import com.citypulse.common.exception.DuplicateEmailException;
import com.citypulse.common.exception.GlobalExceptionHandler;
import com.citypulse.common.exception.InvalidCredentialsException;
import com.citypulse.security.UserPrincipal;
import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserPrincipal mockPrincipal;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        mockPrincipal = new UserPrincipal(
                userId,
                "authenticated@vista.vn",
                "$2a$10$hash",
                "Auth User",
                Role.USER,
                UserStatus.ACTIVE
        );

        // Custom resolver to simulate @AuthenticationPrincipal in standalone MockMvc
        HandlerMethodArgumentResolver authPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                String authHeader = webRequest.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer valid_token")) {
                    return mockPrincipal;
                }
                return null;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(authPrincipalResolver)
                .build();
    }

    // ==========================================
    // REGISTER API TESTS
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/auth/register: Returns 201 Created with token and safe user info")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("Le Son", "son.le@vista.vn", "myPassword123");
        UserResponse userResponse = new UserResponse(
                UUID.randomUUID(),
                "son.le@vista.vn",
                "Le Son",
                null,
                Role.USER,
                UserStatus.ACTIVE,
                Instant.now()
        );
        AuthResponse authResponse = new AuthResponse("mock_token_abc", 86400000L, userResponse);

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("mock_token_abc"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("son.le@vista.vn"))
                .andExpect(jsonPath("$.user.displayName").value("Le Son"))
                .andExpect(jsonPath("$.user.role").value("USER"))
                .andExpect(jsonPath("$.user.status").value("ACTIVE"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register: Returns 400 Bad Request on invalid input format")
    void shouldReturn400OnValidationFailure() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest("", "invalid-email-format", "123"); // password < 6 chars

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.displayName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register: Returns 409 Conflict when email already registered")
    void shouldReturn409OnDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest("Existing", "dup@vista.vn", "password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateEmailException("Email dup@vista.vn đã được sử dụng."));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(containsString("đã được sử dụng")));
    }

    // ==========================================
    // LOGIN API TESTS
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/auth/login: Returns 200 OK with token on valid credentials")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("user@vista.vn", "password123");
        UserResponse userResponse = new UserResponse(
                UUID.randomUUID(),
                "user@vista.vn",
                "User Name",
                null,
                Role.USER,
                UserStatus.ACTIVE,
                Instant.now()
        );
        AuthResponse authResponse = new AuthResponse("access_jwt_valid", 86400000L, userResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_jwt_valid"))
                .andExpect(jsonPath("$.user.email").value("user@vista.vn"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login: Returns 401 Unauthorized with generic message on wrong credentials")
    void shouldReturn401OnInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("user@vista.vn", "wrongPassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Email hoặc mật khẩu không chính xác."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Email hoặc mật khẩu không chính xác."));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login: Returns 403 Forbidden when account is LOCKED")
    void shouldReturn403OnLockedAccount() throws Exception {
        LoginRequest request = new LoginRequest("locked@vista.vn", "validPassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new AccountLockedException("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value(containsString("khóa")));
    }

    // ==========================================
    // GET /api/v1/auth/me TESTS
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/auth/me: Returns 401 Unauthorized when token is missing")
    void shouldReturn401WhenNoTokenProvided() throws Exception {
        when(authService.getCurrentUser(null))
                .thenThrow(new InvalidCredentialsException("Yêu cầu xác thực tài khoản."));

        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me: Returns 200 OK with safe user details when token is valid")
    void shouldReturn200WithSafeUserDetails() throws Exception {
        UserResponse response = new UserResponse(
                mockPrincipal.getId(),
                "authenticated@vista.vn",
                "Auth User",
                null,
                Role.USER,
                UserStatus.ACTIVE,
                Instant.now()
        );

        when(authService.getCurrentUser(any(UserPrincipal.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer valid_token_12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockPrincipal.getId().toString()))
                .andExpect(jsonPath("$.email").value("authenticated@vista.vn"))
                .andExpect(jsonPath("$.displayName").value("Auth User"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    // ==========================================
    // LOGOUT API TESTS
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/auth/logout: Returns 200 OK with success message")
    void shouldLogoutSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("Đăng xuất thành công")));
    }
}
