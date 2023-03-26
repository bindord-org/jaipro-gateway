package com.bindord.eureka.gateway.controller.auth;

import com.bindord.eureka.auth.model.Customer;
import com.bindord.eureka.auth.model.CustomerPersist;
import com.bindord.eureka.resourceserver.model.CustomerInformationUpdateDto;
import com.bindord.eureka.resourceserver.model.CustomerLocationUpdateDto;
import com.bindord.eureka.resourceserver.model.CustomerPasswordUpdateDto;
import com.bindord.eureka.gateway.wsc.AuthClientConfiguration;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("${service.ingress.context-path}/customer")
@Slf4j
public class CustomerController {

    private final AuthClientConfiguration authClientConfiguration;

    @ApiResponse(description = "Persist a customer",
            responseCode = "200")
    @PostMapping(value = "",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Customer> persistCustomer(@Valid @RequestBody CustomerPersist customer) {
        return authClientConfiguration.init()
                .post()
                .uri("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), CustomerPersist.class)
                .retrieve()
                .bodyToMono(Customer.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Update a customer information",
            responseCode = "200")
    @PutMapping(value = "/updateInformation",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Customer> updateInformation(@Valid @RequestBody CustomerInformationUpdateDto customer) {
        return authClientConfiguration.init()
                .put()
                .uri("/customer/updateAbout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), CustomerInformationUpdateDto.class)
                .retrieve()
                .bodyToMono(Customer.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Update a customer information",
            responseCode = "200")
    @PutMapping(value = "/updateLocation",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Boolean> updateLocation(@Valid @RequestBody CustomerLocationUpdateDto customer) {
        return authClientConfiguration.init()
                .put()
                .uri("/customer/updateLocation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), CustomerLocationUpdateDto.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @ApiResponse(description = "Update a customer information",
            responseCode = "200")
        @PutMapping(value = "/updatePassword",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Boolean> updatePassword(@Valid @RequestBody CustomerPasswordUpdateDto customer) {
        return authClientConfiguration.init()
                .put()
                .uri("/customer/updatePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(customer), CustomerPasswordUpdateDto.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
