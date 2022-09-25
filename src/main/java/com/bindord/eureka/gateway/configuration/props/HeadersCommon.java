package com.bindord.eureka.gateway.configuration.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConfigurationProperties(prefix = "eureka.propagate")
@Getter
@Setter
public class HeadersCommon {

    private Set<String> headers;

}
