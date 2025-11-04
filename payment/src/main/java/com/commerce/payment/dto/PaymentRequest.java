package com.commerce.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:08
 * @package com.commerce.payment.dto
 * <p>
 * Description:
 */
@Data
public class PaymentRequest {
    private Long orderId;
    private String keycloakId;
    private Double amount;
    private String currency;
}
