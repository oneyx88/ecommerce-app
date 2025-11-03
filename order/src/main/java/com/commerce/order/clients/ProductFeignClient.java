package com.commerce.order.clients;

import com.commerce.order.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Yixi Wan
 * @date 2025/10/29 16:19
 * @package com.commerce.order.service
 * <p>
 * Description:
 */
@FeignClient(name = "product-service", url = "http://localhost:8082/api")
public interface ProductFeignClient {

    @GetMapping("/product/{productId}")
    ProductDTO getProductById(@PathVariable Long productId);
}
