package com.commerce.payment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/11/2 21:41
 * @package com.commerce.payment.model
 * <p>
 * Description:
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    /**
     * 外部关联：Order Service 的订单ID（逻辑关联，不是外键）
     * 微服务间不共享数据库
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * 订单所属用户的 Keycloak ID
     * 方便审计与支付验证（防止伪造 orderId）
     */
    @Column(nullable = false)
    private String keycloakId;

    /**
     * 支付方式，如 "CREDIT_CARD", "PAYPAL", "STRIPE", "ALIPAY"
     */
    @Size(min = 2, message = "Payment method must contain at least 2 characters")
    private String paymentMethod;

    /**
     * 第三方支付网关返回的唯一交易号（例如 Stripe/PayPal Transaction ID）
     */
    private String pgPaymentId;

    /**
     * 网关返回状态（PENDING, SUCCESS, FAILED）
     */
    private String pgStatus;

    /**
     * 网关响应消息（失败原因、备注等）
     */
    private String pgResponseMessage;

    /**
     * 支付网关名称（Stripe, PayPal, Alipay, etc.）
     */
    private String pgName;

    /**
     * 本地支付状态（INITIATED, PROCESSING, SUCCESS, FAILED, REFUNDED）
     */
    private String paymentStatus;

    /**
     * 实际支付金额
     */
    private Double amount;

    /**
     * 订单货币类型（USD, EUR, CNY ...）
     */
    private String currency;

    /**
     * 支付发起时间与更新时间
     */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
