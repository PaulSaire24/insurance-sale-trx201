package com.bbva.rbvd.lib.r201.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.lib.r201.impl.util.RimacExceptionHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;

public class RimacExceptionHandlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RimacExceptionHandlerTest.class);

    private final RimacExceptionHandler rimacExceptionHandler = new RimacExceptionHandler();

    @Test(expected = BusinessException.class)
    public void handler_ClientException_OK() {
        LOGGER.info("RimacExceptionHandlerTest - Executing handler_ClientException_OK");
        String responseBody = "{\"error\":{\"code\":\"VEHDAT005\",\"message\":\"Error al Validar Datos.\",\"details\":[\"\\\"contactoInspeccion.telefono\\\" debe contener caracteres\"],\"httpStatus\":400}}";
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8);
        rimacExceptionHandler.handler(exception);
    }

    @Test(expected = BusinessException.class)
    public void handler_ServerException_OK() {
        LOGGER.info("RimacExceptionHandlerTest - Executing handler_ServerException_OK");
        String responseBody = "{\"error\":{\"code\":\"VEHEND001\",\"message\":\"La transacción ingresada: e1a98516-7ee4-42c2-9c31-6c392dc52245 no existe o no está activa.\",\"httpStatus\":500}}";
        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "", responseBody.getBytes(), StandardCharsets.UTF_8);
        rimacExceptionHandler.handler(exception);
    }


}
