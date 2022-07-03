package com.bindord.eureka.gateway.controller.auth;

import com.bindord.eureka.gateway.advice.CustomValidationException;
import com.bindord.eureka.gateway.advice.NotFoundValidationException;
import com.bindord.eureka.gateway.configuration.ClientProperties;
import com.bindord.eureka.keycloak.auth.model.UserLogin;
import com.bindord.eureka.keycloak.auth.model.UserToken;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("${service.ingress.context-path}/auth")
@Slf4j
public class AuthenticationController {

    @Autowired
    private ClientProperties clientProperties;

//    @Autowired
    private WebClient client = WebClient.create();


    @ApiResponse(description = "Storage a professional",
            responseCode = "200")
    @PostMapping(value = "/login",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<UserToken> login(@Valid @RequestBody UserLogin userLogin)
            throws CustomValidationException, NotFoundValidationException, URISyntaxException {
        return client.post()
                .uri(new URI(clientProperties.getKeycloakAuth().getUrl() + "/auth"))
                .header("Authorization", "Bearer MY_SECRET_TOKEN")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(userLogin), UserLogin.class)
                .retrieve()
                .bodyToMono(UserToken.class);
    }
}
