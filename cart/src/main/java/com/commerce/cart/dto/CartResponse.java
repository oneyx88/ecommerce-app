package com.commerce.cart.dto;

import com.commerce.cart.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/10/29 15:58
 * @package com.commerce.order.dto
 * <p>
 * Description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Double totalPrice = 0.0;
    private List<CartItem> cartItems = new ArrayList<>();
}
