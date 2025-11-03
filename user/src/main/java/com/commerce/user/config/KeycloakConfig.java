package com.commerce.user.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yixi Wan
 * @date 2025/10/28 23:08
 * @package com.commerce.user.config
 * <p>
 * Description:
 */
@Configuration
public class KeycloakConfig {
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    @Value("${keycloak.username}")
    private String username;
    @Value("${keycloak.password}")
    private String password;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType(org.keycloak.OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .build();
}


}
