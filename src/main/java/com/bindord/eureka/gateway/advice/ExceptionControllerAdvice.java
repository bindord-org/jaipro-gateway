package com.bindord.eureka.gateway.advice;

import com.bindord.eureka.gateway.domain.exception.ApiError;
import com.bindord.eureka.gateway.domain.exception.ApiSubError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOGGER = LogManager.getLogger(ExceptionControllerAdvice.class);
    public static final  String BINDING_ERROR = "Validation has failed";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ApiError>  handleBindException(WebExchangeBindException ex) {
        ex.getModel().entrySet().forEach(e->{
            LOGGER.warn(e.getKey() + ": " + e.getValue());
        });
        List<ApiSubError> errors = new ArrayList<>();

        for (FieldError x : ex.getBindingResult().getFieldErrors()) {
            errors.add(new ApiSubError(x.getObjectName(), x.getField(), x.getRejectedValue(), x.getDefaultMessage()));
        }
        return Mono.just(new ApiError(HttpStatus.BAD_REQUEST, BINDING_ERROR, errors));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ApiError>  handleBindException(IllegalArgumentException ex) {
        return Mono.just(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundValidationException.class)
    public @ResponseBody
    Mono<ApiError> handlerNotFoundValidationException(NotFoundValidationException ex) {
        return Mono.just(new ApiError(HttpStatus.NOT_FOUND, ex));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public @ResponseBody
    Mono<ResponseEntity<ApiError>> handlerWebClientResponseException(WebClientResponseException ex) throws JsonProcessingException {
        HttpStatus httpStatus = HttpStatus.valueOf(ex.getRawStatusCode());
        var objMapper = new ObjectMapper();
        objMapper.registerModule(new JavaTimeModule());
        ApiError apiError = objMapper.readValue(ex.getResponseBodyAsString(StandardCharsets.UTF_8), ApiError.class);
        return Mono.just(
                new ResponseEntity(
                        apiError, httpStatus)
        );
    }
}


