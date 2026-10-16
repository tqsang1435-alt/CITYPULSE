package com.citypulse.auth.service;

import com.citypulse.auth.dto.AuthResponse;
import com.citypulse.auth.dto.LoginRequest;
import com.citypulse.auth.dto.RegisterRequest;
import com.citypulse.common.exception.AccountLockedException;
import com.citypulse.common.exception.DuplicateEmailException;
import com.citypulse.common.exception.InvalidCredentialsException;
import com.citypulse.security.JwtService;
import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.User;
import com.citypulse.user.entity.UserStatus;
import com.citypulse.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    // ==========================================
    // REGISTER TESTS
    // ==========================================

    @Test
    @DisplayName("REGISTER: Successfully registers user, hashes password, defaults role to USER and status to ACTIVE")
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest(
                "Le Son",
                "  Son.Le@DaNangVista.VN  ",
                "mySecret123"
        );

        when(userRepository.existsByEmail("son.le@danangvista.vn")).thenReturn(false);
        when(passwordEncoder.encode("mySecret123")).thenReturn("$2a$10$hashedPasswordSecret");

        UUID generatedId = UUID.randomUUID();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(generatedId);
            return u;
        });

        when(jwtService.generateToken(any(User.class))).thenReturn("jwt_mock_token_12345");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt_mock_token_12345", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(86400000L, response.expiresInMs());
        assertNotNull(response.user());
        assertEquals("son.le@danangvista.vn", response.user().email());
        assertEquals("Le Son", response.user().displayName());
        assertEquals(Role.USER, response.user().role());
        assertEquals(UserStatus.ACTIVE, response.user().status());

        // Verify password was hashed and default role/status applied
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("$2a$10$hashedPasswordSecret", savedUser.getPasswordHash());
        assertEquals(Role.USER, savedUser.getRole(), "Default role must be USER");
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus(), "Default status must be ACTIVE");
    }

    @Test
    @DisplayName("REGISTER: Rejects duplicate email with DuplicateEmailException")
    void shouldThrowDuplicateEmailExceptionWhenEmailExists() {
        RegisterRequest request = new RegisterRequest("Existing User", "duplicate@vista.vn", "password123");

        when(userRepository.existsByEmail("duplicate@vista.vn")).thenReturn(true);

        DuplicateEmailException ex = assertThrows(DuplicateEmailException.class, () -> authService.register(request));
        assertTrue(ex.getMessage().contains("duplicate@vista.vn"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("REGISTER: Client cannot assign ADMIN role during registration")
    void shouldEnsureClientCannotElevatePrivilegesToAdmin() {
        // RegisterRequest record enforces only displayName, email, password fields.
        // Even if an attacker tries to inject role parameters, AuthService always assigns Role.USER.
        RegisterRequest request = new RegisterRequest("Attacker", "hacker@domain.com", "hackerPass123");

        when(userRepository.existsByEmail("hacker@domain.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertEquals(Role.USER, response.user().role(), "Role must be USER and not elevated");
    }

    // ==========================================
    // LOGIN TESTS
    // ==========================================

    @Test
    @DisplayName("LOGIN: Successfully authenticates active user with valid credentials")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("user@vista.vn", "validPassword");
        User existingUser = new User("user@vista.vn", "$2a$10$encodedHash", "Valid User", Role.USER, UserStatus.ACTIVE);
        existingUser.setId(UUID.randomUUID());

        when(userRepository.findByEmail("user@vista.vn")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("validPassword", "$2a$10$encodedHash")).thenReturn(true);
        when(jwtService.generateToken(existingUser)).thenReturn("login_jwt_token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("login_jwt_token", response.accessToken());
        assertEquals("user@vista.vn", response.user().email());
        assertEquals("Valid User", response.user().displayName());
    }

    @Test
    @DisplayName("LOGIN: Fails when email is not found with generic error message")
    void shouldFailWhenEmailNotFound() {
        LoginRequest request = new LoginRequest("unknown@vista.vn", "anyPassword");

        when(userRepository.findByEmail("unknown@vista.vn")).thenReturn(Optional.empty());

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        assertEquals("Email hoặc mật khẩu không chính xác.", ex.getMessage(),
                "Must return generic message without revealing if email exists");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("LOGIN: Fails when password does not match with generic error message")
    void shouldFailWhenPasswordIncorrect() {
        LoginRequest request = new LoginRequest("user@vista.vn", "wrongPassword");
        User existingUser = new User("user@vista.vn", "$2a$10$encodedHash", "User", Role.USER, UserStatus.ACTIVE);

        when(userRepository.findByEmail("user@vista.vn")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPassword", "$2a$10$encodedHash")).thenReturn(false);

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        assertEquals("Email hoặc mật khẩu không chính xác.", ex.getMessage(),
                "Must return generic message without revealing failure reason");
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("LOGIN: Blocks locked account with AccountLockedException")
    void shouldBlockLockedAccount() {
        LoginRequest request = new LoginRequest("locked@vista.vn", "correctPassword");
        User lockedUser = new User("locked@vista.vn", "$2a$10$encodedHash", "Locked User", Role.USER, UserStatus.LOCKED);

        when(userRepository.findByEmail("locked@vista.vn")).thenReturn(Optional.of(lockedUser));
        when(passwordEncoder.matches("correctPassword", "$2a$10$encodedHash")).thenReturn(true);

        AccountLockedException ex = assertThrows(AccountLockedException.class, () -> authService.login(request));
        assertTrue(ex.getMessage().contains("khóa"));
        verify(jwtService, never()).generateToken(any());
    }
}
