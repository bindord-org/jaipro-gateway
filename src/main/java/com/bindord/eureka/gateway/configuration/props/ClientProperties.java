package com.bindord.eureka.gateway.configuration.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gateway.services.eureka-clients")
@Getter
@Setter
public class ClientProperties {

    private ClientConfig keycloakAuth;

    private ClientConfig resourceServer;

    private ClientConfig authentication;

    @Getter
    @Setter
    public static class ClientConfig {
        private String url;
        private Integer readTimeout;
        private Integer writeTimeout;
        private Integer connectionTimeout;
    }
}
