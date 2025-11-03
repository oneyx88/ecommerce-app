package com.commerce.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/11/2 17:31
 * @package com.commerce.address.dto
 * <p>
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    private String label;
    private Boolean isDefault;
}
