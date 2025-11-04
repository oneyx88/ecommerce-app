package com.commerce.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/10/22 12:08
 * @package com.commerce.ecommapp.model
 * <p>
 * Description:
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String productName;
    @Size(min = 6, max = 255, message = "Description must be between 6 and 255 characters")
    private String description;
    private String image;
    private Double price;
    private Double discount;
    private Double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Long sellerId;
}
