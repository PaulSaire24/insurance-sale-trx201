package com.bbva.rbvd.lib.r201.impl.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insrncsale.bo.ErrorResponseBO;
import com.bbva.rbvd.dto.insrncsale.bo.ErrorRimacBO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

public class RimacExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RimacExceptionHandler.class);
    private static final String INACTIVE_QUOTATION_ERROR_CODE = "VEHEND001";
    private static final String BAD_REQUEST_EMISSION_RIMAC_ERROR_CODE = "VEHDAT005";

    public void handler(RestClientException exception) {
        if(exception instanceof HttpClientErrorException) {
            LOGGER.info("RimacExceptionHandler - HttpClientErrorException");
            this.clientExceptionHandler((HttpClientErrorException) exception);
        } else {
            LOGGER.info("RimacExceptionHandler - HttpServerErrorException");
            this.serverExceptionHandler((HttpServerErrorException) exception);
        }
    }

    private void clientExceptionHandler(HttpClientErrorException exception) {
        LOGGER.debug("HttpClientErrorException - Response body: {}", exception.getResponseBodyAsString());
        ErrorRimacBO errorObject = this.getErrorObject(exception.getResponseBodyAsString());
        this.throwingBusinessException(errorObject.getError());
    }

    private void serverExceptionHandler(HttpServerErrorException exception) {
        LOGGER.debug("HttpServerErrorException - Response Body: {}", exception.getResponseBodyAsString());
        ErrorRimacBO errorObject = this.getErrorObject(exception.getResponseBodyAsString());
        this.throwingBusinessException(errorObject.getError());
    }

    private ErrorRimacBO getErrorObject(String responseBody) {
        return JsonHelper.getInstance().fromString(responseBody, ErrorRimacBO.class);
    }

    private void throwingBusinessException(ErrorResponseBO error) {
        LOGGER.debug("Exception error code -> {}", error.getCode());
        if(error.getCode().equals(INACTIVE_QUOTATION_ERROR_CODE)) {
            BusinessException be = RBVDValidation.build(RBVDErrors.NON_EXISTENT_OR_INACTIVE_QUOTATION);
            be.setMessage(error.getMessage());
            throw be;
        } else if(error.getCode().equals(BAD_REQUEST_EMISSION_RIMAC_ERROR_CODE)) {
            BusinessException be = RBVDValidation.build(RBVDErrors.BAD_REQUEST_EMISSION_RIMAC);
            be.setMessage(be.getMessage() + " | " + error.getDetails().toString());
            throw be;
        }
    }

}
