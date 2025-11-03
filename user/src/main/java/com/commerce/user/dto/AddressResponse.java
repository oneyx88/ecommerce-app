package com.commerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Yixi Wan
 * @date 2025/10/29 15:08
 * @package com.commerce.user.dto
 * <p>
 * Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    private String label;         // 如 "Home", "Office", "Warehouse"
    private Boolean isDefault;    // 是否为默认地址
    private Boolean deleted;      // 是否被软删除

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}