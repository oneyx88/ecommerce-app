package com.commerce.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/11/2 21:26
 * @package com.commerce.order.model
 * <p>
 * Description:
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    /** 外部 Product ID（从 product-service 获取） */
    @Column(nullable = false)
    private Long productId;

    /** 商品名称快照（下单时复制） */
    @Column(nullable = false)
    private String productName;

    private String image;

    /** 单价快照（下单时复制） */
    private double productPrice;

    /** 购买数量 */
    private Integer quantity;

    /** 折扣金额（如果有） */
    private double discount;

    /** 实际下单时价格（单价 * 数量 - 折扣） */
    private double orderedProductPrice;

    /** 与订单关系（本服务内可保留） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
