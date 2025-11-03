package com.commerce.product.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Yixi Wan
 * @date 2025/10/22 15:47
 * @package com.commerce.ecommapp.service
 * <p>
 * Description:
 */
@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.path}")
    private String path;

    @Value("${user.dir}")
    private String userDir;

    @Override
    public String storeFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        File dest = new File(userDir + File.separator + path, fileName);
        dest.getParentFile().mkdirs();
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
        return path + fileName;
    }
}

