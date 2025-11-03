package com.commerce.user.repository;

import com.commerce.user.model.AppRole;
import com.commerce.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Yixi Wan
 * @date 2025/10/28 22:10
 * @package com.commerce.user.repository
 * <p>
 * Description:
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(AppRole role);
}
