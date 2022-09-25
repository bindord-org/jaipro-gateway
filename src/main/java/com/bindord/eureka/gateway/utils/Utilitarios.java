package com.bindord.eureka.gateway.utils;

import com.bindord.eureka.gateway.configuration.JacksonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class Utilitarios {

    public static final Logger LOGGER = LogManager.getLogger(Utilitarios.class);

    private Utilitarios() {
    }

    public static void createDirectoryStartUp(String basePath, String[] paths) {

        for (int i = 0; i < paths.length; i++) {
            File dirFile = new File(basePath);

            if (!dirFile.exists()) {
                try {
                    dirFile.mkdir();
                } catch (SecurityException se) {
                    LOGGER.info(se.getMessage());
                }
            } else {
                File nuevoFile = new File(basePath + paths[i]);
                if (!nuevoFile.exists()) {
                    try {
                        nuevoFile.mkdir();
                    } catch (SecurityException se) {
                        LOGGER.info(se.getMessage());
                    }
                }
            }
        }
    }

    public static String convertJSONtoString(Object obj) {
        try {
            return JacksonFactory.getObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }

    public static ObjectMapper instanceObjectMapper() {
        return JacksonFactory.getObjectMapper();
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public static String customResponse(String code, String domainPk) {
        return "{\"code\": \"" + code + "\", \"id\": \"" + domainPk + "\"}";
    }

    public static String[] filterStringArray(String[] array) {
        array = Arrays.stream(array)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
        return array;
    }

}

