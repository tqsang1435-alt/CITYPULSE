package com.citypulse.auth.service;

import com.citypulse.auth.dto.AuthResponse;
import com.citypulse.auth.dto.LoginRequest;
import com.citypulse.auth.dto.RegisterRequest;
import com.citypulse.auth.dto.UserResponse;
import com.citypulse.common.exception.AccountLockedException;
import com.citypulse.common.exception.DuplicateEmailException;
import com.citypulse.common.exception.InvalidCredentialsException;
import com.citypulse.security.JwtService;
import com.citypulse.security.UserPrincipal;
import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.User;
import com.citypulse.user.entity.UserStatus;
import com.citypulse.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user account.
     * Enforces trimmed/lowercased email uniqueness, BCrypt password hashing,
     * default USER role, default ACTIVE status.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = User.normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("Email " + normalizedEmail + " đã được sử dụng. Vui lòng chọn email khác.");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                normalizedEmail,
                passwordHash,
                request.displayName().trim(),
                Role.USER,
                UserStatus.ACTIVE
        );

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                jwtService.getExpirationMs(),
                UserResponse.fromEntity(savedUser)
        );
    }

    /**
     * Authenticates a user with email and password.
     * Prevents login if account is LOCKED.
     * Returns generic error message if email or password does not match to avoid account enumeration.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = User.normalizeEmail(request.email());

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Email hoặc mật khẩu không chính xác."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Email hoặc mật khẩu không chính xác.");
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            throw new AccountLockedException("Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ.");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                jwtService.getExpirationMs(),
                UserResponse.fromEntity(user)
        );
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal principal) {
        if (principal == null) {
            throw new InvalidCredentialsException("Yêu cầu xác thực tài khoản.");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy thông tin tài khoản: " + principal.getId()));

        return UserResponse.fromEntity(user);
    }
}
