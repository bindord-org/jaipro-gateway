package com.bindord.eureka.gateway.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Set;

@Component
public class CustomWebFilter implements WebFilter {

    private static final Logger LOGGER = LogManager.getLogger(CustomWebFilter.class);

    private final Set<String> headers;

    public CustomWebFilter(Set<String> headers) {
        this.headers = headers;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain webFilterChain) {
        var path = exchange.getRequest().getPath().value();
        if (!path.startsWith("/actuator")) {
            LOGGER.debug("Endpoint >>> " + path);
        }
        return webFilterChain.filter(exchange).contextWrite(ctx -> {
            final Context[] updatedContext = {ctx};

            exchange.getRequest().getHeaders().forEach((key, value) -> {
                String keyLower = key.toLowerCase();
                if (headers.contains(keyLower)) {
                    LOGGER.debug("Found HeadersCommon Header - key {} - value {}", key, value.get(0));
                    updatedContext[0] = updatedContext[0].put(keyLower, value.get(0));
                }
            });
            return updatedContext[0];
        });
    }
}