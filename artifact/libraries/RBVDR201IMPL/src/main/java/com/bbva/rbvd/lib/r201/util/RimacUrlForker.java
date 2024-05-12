package com.bbva.rbvd.lib.r201.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
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

    public String generateUriAddParticipants(String quotationId,String productId){
        String urifromConsole = this.applicationConfigurationService.getProperty(RBVDProperties.URI_ADD_PARTICIPANTS.getValue().replace("{idProd}",productId));
        int helper = urifromConsole.indexOf(".com");
        String uri = urifromConsole.substring(helper+4).replace("{cotizacion}", quotationId);

        LOGGER.info("***** RimacUrlForker - generateUriAddParticipants ***** uri: {}", uri);

        return uri;
    }

    public String generatePropertyKeyName(String productId){

        String propertyKey = ID_API_PRE_POLICY_EMISSION_RIMAC.concat(".").concat(productId);

        LOGGER.info("***** RimacUrlForker - generatePropertyKeyName ***** propertyKey: {}", propertyKey);
        
        return propertyKey;
    }

    public String generateKeyAddParticipants(String productId){
        String key = RBVDProperties.ID_ADD_PARTICIPANTS.getValue().concat(".").concat(productId);

        LOGGER.info("**** RimacUrlForker - generateKeyAddParticipants - property key : {} ****",key);

        return key;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
