package com.commerce.product.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Yixi Wan
 * @date 2025/11/4 20:36
 * @package com.commerce.product.kafka
 * <p>
 * Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCacheEvictEvent implements Serializable {
    private Long productId;
}
