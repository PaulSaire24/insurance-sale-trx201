package com.bbva.rbvd.lib.r201.util;

import com.bbva.rbvd.lib.r201.util.AsoExceptionHandler;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;

public class AsoExceptionHandlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsoExceptionHandlerTest.class);

    @Test
    public void handler_ClientException_OK() {
        LOGGER.info("AsoExceptionHandlerTest - Executing handler_ClientException_OK");
        String responseBody = "{\"messages\":[{\"code\":\"wrongParameters\",\"message\":\"LOS DATOS INGRESADOS SON INVALIDOS\",\"parameters\":[],\"type\":\"FATAL\"}]}";
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8);
        String result =  AsoExceptionHandler.getErrorCode(exception.getResponseBodyAsString());
        Assert.assertEquals("LOS DATOS INGRESADOS SON INVALIDOS", result);

        String responseBody1 = "{\"messages\":[]}";
        HttpClientErrorException exception1 = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", responseBody1.getBytes(), StandardCharsets.UTF_8);
        String result1 =  AsoExceptionHandler.getErrorCode(exception1.getResponseBodyAsString());
        Assert.assertEquals("{\"messages\":[]}", result1);

    }

    @Test
    public void handler_ServerException_OK() {
        LOGGER.info("AsoExceptionHandlerTest - Executing handler_ServerException_OK");
        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.BAD_REQUEST, "", "".getBytes(), StandardCharsets.UTF_8);
        String result = AsoExceptionHandler.getErrorCode(exception.getResponseBodyAsString());
        Assert.assertEquals("No se encontró mensaje del servidor", result);
    }

    @Test
    public void handler_ServerException_Empty() {
        LOGGER.info("AsoExceptionHandlerTest - Executing handler_ServerException_Empty");
        String result = AsoExceptionHandler.getErrorCode("");
        Assert.assertEquals("No se encontró mensaje del servidor", result);

        String result2 = AsoExceptionHandler.getErrorCode("html");
        Assert.assertEquals("No se encontró mensaje del servidor", result2);
    }

}
