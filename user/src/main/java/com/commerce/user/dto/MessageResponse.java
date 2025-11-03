package com.commerce.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yixi Wan
 * @date 2025/10/28 22:03
 * @package com.commerce.user.dto
 * <p>
 * Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String message;
}
