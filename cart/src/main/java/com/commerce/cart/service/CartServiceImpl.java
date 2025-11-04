package com.commerce.cart.service;

import com.commerce.cart.clients.ProductFeignClient;
import com.commerce.cart.dto.CartResponse;
import com.commerce.cart.dto.ProductDTO;
import com.commerce.cart.exceptions.ApiException;
import com.commerce.cart.model.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Yixi Wan
 * @date 2025/10/29 16:14
 * @package com.commerce.order.service
 * <p>
 * Description:
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserClientService userClientService;


    private String buildCartKey(String keycloakId) {
        // 检查user是否存在
        Boolean isUserActive = userClientService.isUserActive(keycloakId);
        if (!isUserActive) {
            throw new ApiException("User is not active or does not exist", HttpStatus.UNAUTHORIZED);
        }
        return "cart:" + keycloakId;
    }

    @Override
    public String addProductToCart(String keycloakId, Long productId, Integer quantity) {
        String cartKey = buildCartKey(keycloakId);

        // 获取商品信息并校验
        ProductDTO product = productFeignClient.getProductById(productId);
        log.info("Product: {}", product);
        validateProductAvailability(product, quantity);

        // 检查购物车中是否已存在该商品
        boolean alreadyExists = redisTemplate.opsForHash().hasKey(cartKey, productId.toString());
        if (alreadyExists) {
            throw new ApiException(
                    "Product " + product.getProductName() + " already exists in cart",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 创建购物车项
        CartItem newItem = CartItem.builder()
                .productId(productId)
                .quantity(quantity)
                .discount(product.getDiscount())
                .productPrice(product.getSpecialPrice())
                .image(product.getImage())
                .productName(product.getProductName())
                .productQuantity(product.getAvailableStock())
                .build();

        // 保存进 Redis
        redisTemplate.opsForHash().put(cartKey, productId.toString(), newItem);

        // 返回提示字符串
        return "Added product '" + product.getProductName() + "' x" + quantity + " to cart successfully.";
    }

    @Override
    public CartResponse getCartByKeycloakId(String keycloakId) {
        String cartKey = buildCartKey(keycloakId);

        // 从 Redis 获取购物车
        Map<Object, Object> cartMap = redisTemplate.opsForHash().entries(cartKey);

        // 如果购物车为空 -> 抛出异常
        if (cartMap.isEmpty()) {
            throw new ApiException("Cart is empty. Please add products before viewing or checking out.",
                    HttpStatus.BAD_REQUEST);
        }

        // 计算总价并封装返回
        double total = 0.0;
        List<CartItem> cartItems = new ArrayList<>();

        for (Object obj : cartMap.values()) {
            CartItem item = (CartItem) obj;
            total += item.getProductPrice() * item.getQuantity();
            cartItems.add(item);
        }

        // 返回结果
        return new CartResponse(total, cartItems);
    }

    @Override
    public CartItem updateProductQuantityInCart(String keycloakId, Long productId, int operation) {
        String cartKey = buildCartKey(keycloakId);

        // 从 Redis 获取指定商品
        CartItem item = (CartItem) redisTemplate.opsForHash().get(cartKey, productId.toString());
        if (item == null) {
            throw new ApiException("Product not found in cart", HttpStatus.NOT_FOUND);
        }

        // 更新数量逻辑
        // operation > 0 表示增加，operation < 0 表示减少
        int newQuantity = item.getQuantity() + operation;

        if (newQuantity <= 0) {
            // 数量减为 0，自动删除该商品
            redisTemplate.opsForHash().delete(cartKey, productId.toString());
            throw new ApiException("Product removed from cart because quantity reached zero.", HttpStatus.OK);
        }

        // 3校验库存限制（productQuantity 来源于redis存储）
        if (newQuantity > item.getProductQuantity()) {
            throw new ApiException("Insufficient stock for " + item.getProductName() + ". Available: " + item.getProductQuantity(),
                    HttpStatus.BAD_REQUEST);
        }

        // 更新 Redis 中的数量
        item.setQuantity(newQuantity);
        redisTemplate.opsForHash().put(cartKey, productId.toString(), item);

        // 返回更新后的商品项
        return item;
    }


    @Override
    public void deleteProductFromCart(String keycloakId, Long productId) {
        String cartKey = buildCartKey(keycloakId);

        // 检查是否存在该商品
        boolean exists = redisTemplate.opsForHash().hasKey(cartKey, productId.toString());
        if (!exists) {
            throw new ApiException("Product not found in cart", HttpStatus.NOT_FOUND);
        }

        // 删除该商品
        redisTemplate.opsForHash().delete(cartKey, productId.toString());
    }



    /** 校验库存和数量 */
    private void validateProductAvailability(ProductDTO product, Integer quantity) {
        if (product.getAvailableStock() == 0) {
            throw new ApiException(product.getProductName() + " is not available", HttpStatus.BAD_REQUEST);
        }
        if (product.getAvailableStock() < quantity) {
            throw new ApiException("Please order " + product.getProductName() +
                    " ≤ " + product.getAvailableStock(), HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public void clearCart(String keycloakId) {
        String cartKey = buildCartKey(keycloakId);

        // 检查购物车是否存在或为空
        Map<Object, Object> cartMap = redisTemplate.opsForHash().entries(cartKey);
        if (cartMap.isEmpty()) {
            throw new ApiException("Cart is already empty.", HttpStatus.BAD_REQUEST);
        }

        // 清空购物车
        redisTemplate.delete(cartKey);
    }



}
