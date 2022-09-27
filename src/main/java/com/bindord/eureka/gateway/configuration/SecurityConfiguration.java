package com.bindord.eureka.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Profile(value = "!nosec")
public class SecurityConfiguration {

    private final CorsCustomConfiguration corsConfiguration;

    public SecurityConfiguration(CorsCustomConfiguration corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }

    @Bean()
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf().disable()
                .cors().configurationSource(corsConfiguration.corsConfigurationSource());
        http
                .authorizeExchange()
                .pathMatchers("/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                .pathMatchers("/swagger**").permitAll()
                .pathMatchers("/v3/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(keycloakGrantedAuthoritiesConverter());
        return http.build();
    }

    @Bean
    public KeycloakGrantedAuthoritiesConverter keycloakGrantedAuthoritiesConverter() {
        return new KeycloakGrantedAuthoritiesConverter();
    }

}
