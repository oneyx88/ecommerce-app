package com.commerce.product.repository;

import com.commerce.product.model.Category;
import com.commerce.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Yixi Wan
 * @date 2025/10/22 12:12
 * @package com.commerce.ecommapp.repository
 * <p>
 * Description:
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(Category category, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
}
