package com.bbva.rbvd.lib.r201.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.lib.r201.impl.util.AsoExceptionHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;

public class AsoExceptionHandlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsoExceptionHandlerTest.class);

    private final AsoExceptionHandler asoExceptionHandler = new AsoExceptionHandler();

    @Test(expected = BusinessException.class)
    public void handler_ClientException_OK() {
        LOGGER.info("AsoExceptionHandlerTest - Executing handler_ClientException_OK");
        String responseBody = "{\"messages\":[{\"code\":\"wrongParameters\",\"message\":\"LOS DATOS INGRESADOS SON INVALIDOS\",\"parameters\":[],\"type\":\"FATAL\"}]}";
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8);
        asoExceptionHandler.handler(exception);
    }

    @Test(expected = BusinessException.class)
    public void handler_ServerException_OK() {
        LOGGER.info("AsoExceptionHandlerTest - Executing handler_ServerException_OK");
        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.BAD_REQUEST, "", "".getBytes(), StandardCharsets.UTF_8);
        asoExceptionHandler.handler(exception);
    }

}
