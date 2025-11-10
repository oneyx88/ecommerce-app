package com.commerce.cart.security;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * @author Yixi Wan
 * @date 2025/11/6 18:00
 * @package com.commerce.product.security
 * <p>
 * Description:
 */
@Configuration
public class FeignClientConfig {

    private final OAuth2AuthorizedClientManager clientManager;

    public FeignClientConfig(OAuth2AuthorizedClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Bean
    public RequestInterceptor serviceTokenInterceptor() {
        return requestTemplate -> {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("ecomm-oauth2")
                    .principal("service-account")
                    .build();

            OAuth2AuthorizedClient client = clientManager.authorize(authorizeRequest);
            if (client != null && client.getAccessToken() != null) {
                String token = client.getAccessToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}

