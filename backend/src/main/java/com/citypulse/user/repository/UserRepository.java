package com.citypulse.user.repository;

import com.citypulse.user.entity.Role;
import com.citypulse.user.entity.User;
import com.citypulse.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    List<User> findAllByRole(Role role);

    List<User> findAllByStatus(UserStatus status);

    long countByStatus(UserStatus status);

    /**
     * Finds a user after trimming and lowercasing the provided email.
     */
    default Optional<User> findByNormalizedEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return findByEmail(User.normalizeEmail(email));
    }

    /**
     * Checks if a user exists after trimming and lowercasing the provided email.
     */
    default boolean existsByNormalizedEmail(String email) {
        if (email == null) {
            return false;
        }
        return existsByEmail(User.normalizeEmail(email));
    }
}
