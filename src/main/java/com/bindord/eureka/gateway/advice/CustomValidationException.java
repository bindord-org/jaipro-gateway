package com.bindord.eureka.gateway.advice;

public class CustomValidationException extends Exception {

    private String internalCode;

    public CustomValidationException(String message) {
        super(message);
    }

    public CustomValidationException(String message, String internalCode) {
        super(message);
        this.internalCode = internalCode;
    }

    public String getInternalCode() {
        return internalCode;
    }
}
