package com.commerce.order.service;

import com.commerce.order.dto.OrderResponse;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:07
 * @package com.commerce.order.service
 * <p>
 * Description:
 */
public interface OrderService {
    OrderResponse createOrder(String keycloakId, String userEmail);

}
