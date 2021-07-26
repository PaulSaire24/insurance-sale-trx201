package com.bbva.rbvd.lib.r201.impl.util;

import com.bbva.rbvd.dto.insrncsale.aso.ErrorASO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.util.Objects;

public class AsoExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsoExceptionHandler.class);

    public void handler(RestClientException exception) {
        if(exception instanceof HttpClientErrorException) {
            LOGGER.info("ExceptionHandler - HttpClientErrorException");
            this.clientExceptionHandler((HttpClientErrorException) exception);
        } else {
            LOGGER.info("ExceptionHandler - HttpServerErrorException");
            this.serverExceptionHandler((HttpServerErrorException) exception);
        }
    }

    private void clientExceptionHandler(HttpStatusCodeException clientException) {
        LOGGER.debug("HttpStatusCodeException - Response body: {}", clientException.getResponseBodyAsString());
        String errorCode = this.getErrorCode(clientException.getResponseBodyAsString());
        this.throwingBusinessException(errorCode);
    }

    private void serverExceptionHandler(HttpServerErrorException serverException) {
        LOGGER.debug("HttpStatusCodeException - Response body: {}", serverException.getResponseBodyAsString());
        String errorCode = this.getErrorCode(serverException.getResponseBodyAsString());
        this.throwingBusinessException(errorCode);
    }

    private String getErrorCode(String responseBody) {
        if(responseBody.isEmpty() || responseBody.contains("html")) {
            return null;
        }
        ErrorASO error = JsonHelper.getInstance().fromString(responseBody, ErrorASO.class);
        return error.getMessages().get(0).getCode();
    }

    private void throwingBusinessException(String errorCode) {
        if(Objects.nonNull(errorCode)) {
            LOGGER.debug("Client exception error -> {}", errorCode);
            throw RBVDValidation.build(RBVDErrors.BAD_REQUEST_CREATEINSURANCE);
        } else {
            throw RBVDValidation.build(RBVDErrors.QUERY_EMPTY_RESULT);
        }
    }

}
