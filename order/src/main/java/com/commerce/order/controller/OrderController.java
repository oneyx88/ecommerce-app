package com.commerce.order.controller;

import com.commerce.order.dto.OrderResponse;
import com.commerce.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:07
 * @package com.commerce.order.controller
 * <p>
 * Description:
 */
@RestController
@RequestMapping("/api/v1/orders")
class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestHeader("X-User-Id") String keycloakId,
                            @RequestHeader("X-User-Email") String userEmail) {
        OrderResponse orderResponse = orderService.createOrder(keycloakId, userEmail);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + orderResponse.getKeycloakId())).body(orderResponse);
    }
}
