package com.commerce.order.repository;

import com.commerce.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:08
 * @package com.commerce.order.repository
 * <p>
 * Description:
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
