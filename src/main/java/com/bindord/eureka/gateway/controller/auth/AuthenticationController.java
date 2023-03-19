package com.bindord.eureka.gateway.controller.auth;

import com.bindord.eureka.auth.model.AuthUser;
import com.bindord.eureka.auth.model.RefreshToken;
import com.bindord.eureka.gateway.wsc.AuthClientConfiguration;
import com.bindord.eureka.keycloak.auth.model.UserLogin;
import com.bindord.eureka.keycloak.auth.model.UserToken;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/auth")
@Slf4j
public class AuthenticationController {

    private final AuthClientConfiguration authClientConfiguration;

    @ApiResponse(description = "Authentication an user",
            responseCode = "200")
    @PostMapping(value = "/login",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<AuthUser> login(@Valid @RequestBody UserLogin userLogin) {
        return authClientConfiguration.init().post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(userLogin), UserLogin.class)
                .retrieve()
                .bodyToMono(AuthUser.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Refresh access token",
            responseCode = "200")
    @PostMapping(value = "/refresh-token",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<UserToken> refreshToken(@Valid @RequestBody RefreshToken refreshToken) {
        return authClientConfiguration.init().post()
                .uri("/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(refreshToken), RefreshToken.class)
                .retrieve()
                .bodyToMono(UserToken.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
