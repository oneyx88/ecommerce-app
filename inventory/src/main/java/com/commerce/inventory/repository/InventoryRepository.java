package com.commerce.inventory.repository;

import com.commerce.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author Yixi Wan
 * @date 2025/11/3 11:10
 * @package com.commerce.inventory.repository
 * <p>
 * Description:
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    // 下单时锁定库存 (available -> locked)
    @Modifying
    @Query("UPDATE Inventory i SET i.availableStock = i.availableStock - :qty, " +
            "i.lockedStock = i.lockedStock + :qty, i.version = i.version + 1 " +
            "WHERE i.productId = :pid AND i.availableStock >= :qty AND i.version = :version")
    int lockStock(@Param("pid") Long productId,
                  @Param("qty") int quantity,
                  @Param("version") int version);

    // 支付成功 -> 确认库存 (locked -> sold)
    @Modifying
    @Query("UPDATE Inventory i SET i.lockedStock = i.lockedStock - :qty, " +
            "i.soldStock = i.soldStock + :qty WHERE i.productId = :pid")
    int confirmStock(@Param("pid") Long productId, @Param("qty") int quantity);

    // 超时/支付失败 -> 释放库存 (locked -> available)
    @Modifying
    @Query("UPDATE Inventory i SET i.lockedStock = i.lockedStock - :qty, " +
            "i.availableStock = i.availableStock + :qty WHERE i.productId = :pid")
    int releaseStock(@Param("pid") Long productId, @Param("qty") int quantity);
}
