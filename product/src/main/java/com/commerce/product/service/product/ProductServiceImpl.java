package com.commerce.product.service.product;


import com.commerce.product.clients.InventoryClientService;
import com.commerce.product.clients.InventoryResponse;
import com.commerce.product.dto.product.PagedProductResponse;
import com.commerce.product.dto.product.ProductRequest;
import com.commerce.product.dto.product.ProductResponse;
import com.commerce.product.exceptions.ResourceNotFoundException;
import com.commerce.product.model.Category;
import com.commerce.product.model.Product;
import com.commerce.product.repository.CategoryRepository;
import com.commerce.product.repository.ProductRepository;
import com.commerce.product.service.category.CategoryService;
import com.commerce.product.service.file.FileStorageService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yixi Wan
 * @date 2025/10/22 12:12
 * @package com.commerce.ecommapp.service
 * <p>
 * Description:
 */
@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private InventoryClientService inventoryClientService;

    @Value("${cache.ttl.product}")
    private Duration productCacheTtl;

    @Override
    public ProductResponse addProduct(Long categoryId, ProductRequest productRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Product product = modelMapper.map(productRequest, Product.class);
        product.setCategory(category);
        product.setImage("default-product-image.jpg");
        product.setSpecialPrice(productRequest.getPrice() * (1 - productRequest.getDiscount() * 0.01));
        productRepository.save(product);
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public PagedProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findAll(pageable);
        return getPagedProductResponse(productPage);
    }

    @Override
    public PagedProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // 检查category是否存在
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        // 获取products
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Page<Product> productPage = productRepository.findByCategory(category, PageRequest.of(pageNumber, pageSize, sort));
        return getPagedProductResponse(productPage);
    }

    @Override
    public PagedProductResponse searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> productPage = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
        return getPagedProductResponse(productPage);
    }

    private PagedProductResponse getPagedProductResponse(Page<Product> productPage) {
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found");
        }
        List<ProductResponse> productResponses = products.stream()
                .map(product -> {
                    ProductResponse response = modelMapper.map(product, ProductResponse.class);

                    // 调用库存服务，设置库存数量
                    InventoryResponse inventoryResponse = inventoryClientService.getInventoryByProductId(product.getProductId());
                    response.setAvailableStock(inventoryResponse.getAvailableStock());

                    return response;
                })
                .collect(Collectors.toList());
        return PagedProductResponse.builder()
                .products(productResponses)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .islastPage(productPage.isLast())
                .build();
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
        // 检查product是否存在
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setDiscount(productRequest.getDiscount());
        product.setSpecialPrice(productRequest.getPrice() * (1 - productRequest.getDiscount() * 0.01));
        productRepository.save(product);
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public void deleteProduct(Long productId) {
        // 检查product是否存在
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        productRepository.delete(product);
    }

    @Override
    public ProductResponse updateProductImage(Long productId, MultipartFile image) {
        // 检查product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        String imagePath = fileStorageService.storeFile(image);
        product.setImage(imagePath);
        productRepository.save(product);
        return modelMapper.map(product, ProductResponse.class);

    }

    @Override
    public ProductResponse getProductById(Long productId) {
        String cacheKey = "product_cache:" + productId;

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof ProductResponse cachedResponse) {
            redisTemplate.expire(cacheKey, productCacheTtl); // 更新TTL
            return cachedResponse;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        ProductResponse response = modelMapper.map(product, ProductResponse.class);

        InventoryResponse inventoryResponse = inventoryClientService.getInventoryByProductId(productId);
        response.setAvailableStock(inventoryResponse.getAvailableStock());

        redisTemplate.opsForValue().set(cacheKey, response, productCacheTtl); // 自动过期
        return response;
    }

}
