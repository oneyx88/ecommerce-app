package com.commerce.product.service.category;


import com.commerce.product.dto.category.CategoryRequest;
import com.commerce.product.dto.category.CategoryResponse;
import com.commerce.product.dto.category.PagedCategoryResponse;
import com.commerce.product.exceptions.ApiException;
import com.commerce.product.exceptions.ResourceNotFoundException;
import com.commerce.product.model.Category;
import com.commerce.product.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yixi Wan
 * @date 2025/10/20 23:25
 * @package com.commerce.ecommapp.service
 * <p>
 * Description:
 */
@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    final CategoryRepository categoryRepository;

    final ModelMapper modelMapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        // 验证category是否已经存在
        if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
            throw new ApiException("Category with the name " + categoryRequest.getCategoryName() + "already exists", HttpStatus.BAD_REQUEST);
        }
        // insert category 并且 返回DTO
        Category category = modelMapper.map(categoryRequest, Category.class);
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponse.class);
    }


    @Override
    public PagedCategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<Category> categories = categoryPage.getContent();
        // 检查category是否为空
        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("No categories found");
        }
        List<CategoryResponse> categoryResponses = categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
        return PagedCategoryResponse.builder()
                .categories(categoryResponses)
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalPages(categoryPage.getTotalPages())
                .totalElements(categoryPage.getTotalElements())
                .islastPage(categoryPage.isLast())
                .build();
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        // 检查category是否存在
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        category.setCategoryName(categoryRequest.getCategoryName());
        category.setDescription(categoryRequest.getDescription());
        categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponse.class);
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));
        categoryRepository.deleteById(categoryId);
    }
}
