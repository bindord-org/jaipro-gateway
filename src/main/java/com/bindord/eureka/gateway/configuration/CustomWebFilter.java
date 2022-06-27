package com.bindord.eureka.gateway.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CustomWebFilter implements WebFilter {

    private static final Logger LOGGER = LogManager.getLogger(CustomWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        var path = serverWebExchange.getRequest().getPath().value();

        if (!path.startsWith("/actuator")) {
            LOGGER.info("Endpoint >>> " + path);
        }
        return webFilterChain.filter(serverWebExchange);
    }
}