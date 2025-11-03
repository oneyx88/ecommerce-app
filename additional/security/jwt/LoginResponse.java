package com.commerce.user.security.jwt;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Yixi Wan
 * @date 2025/10/24 15:09
 * @package com.commerce.user.security.jwt
 * <p>
 * Description:
 */
@Data
@Builder
public class LoginResponse {
    private String jwtToken;

    private String username;
    private List<String> roles;
}
