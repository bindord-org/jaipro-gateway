package com.bindord.eureka.gateway.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.bindord.eureka.gateway.configuration.JacksonFactory.getObjectMapper;

@Component
@Aspect
@Order(value = -2)
public class AspectController {

    private static final Logger LOGGER = LogManager.getLogger(AspectController.class);
    private static final ObjectMapper mapper = getObjectMapper();

    @Before(value = "within(com.bindord.eureka.gateway.controller..*) && !@annotation(com.bindord.eureka.gateway.annotation.NoLogging)",
            argNames = "joinPoint")
    private void before(JoinPoint joinPoint) {
        String caller = joinPoint.getSignature().toShortString();
        LOGGER.info(caller + " method called.");
        if (LOGGER.isInfoEnabled()) {

            Object[] signatureArgs = joinPoint.getArgs();

            for (int i = 0; i < signatureArgs.length; i++) {
//                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                try {
                    if (signatureArgs[i] != null) {
                        LOGGER.info(">> Inputs > " + mapper.writeValueAsString(signatureArgs[i]));
                    }

                } catch (JsonProcessingException e) {
                    LOGGER.warn(e.getMessage());
                }
            }
        }
    }

    @AfterReturning(value = "within(com.bindord.eureka.gateway.controller..*) && !@annotation(com.bindord.eureka.gateway.annotation.NoLogging)",
            returning = "returnValue")
    private void after(JoinPoint joinPoint, Object returnValue) {

    }
}
