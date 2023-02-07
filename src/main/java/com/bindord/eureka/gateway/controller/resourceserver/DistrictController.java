package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.District;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/district")
@Slf4j
public class DistrictController {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Get all districts",
            responseCode = "200")
    @GetMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<District> getAllDistrict() throws InterruptedException {
        return resourceServerClientConfiguration.init()
                .get()
                .uri("/district")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(District.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
