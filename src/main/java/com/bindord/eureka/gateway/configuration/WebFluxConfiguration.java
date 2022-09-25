package com.bindord.eureka.gateway.configuration;

import com.bindord.eureka.gateway.configuration.props.CorsAddress;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.config.CorsRegistration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@AllArgsConstructor
@Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {

    private CorsAddress corsAddress;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        CorsRegistration corsRegistration = corsRegistry.addMapping("/**");

        for (String address : corsAddress.getAddress()) {
            corsRegistration.allowedOrigins(address);
        }

        corsRegistration
                .allowedOrigins()
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PATCH.name())
                .maxAge(3600);
    }
}
