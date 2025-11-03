package com.commerce.order.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class Cart implements Serializable {

    private String keycloakId;          // 用户唯一标识
    private List<CartItem> cartItems = new ArrayList<>();
    private Double totalPrice = 0.0;
}