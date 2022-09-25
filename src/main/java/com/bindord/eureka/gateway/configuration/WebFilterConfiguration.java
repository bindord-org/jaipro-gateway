package com.bindord.eureka.gateway.configuration;

import com.bindord.eureka.gateway.configuration.props.HeadersCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebFilter;

@Configuration
public class WebFilterConfiguration {

    private HeadersCommon headersCommon;

    public WebFilterConfiguration(HeadersCommon headersCommon) {
        this.headersCommon = headersCommon;
    }

    @Bean
    public WebClient.Builder webClient() {
        return WebClient.builder().filter(
                this.customExchangeFilterFunction()
        );
    }

    @Bean
    public WebFilter openTracingFilter() {
        return new CustomWebFilter(headersCommon.getHeaders());
    }

    @Bean
    public CustomExchangeFilterFunction customExchangeFilterFunction() {
        return new CustomExchangeFilterFunction(headersCommon.getHeaders());
    }
}
