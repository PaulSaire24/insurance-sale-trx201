package com.bbva.rbvd.lib.r211.impl.properties;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import org.apache.commons.lang3.StringUtils;

public class Properties {

    private ApplicationConfigurationService applicationConfigurationService;

    public String getProperty(String key,String valueDefault){
        String keyDefault = StringUtils.isEmpty(key) ? StringUtils.EMPTY : key;
        return this.getApplicationConfigurationService().getDefaultProperty(keyDefault,valueDefault);
    }

    public String getProperty(String key){
        String keyDefault = StringUtils.isEmpty(key) ? StringUtils.EMPTY : key;
        return this.getApplicationConfigurationService().getProperty(keyDefault);
    }

    private ApplicationConfigurationService getApplicationConfigurationService() {
        return applicationConfigurationService;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

}
