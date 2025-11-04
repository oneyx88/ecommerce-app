package com.commerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:11
 * @package com.commerce.order.dto
 * <p>
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long orderId;

    /** 用户与金额信息 */
    private String keycloakId;
    private String email;
    private Double totalAmount;
    private String orderStatus; // CREATED, PAID, SHIPPED, DELIVERED

    /** 支付与物流关联 */
    private Long paymentId;
    private Long shipmentId;

    /** 收货地址快照 */
    private String shippingName;
    private String shippingStreet;
    private String shippingCity;
    private String shippingState;
    private String shippingCountry;
    private String shippingZipCode;

    /** 时间字段 */
    private LocalDateTime createdAt;      // 创建时间
    private LocalDateTime paidAt;         // 支付时间
    private LocalDateTime shippedAt;      // 发货时间
    private LocalDateTime deliveredAt;    // 签收时间
    private LocalDateTime updatedAt;      // 更新时间

    /** 子项明细 */
    private List<OrderItemResponse> orderItems;
}
