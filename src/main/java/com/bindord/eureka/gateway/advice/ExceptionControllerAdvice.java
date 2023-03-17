package com.bindord.eureka.gateway.advice;

import com.bindord.eureka.gateway.configuration.JacksonFactory;
import com.bindord.eureka.gateway.domain.exception.ApiError;
import com.bindord.eureka.gateway.domain.exception.ApiSubError;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOGGER = LogManager.getLogger(ExceptionControllerAdvice.class);
    public static final String BINDING_ERROR = "Validation has failed";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ApiError> handleBindException(WebExchangeBindException ex) {
        log.error("method {}", "handleBindException(WebExchangeBindException)");
        ex.getModel().entrySet().forEach(e -> {
            LOGGER.warn(e.getKey() + ": " + e.getValue());
        });
        List<ApiSubError> errors = new ArrayList<>();

        for (FieldError x : ex.getBindingResult().getFieldErrors()) {
            errors.add(new ApiSubError(x.getObjectName(), x.getField(), x.getRejectedValue(), x.getDefaultMessage()));
        }
        return Mono.just(new ApiError(BINDING_ERROR, errors));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ApiError> handleBindException(IllegalArgumentException ex) {
        log.error("method {}", "handlerNotFoundValidationException(IllegalArgumentException)");
        return Mono.just(new ApiError(ex.getMessage(), ex));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundValidationException.class)
    public @ResponseBody
    Mono<ApiError> handlerNotFoundValidationException(NotFoundValidationException ex) {
        log.error("method {}", "handlerNotFoundValidationException");
        return Mono.just(new ApiError(ex));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public @ResponseBody
    Mono<ResponseEntity<ApiError>> handlerWebClientResponseException(WebClientResponseException ex) {
        log.error("method {}", "handlerWebClientResponseException");
        ApiError apiErr;
        try {
            apiErr = JacksonFactory.getObjectMapper().readValue(ex.getResponseBodyAsString(), ApiError.class);
        } catch (JsonProcessingException e) {
            log.error("failed to serialize ApiError object");
            return Mono.just(ResponseEntity.status(ex.getStatusCode())
                    .body(new ApiError(ex)));
        }
        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(apiErr));
    }
}


