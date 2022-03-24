package com.bbva.rbvd.lib.r201.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RimacUrlForker {
    private static final Logger LOGGER = LoggerFactory.getLogger(RimacUrlForker.class);

    private static final String URI_EMISSION = "api.connector.emission.rimac.{idProd}.url";
    private static final String ID_API_PRE_POLICY_EMISSION_RIMAC = "emission.rimac";

    private ApplicationConfigurationService applicationConfigurationService;

    public String generateUriForSignatureAWS(String productId, String quotationId){

        String urifromConsole = this.applicationConfigurationService.getProperty(URI_EMISSION.replace("{idProd}", productId));
		int helper = urifromConsole.indexOf(".com");
		String uri = urifromConsole.substring(helper+4).replace("{ideCotizacion}", quotationId);

        LOGGER.info("***** RimacUrlForker - generateUriForSignatureAWS ***** uri: {}", uri);

        return uri;
    }

    public String generatePropertyKeyName(String productId){

        String propertyKey = ID_API_PRE_POLICY_EMISSION_RIMAC.concat(".").concat(productId);

        LOGGER.info("***** RimacUrlForker - generatePropertyKeyName ***** propertyKey: {}", propertyKey);
        
        return propertyKey;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
