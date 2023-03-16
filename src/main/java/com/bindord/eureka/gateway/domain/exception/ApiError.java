package com.bindord.eureka.gateway.domain.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError {

    private String code;
    private String message;
    private String debugMessage;
    private List<ApiSubError> subErrors;

    private ApiError() {

    }

    public ApiError(Throwable ex) {
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public ApiError(String message, Throwable ex) {
        this.message = message;
        this.debugMessage = ex.getMessage();
    }

    public ApiError(String message, Throwable ex, List<ApiSubError> subErrors) {
        this.message = message;
        this.debugMessage = ex.getMessage();
        this.subErrors = subErrors;
    }

    public ApiError(String message, List<ApiSubError> subErrors) {
        this.message = message;
        this.subErrors = subErrors;
    }
}
