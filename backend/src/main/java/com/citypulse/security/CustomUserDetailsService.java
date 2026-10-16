package com.citypulse.security;

import com.citypulse.user.entity.User;
import com.citypulse.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // First try to parse as UUID (user ID from JWT subject)
        try {
            UUID userId = UUID.fromString(identifier);
            return userRepository.findById(userId)
                    .map(UserPrincipal::fromEntity)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + identifier));
        } catch (IllegalArgumentException notUuid) {
            // Otherwise find by normalized email
            return userRepository.findByNormalizedEmail(identifier)
                    .map(UserPrincipal::fromEntity)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + identifier));
        }
    }

    public UserDetails loadUserById(UUID userId) throws UsernameNotFoundException {
        return userRepository.findById(userId)
                .map(UserPrincipal::fromEntity)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }
}
