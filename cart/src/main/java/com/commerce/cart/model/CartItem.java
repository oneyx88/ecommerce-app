package com.commerce.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Yixi Wan
 * @date 2025/10/29 15:54
 * @package com.commerce.order.model
 * <p>
 * Description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem implements Serializable {

    private Long productId;
    private String productName;
    private Integer quantity;
    private double discount;
    private double productPrice;
    private String image;
    private Integer productQuantity;

}
