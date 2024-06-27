package com.example.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Order(1)
@Component
@Slf4j
public class LogTraceFilter implements GlobalFilter {

    public static final String CORRELATION_ID = "cartblitz";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
        log.info("cartblitz request ... ");

        if (this.getCorrelationId(requestHeaders) != null) {
            log.info("correlation-id found {}:",
                    this.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = this.setCorrelationId(exchange, correlationID);
            log.info("correlation-id generated: {}", correlationID);
        }
        return chain.filter(exchange);
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

    private String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            return requestHeaders.get(CORRELATION_ID).stream().findFirst().get();
        } else {
            return null;
        }
    }

    private ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(CORRELATION_ID, correlationId).build()).build();
    }
}
