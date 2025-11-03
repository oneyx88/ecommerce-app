package com.commerce.product.repository;


import com.commerce.product.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Yixi Wan
 * @date 2025/10/20 23:28
 * @package com.commerce.ecommapp.repository
 * <p>
 * Description:
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByCategoryName(String categoryName);
}
