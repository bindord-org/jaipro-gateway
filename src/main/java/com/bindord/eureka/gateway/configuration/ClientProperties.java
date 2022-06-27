package com.bindord.eureka.gateway.configuration;

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

    @Getter
    @Setter
    public static class ClientConfig {
        private String url;
        private String readTimeout;
        private String writeTimeout;
        private String connectionTimeout;
    }
}
