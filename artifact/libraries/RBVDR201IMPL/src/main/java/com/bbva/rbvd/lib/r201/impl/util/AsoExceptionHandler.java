package com.bbva.rbvd.lib.r201.impl.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class AsoExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsoExceptionHandler.class);
    private AsoExceptionHandler() {
    }

    private static final String NON_EXISTENT_MESSAGE = "No se encontró mensaje del servidor";
    public static String getErrorCode(String responseBody) {
        if(StringUtils.isEmpty(responseBody) || responseBody.contains("html")) {
            LOGGER.info("*** Null or empty error responseBody ***");
            return NON_EXISTENT_MESSAGE;
        }

        JsonObject jsonResponseObject = new JsonParser().parse(responseBody).getAsJsonObject();
        JsonArray jsonResponseArray = jsonResponseObject.getAsJsonArray("messages");
        if(Objects.nonNull(jsonResponseArray) && jsonResponseArray.size()>0){
            LOGGER.info("*** Non null or empty error responseBody {} ***", responseBody);
            return Objects.nonNull(jsonResponseArray.get(0).getAsJsonObject().get("message")) ?
                    jsonResponseArray.get(0).getAsJsonObject().get("message").toString().replace("\"",""):
                    NON_EXISTENT_MESSAGE;
        }
        LOGGER.info("*** Non null or empty error responseBody with wrong structure {} ***", responseBody);
        return responseBody;
    }

}
