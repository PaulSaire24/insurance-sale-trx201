package com.bbva.rbvd.lib.r201.properties;

import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class EmissionServiceProperties extends Properties{

    private static final Logger LOGGER = LoggerFactory.getLogger(EmissionServiceProperties.class);

    public Boolean enabledMockPrePolicyEmissionCics(){
        Boolean enabledMockPrePolicyEmissionCics = Boolean.parseBoolean(this.getProperty("enabled.mock.emission.cics", Boolean.FALSE.toString()));
        LOGGER.info(" :: EmissionServiceProperties[ enabledRulesValidationQuotationAmount :: {} ]",enabledMockPrePolicyEmissionCics);
        return enabledMockPrePolicyEmissionCics;
    }


    
}
