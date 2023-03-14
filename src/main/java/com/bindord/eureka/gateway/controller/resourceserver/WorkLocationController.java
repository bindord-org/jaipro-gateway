package com.bindord.eureka.gateway.controller.resourceserver;

import com.bindord.eureka.gateway.wsc.ResourceServerClientConfiguration;
import com.bindord.eureka.resourceserver.model.WorkLocation;
import com.bindord.eureka.resourceserver.model.WorkLocationDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/work-location")
@Slf4j
public class WorkLocationController {

    private final ResourceServerClientConfiguration resourceServerClientConfiguration;

    @ApiResponse(description = "Persist a Work Locations",
            responseCode = "200")
    @PostMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<WorkLocation> persistWorkLocation(@Valid @RequestBody WorkLocationDto workLocation) throws InterruptedException {
        return resourceServerClientConfiguration.init()
                .post()
                .uri("/work-location")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(workLocation), WorkLocationDto.class)
                .retrieve()
                .bodyToMono(WorkLocation.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Update Work Location",
            responseCode = "200")
    @PutMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<WorkLocation> updateWorkLocation(@Valid @RequestBody WorkLocationDto workLocation) throws InterruptedException {
        return resourceServerClientConfiguration.init()
                .put()
                .uri("/work-location")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(workLocation), WorkLocationDto.class)
                .retrieve()
                .bodyToMono(WorkLocation.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Get all Work Locations By SpecialistId",
            responseCode = "200")
    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Flux<WorkLocation> findAllBySpecialistId(@PathVariable UUID id) throws InterruptedException {
        return resourceServerClientConfiguration.init()
                .get()
                .uri(uriBuilder ->  uriBuilder.path("/work-location/{id}")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(WorkLocation.class);
    }
}
