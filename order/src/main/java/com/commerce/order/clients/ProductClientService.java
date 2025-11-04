package com.commerce.order.clients;

import com.commerce.order.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Yixi Wan
 * @date 2025/11/2 23:38
 * @package com.commerce.order.service
 * <p>
 * Description:
 */
@Service
public class ProductClientService {
    @Autowired
    private ProductFeignClient productFeignClient;

    public ProductDTO getProductById(Long productId) {
        return productFeignClient.getProductById(productId);
    }
}
