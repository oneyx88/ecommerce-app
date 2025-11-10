package com.commerce.order.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/11/3 17:28
 * @package com.commerce.order.kafka
 * <p>
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable {
    private Long orderId;
    private String keycloakId;
    private String email;
    private Double totalAmount;
    private String currency;

    /** 收货地址快照 */
    private String shippingName;
    private String shippingStreet;
    private String shippingCity;
    private String shippingState;
    private String shippingCountry;
    private String shippingZipCode;

    /** 订单明细快照（商品ID + 数量 + 单价） */
    private List<OrderItemPayload> orderItems;

    /** 时间字段 */
    private LocalDateTime createdAt;
    private LocalDateTime eventTime;
}
