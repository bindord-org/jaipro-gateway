package com.bindord.eureka.gateway.wsc;

import com.bindord.eureka.gateway.configuration.props.ClientProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;

public class BaseClientConfiguration {

    protected ClientHttpConnector instanceBaseConfig(ClientProperties.ClientConfig cliProps) {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, cliProps.getConnectionTimeout())
                .doOnConnected(conn -> conn
                        .addHandlerLast(
                                new ReadTimeoutHandler(
                                        cliProps.getReadTimeout() / 1000))
                        .addHandlerLast(new WriteTimeoutHandler(
                                cliProps.getWriteTimeout() / 1000))
                );
        return new ReactorClientHttpConnector(httpClient.wiretap(true));
    }
}
