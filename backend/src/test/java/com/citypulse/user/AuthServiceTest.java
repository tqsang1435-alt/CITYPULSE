package com.citypulse.user;

import com.citypulse.common.exception.BusinessRuleException;
import com.citypulse.user.entity.AppUser;
import com.citypulse.user.repository.AppUserRepository;
import com.citypulse.user.repository.AuthSessionRepository;
import com.citypulse.user.service.AuthRequest;
import com.citypulse.user.service.AuthService;
import com.citypulse.user.service.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock AppUserRepository userRepository;
    @Mock AuthSessionRepository sessionRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks AuthService authService;

    @Test
    void register_normalizesEmailAndStoresPasswordHash() {
        given(userRepository.existsByEmailIgnoreCase("person@example.com")).willReturn(false);
        given(passwordEncoder.encode("correct-horse")).willReturn("bcrypt-hash");
        given(userRepository.saveAndFlush(any(AppUser.class))).willAnswer(invocation -> invocation.getArgument(0));

        var response = authService.register(
                new RegisterRequest(" Person@Example.com ", "correct-horse", "Person"));

        assertThat(response.email()).isEqualTo("person@example.com");
        assertThat(response.token()).isNotBlank();
        then(userRepository).should().saveAndFlush(org.mockito.ArgumentMatchers.argThat(
                user -> user.getEmail().equals("person@example.com")
                        && user.getPasswordHash().equals("bcrypt-hash")));
        then(sessionRepository).should().save(any());
    }

    @Test
    void registerRejectsExistingEmail() {
        given(userRepository.existsByEmailIgnoreCase("person@example.com")).willReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("person@example.com", "correct-horse", "Person")))
                .isInstanceOf(BusinessRuleException.class);

        then(userRepository).should().existsByEmailIgnoreCase("person@example.com");
        then(userRepository).should(never()).saveAndFlush(any());
        then(sessionRepository).shouldHaveNoInteractions();
    }

    @Test
    void loginVerifiesPasswordAndCreatesSession() {
        AppUser user = new AppUser("person@example.com", "Person");
        user.setPasswordHash("bcrypt-hash");
        given(userRepository.findByEmailIgnoreCase("person@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("correct-horse", "bcrypt-hash")).willReturn(true);

        var response = authService.login(new AuthRequest("PERSON@example.com", "correct-horse"));

        assertThat(response.email()).isEqualTo("person@example.com");
        assertThat(response.token()).isNotBlank();
        then(sessionRepository).should().save(any());
    }

    @Test
    void loginRejectsInvalidPassword() {
        AppUser user = new AppUser("person@example.com", "Person");
        user.setPasswordHash("bcrypt-hash");
        given(userRepository.findByEmailIgnoreCase("person@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong-password", "bcrypt-hash")).willReturn(false);

        assertThatThrownBy(() -> authService.login(new AuthRequest("person@example.com", "wrong-password")))
                .isInstanceOf(AuthService.InvalidCredentialsException.class);

        then(sessionRepository).shouldHaveNoInteractions();
    }
}
