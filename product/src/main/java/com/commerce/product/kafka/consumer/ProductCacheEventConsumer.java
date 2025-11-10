package com.commerce.product.kafka.consumer;

import com.commerce.product.kafka.event.ProductCacheEvictEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Yixi Wan
 * @date 2025/11/4 20:37
 * @package com.commerce.product.kafka
 * <p>
 * Description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCacheEventConsumer {

    private final RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(topics = "product-cache-evict-topic", groupId = "product-service-group")
    public void consume(ProductCacheEvictEvent event) {
        Long productId = event.getProductId();
        String productKey = "product_cache:" + productId;
        String summaryKey = "product_summary:" + productId;

        redisTemplate.delete(productKey);
        redisTemplate.delete(summaryKey);

        log.info("[KafkaConsumer] Cleared Redis cache for productId={}", productId);
    }
}