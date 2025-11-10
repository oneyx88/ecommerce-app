package com.commerce.gateway;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Yixi Wan
 * @date 2025/11/5 20:17
 * @package com.commerce.gateway
 * <p>
 * Description:
 */
@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    long cost = System.currentTimeMillis() - start;
                    int status = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 200;
                    log.info("[GW] {} {} -> {} ({}ms)", method, path, status, cost);
                })
                .doOnError(ex -> {
                    long cost = System.currentTimeMillis() - start;
                    log.error("[GW] {} {} ERROR {}ms {}", method, path, cost, ex.getMessage());
                });
    }

    @Override
    public int getOrder() {
        return -1; // 越小越早执行
    }
}
