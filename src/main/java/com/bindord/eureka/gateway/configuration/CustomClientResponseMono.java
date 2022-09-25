package com.bindord.eureka.gateway.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@AllArgsConstructor
public class CustomClientResponseMono extends Mono<ClientResponse> {

    private final ClientRequest request;
    private final ExchangeFunction next;
    private final Set<String> headersToPropagate;

    @Override
    public void subscribe(CoreSubscriber<? super ClientResponse> subscriber) {
        var context = subscriber.currentContext();

        var requestBuilder = ClientRequest.from(request);
        requestBuilder.headers(httpHeaders -> headersToPropagate.forEach(it -> {
            if (context.hasKey(it)) {
                log.debug("Propagating header key {} - value {}", it, context.get(it));
                httpHeaders.add(it, context.get(it));
            }
        }));
        var mutatedRequest = requestBuilder.build();
        next.exchange(mutatedRequest).subscribe(subscriber);
    }
}
