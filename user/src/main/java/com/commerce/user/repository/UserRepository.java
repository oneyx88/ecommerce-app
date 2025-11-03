package com.commerce.user.repository;

import com.commerce.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Yixi Wan
 * @date 2025/10/24 16:01
 * @package com.commerce.user.repository
 * <p>
 * Description:
 */
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(@NotBlank @Size(min = 3, max = 20) String username);

    boolean existsByEmail(@NotBlank @Size(max = 50) @Email String email);

    Optional<User> findByKeycloakId(String keycloakId);

    boolean existsByKeycloakIdAndEnabledTrueAndDeletedFalse(String keycloakId);
}
