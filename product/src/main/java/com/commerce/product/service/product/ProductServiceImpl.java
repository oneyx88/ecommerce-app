package com.commerce.product.service.product;


import com.commerce.product.clients.InventoryClientService;
import com.commerce.product.clients.InventoryResponse;
import com.commerce.product.dto.product.PagedProductResponse;
import com.commerce.product.dto.product.ProductRequest;
import com.commerce.product.dto.product.ProductResponse;
import com.commerce.product.dto.product.ProductSummary;
import com.commerce.product.exceptions.ResourceNotFoundException;
import com.commerce.product.kafka.producer.ProductCacheEventProducer;
import com.commerce.product.model.Category;
import com.commerce.product.model.Product;
import com.commerce.product.repository.CategoryRepository;
import com.commerce.product.repository.ProductRepository;
import com.commerce.product.service.category.CategoryService;
import com.commerce.product.service.file.FileStorageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yixi Wan
 * @date 2025/10/22 12:12
 * @package com.commerce.ecommapp.service
 * <p>
 * Description:
 * 商品列表页调取时缓存每一个product Summary，更新时同步删除缓存
 * 商品详细页调取时缓存每一个product，更新时同步删除缓存
 * todo 热点商品
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final InventoryClientService inventoryClientService;

    @Value("${cache.ttl.product}")
    private Duration productCacheTtl;

    // ========================= 商品创建 =========================
    @Override
    public ProductResponse addProduct(Long categoryId, ProductRequest productRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Product product = modelMapper.map(productRequest, Product.class);
        product.setCategory(category);
        product.setImage("default-product-image.jpg");
        product.setSpecialPrice(productRequest.getPrice() * (1 - productRequest.getDiscount() * 0.01));
        productRepository.save(product);

        // 清除分类页缓存
        clearCategoryListCache(category.getCategoryName());
        return modelMapper.map(product, ProductResponse.class);
    }

    // ========================= 商品列表（全部 / 分类 / 搜索） =========================

    @Override
    public PagedProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findAll(pageable);
        return getPagedSummaryResponse(productPage);
    }

    @Override
    public PagedProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Pageable pageable = buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByCategory(category, pageable);
        return getPagedSummaryResponse(productPage);
    }

    @Override
    public PagedProductResponse searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = buildPageable(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
        return getPagedSummaryResponse(productPage);
    }

    // ------------------- 公共方法：统一列表页逻辑 -------------------
    private PagedProductResponse getPagedSummaryResponse(Page<Product> productPage) {
        List<Long> productIds = productPage.getContent().stream()
                .map(Product::getProductId)
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            throw new ResourceNotFoundException("No products found");
        }

        // 1️⃣ 批量取缓存
        List<String> keys = productIds.stream().map(id -> "product_summary:" + id).toList();
        List<Object> cachedList = redisTemplate.opsForValue().multiGet(keys);

        List<ProductSummary> summaries = new ArrayList<>();
        List<Long> missingIds = new ArrayList<>();

        // 2️⃣ 判断哪些商品没命中缓存
        for (int i = 0; i < productIds.size(); i++) {
            Object obj = cachedList.get(i);
            if (obj instanceof ProductSummary summary) {
                summaries.add(summary);
            } else {
                missingIds.add(productIds.get(i));
            }
        }

        // 3️⃣ 对未命中的商品从数据库查并写回 Redis
        if (!missingIds.isEmpty()) {
            List<Product> missingProducts = productRepository.findAllById(missingIds);
            for (Product p : missingProducts) {
                ProductSummary summary = ProductSummary.builder()
                        .productId(p.getProductId())
                        .productName(p.getProductName())
                        .image(p.getImage())
                        .price(p.getPrice())
                        .discount(p.getDiscount())
                        .specialPrice(p.getSpecialPrice())
                        .build();
                redisTemplate.opsForValue().set("product_summary:" + p.getProductId(), summary, productCacheTtl);
                summaries.add(summary);
            }
        }

        // 4️⃣ 保证顺序与分页一致
        summaries.sort(Comparator.comparingInt(o -> productIds.indexOf(o.getProductId())));

        return PagedProductResponse.builder()
                .productSummaries(summaries)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .islastPage(productPage.isLast())
                .build();
    }

    // ========================= 商品详情缓存 =========================
    @Override
    public ProductResponse getProductById(Long productId) {
        String cacheKey = "product_cache:" + productId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached instanceof ProductResponse cachedResponse) {
            redisTemplate.expire(cacheKey, productCacheTtl);
            return cachedResponse;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        InventoryResponse inventoryResponse = inventoryClientService.getInventoryByProductId(productId);
        response.setAvailableStock(inventoryResponse.getAvailableStock());

        redisTemplate.opsForValue().set(cacheKey, response, productCacheTtl);
        return response;
    }

    // ========================= 商品更新 / 删除 =========================

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setDiscount(productRequest.getDiscount());
        product.setSpecialPrice(productRequest.getPrice() * (1 - productRequest.getDiscount() * 0.01));
        productRepository.save(product);

        // 删除相关缓存
        clearProductCache(product);
        redisTemplate.delete("product_summary:" + product.getProductId());

        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        productRepository.delete(product);

        // 删除详情和 summary 缓存
        clearProductCache(product);
        redisTemplate.delete("product_summary:" + product.getProductId());
    }

    @Override
    public ProductResponse updateProductImage(Long productId, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        String imagePath = fileStorageService.storeFile(image);
        product.setImage(imagePath);
        productRepository.save(product);

        clearProductCache(product);
        redisTemplate.delete("product_summary:" + product.getProductId());

        return modelMapper.map(product, ProductResponse.class);
    }

    // ========================= 公共辅助方法 =========================
    private Pageable buildPageable(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private void clearProductCache(Product product) {
        redisTemplate.delete("product_cache:" + product.getProductId());
        if (product.getCategory() != null) {
            clearCategoryListCache(product.getCategory().getCategoryName());
        }
        log.info("🧹 Deleted cache for product {}, category {}",
                product.getProductId(),
                product.getCategory() != null ? product.getCategory().getCategoryName() : "N/A");
    }

    private void clearCategoryListCache(String categoryName) {
        for (int i = 0; i < 3; i++) {
            redisTemplate.delete(String.format("product_list:%s:page:%d", categoryName, i));
        }
    }
}
