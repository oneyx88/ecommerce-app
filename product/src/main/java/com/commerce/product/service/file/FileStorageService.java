package com.commerce.product.service.file;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Yixi Wan
 * @date 2025/10/22 15:46
 * @package com.commerce.ecommapp.service
 * <p>
 * Description:
 */
public interface FileStorageService {
    String storeFile(MultipartFile file);
}
