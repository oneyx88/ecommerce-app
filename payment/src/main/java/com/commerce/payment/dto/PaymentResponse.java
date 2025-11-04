package com.commerce.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:10
 * @package com.commerce.payment.dto
 * <p>
 * Description:
 */
@Data
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private String keycloakId;
    private String paymentMethod;
    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
    private String paymentStatus;
    private Double amount;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
