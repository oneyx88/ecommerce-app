package com.commerce.product.kafka.producer;

import com.commerce.product.kafka.event.ProductCacheEvictEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Yixi Wan
 * @date 2025/11/4 20:40
 * @package com.commerce.product.kafka.producer
 * <p>
 * Description:
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCacheEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "product-cache-evict-topic";

    public void sendCacheEvictEvent(Long productId) {
        ProductCacheEvictEvent event = new ProductCacheEvictEvent(productId);
        kafkaTemplate.send(TOPIC, productId.toString(), event);
        log.info("📤 Sent ProductCacheEvictEvent for productId={}", productId);
    }
}
