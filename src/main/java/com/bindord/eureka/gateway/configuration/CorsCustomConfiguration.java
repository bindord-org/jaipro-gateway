package com.bindord.eureka.gateway.configuration;

import com.bindord.eureka.gateway.configuration.props.CorsAddress;
import com.bindord.eureka.gateway.configuration.props.HeadersCommon;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.time.Duration;

@Configuration
@AllArgsConstructor
@Slf4j
public class CorsCustomConfiguration {


    private CorsAddress corsAddress;

    private HeadersCommon headersCommon;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();

        for (String address : corsAddress.getAddress()) {
            log.debug("Host addresses allowed for cors: {}", address);
            config.addAllowedOrigin(address);
        }

        config.addAllowedMethod(HttpMethod.OPTIONS.name());
        config.addAllowedMethod(HttpMethod.GET.name());
        config.addAllowedMethod(HttpMethod.PUT.name());
        config.addAllowedMethod(HttpMethod.POST.name());
        config.addAllowedMethod(HttpMethod.DELETE.name());
        config.addAllowedMethod(HttpMethod.PATCH.name());

        for (String header : headersCommon.getHeaders()) {
            log.debug("Headers allowed for cors: {}", header);
            config.addAllowedHeader(header);
        }

        config.setMaxAge(Duration.ofDays(1));

        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
