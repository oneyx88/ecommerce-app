package com.commerce.gateway;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * @author Yixi Wan
 * @date 2025/11/6 16:28
 * @package com.commerce.gateway
 * <p>
 * Description:
 */
@Component
public class JwtUtils {

    /**
     * Get Keycloak userId (UUID) from JWT "sub" claim
     */
    public String getKeycloakId(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        return jwt.getSubject(); // Always present
    }

    /**
     * Get username from JWT "preferred_username" claim
     */
    public String getUsername(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        return jwt.getClaimAsString("preferred_username");
    }

    /**
     * Get user email (ensure Access Token includes "email" claim)
     */
    public String getEmail(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);

        // Keycloak可能存在email或email_verified等结构，全部安全取值
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isEmpty()) {
            // 有些Token中email在其他嵌套结构里
            Object other = jwt.getClaims().get("user_email");
            if (other != null) {
                email = other.toString();
            }
        }

        if (email == null || email.isEmpty()) {
            throw new IllegalStateException("Email not found in JWT claims");
        }
        return email;
    }

    /**
     * Safely extract JWT object from Authentication
     */
    private Jwt extractJwt(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("Authentication is null");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt;
        } else {
            throw new IllegalStateException("Principal is not a Jwt instance: " + principal.getClass());
        }
    }
}
