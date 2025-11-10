package com.commerce.inventory.kafka.event;

/**
 * @author Yixi Wan
 * @date 2025/11/3 13:31
 * @package com.commerce.inventory.dto
 * <p>
 * Description:
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  InventoryEvent — 库存变动事件
 *  用于异步通知其他微服务（如 Product、Cache、Search）库存变化。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEvent implements Serializable {

    /** 商品ID */
    private Long productId;

    /** 可用库存 */
//    private Integer availableStock;

//    /** 锁定库存 */
//    private Integer lockedStock;
//
//    /** 已售库存 */
//    private Integer soldStock;
//
    /** 操作类型：LOCK / CONFIRM / RELEASE */
    private String eventType;
//
    /** 事件发生时间 */
    private LocalDateTime eventTime;
//
//    /** 版本号（可选，用于乐观锁一致性） */
//    private Integer version;
}
