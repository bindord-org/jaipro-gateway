package com.bindord.eureka.gateway.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    public ApiSubError() {}

    public ApiSubError(String object, String field, Object rejectedValue, String message) {
        this.object = object;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }
}