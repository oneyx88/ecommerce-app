package com.commerce.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/11/3 11:05
 * @package com.commerce.inventory.model
 * <p>
 * Description:
 */
@Entity
@Table(name = "inventory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    private Long productId;

    private Integer availableStock = 0;
    private Integer lockedStock = 0;
    private Integer soldStock = 0;

    private Integer version = 0; // 乐观锁字段

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
