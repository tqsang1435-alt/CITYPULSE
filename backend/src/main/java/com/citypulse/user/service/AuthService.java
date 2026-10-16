package com.citypulse.user.service;

import com.citypulse.common.exception.BusinessRuleException;
import com.citypulse.user.entity.AppUser;
import com.citypulse.user.entity.AuthSession;
import com.citypulse.user.repository.AppUserRepository;
import com.citypulse.user.repository.AuthSessionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class AuthService {
    private static final int TOKEN_BYTES = 32;
    private static final int SESSION_DAYS = 30;

    private final AppUserRepository userRepository;
    private final AuthSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            AppUserRepository userRepository,
            AuthSessionRepository sessionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException("Email này đã được đăng ký.");
        }
        AppUser user = new AppUser(email, request.displayName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        try {
            user = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("Email này đã được đăng ký.");
        }
        return createSession(user);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        AppUser user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(() -> new InvalidCredentialsException());
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return createSession(user);
    }

    @Transactional(readOnly = true)
    public CurrentUser authenticate(String rawToken) {
        return sessionRepository.findByTokenHashAndExpiresAtAfter(hashToken(rawToken), Instant.now())
                .map(session -> new CurrentUser(
                        session.getUser().getId(),
                        session.getUser().getEmail(),
                        session.getUser().getDisplayName()))
                .orElse(null);
    }

    @Transactional
    public void logout(String rawToken) {
        sessionRepository.deleteByTokenHash(hashToken(rawToken));
    }

    private AuthResponse createSession(AppUser user) {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessionRepository.save(new AuthSession(
                user, hashToken(rawToken), Instant.now().plus(SESSION_DAYS, ChronoUnit.DAYS)));
        return new AuthResponse(rawToken, user.getId(), user.getEmail(), user.getDisplayName());
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }

    private static String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Email hoặc mật khẩu không chính xác.");
        }
    }
}
