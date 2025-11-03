package com.commerce.address.repository;

import com.commerce.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/11/2 17:18
 * @package com.commerce.address.repository
 * <p>
 * Description:
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByKeycloakIdAndDeletedFalse(String keycloakId);
}
