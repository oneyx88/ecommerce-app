package com.commerce.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/11/2 21:25
 * @package com.commerce.order.model
 * <p>
 * Description:
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    /** 用户标识 — 与 Keycloak 统一 */
    @Column(nullable = false)
    private String keycloakId;

    /** 用户邮箱快照（下单时复制） */
    @Column(nullable = false)
    private String email;

    /** 总金额（下单时计算并冻结） */
    private Double totalAmount;

    /** 订单状态：CREATED / PAID / SHIPPED / DELIVERED / CANCELLED */
    private String orderStatus;

    /** 收货地址信息（来自 Address Service 的快照） */
    private Long addressId;   // 用于追溯外部 ID
    private String shippingName;
    private String shippingStreet;
    private String shippingCity;
    private String shippingState;
    private String shippingCountry;
    private String shippingZipCode;

    /** 与订单项的内部关系（同服务内可保留） */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    /** 时间字段 */
    @Column(updatable = false)
    private LocalDateTime createdAt;  // 订单创建时间（系统生成）
    private LocalDateTime paidAt;     // 支付成功时间
    private LocalDateTime shippedAt;  // 发货时间
    private LocalDateTime deliveredAt;// 签收时间
}
