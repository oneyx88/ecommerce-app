package com.commerce.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yixi Wan
 * @date 2025/11/5 20:14
 * @package com.commerce.gateway
 * <p>
 * Description:
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchanges -> exchanges
                        // ✅ 公共接口（无需 Token）
                        .pathMatchers(
                                "/actuator/**",
                                "/login/**",
                                "/oauth2/**",
                                "/api/v1/users/signup",
                                "/api/v1/users/signup/**",
                                "/fallback/**"
                        ).permitAll()

                        // ✅ 其他所有请求：只要有合法 JWT 就放行
                        .anyExchange().authenticated()
                )

                // ✅ Gateway 只验证 JWT，不解析角色权限
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // ✅ 简单打印整个 JWT payload（所有 claims）
            System.out.println(">>> JWT Claims = " + jwt.getClaims());

            // 正常角色提取逻辑（可保留或暂时注释）
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            List<String> roles = Collections.emptyList();

            if (realmAccess != null && realmAccess.get("roles") instanceof List<?>) {
                roles = ((List<?>) realmAccess.get("roles")).stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .toList();
            }

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return Flux.fromIterable(authorities);
        });

        return converter;
    }


    /**
     * 限流用：按 IP 做 key
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-Forwarded-For") != null
                        ? exchange.getRequest().getHeaders().getFirst("X-Forwarded-For")
                        : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }
}
