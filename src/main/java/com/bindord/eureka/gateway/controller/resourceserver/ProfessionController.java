package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.Profession;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/profession")
@Slf4j
public class ProfessionController {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Get all professions",
            responseCode = "200")
    @GetMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<Profession> findAllProfession() throws InterruptedException {
        return resourceServerClientConfiguration.init()
                .get()
                .uri("/profession")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Profession.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
