package com.commerce.user.security.jwt;

import lombok.Builder;
import lombok.Data;

/**
 * @author Yixi Wan
 * @date 2025/10/24 15:08
 * @package com.commerce.user.security.jwt
 * <p>
 * Description:
 */
@Data
@Builder
public class LoginRequest {
    private String username;
    private String password;
}
