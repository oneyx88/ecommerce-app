package com.commerce.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Yixi Wan
 * @date 2025/11/6 16:34
 * @package com.commerce.gateway
 * <p>
 * Description:
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHeaderInjectorFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .flatMap(principal -> {
                    if (principal instanceof Authentication authentication && authentication.isAuthenticated()) {
                        String userId = jwtUtils.getKeycloakId(authentication);
                        String email = jwtUtils.getEmail(authentication);
                        String username = jwtUtils.getUsername(authentication);
                        log.info("User authenticated: userId={}, email={}, username={}", userId, email, username);

                        // ✅ 创建新的可变 Request，不能直接修改原 headers
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", userId)
                                .header("X-User-Email", email)
                                .header("X-User-Name", username)
                                .build();

                        // ✅ 构建新的 Exchange
                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(mutatedRequest)
                                .build();

                        return chain.filter(mutatedExchange);
                    }
                    // 匿名访问（signup/login）
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    // ✅ 顺序 10：在限流、断路器之后执行，避免 ReadOnlyHeader 异常
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
