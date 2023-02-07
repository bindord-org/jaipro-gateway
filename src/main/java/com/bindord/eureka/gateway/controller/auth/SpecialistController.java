package com.bindord.eureka.gateway.controller.auth;

import com.bindord.eureka.auth.model.Specialist;
import com.bindord.eureka.auth.model.SpecialistPersist;
import com.bindord.eureka.gateway.wsc.AuthClientConfiguration;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/specialist")
@Slf4j
public class SpecialistController {

    private final AuthClientConfiguration authClientConfiguration;

    @ApiResponse(description = "Persist a specialist",
            responseCode = "200")
    @PostMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Specialist> persistSpecialist(@Valid @RequestBody SpecialistPersist specialist) throws InterruptedException {
        return authClientConfiguration.init()
                .post()
                .uri("/specialist")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(specialist), SpecialistPersist.class)
                .retrieve()
                .bodyToMono(Specialist.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
