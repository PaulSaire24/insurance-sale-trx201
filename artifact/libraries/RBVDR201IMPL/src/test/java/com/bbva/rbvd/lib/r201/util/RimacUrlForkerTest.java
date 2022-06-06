package com.bbva.rbvd.lib.r201.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.lib.r201.impl.util.RimacUrlForker;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.mockito.Mockito.*;

public class RimacUrlForkerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RimacUrlForkerTest.class);
    private final RimacUrlForker rimacUrlForker = new RimacUrlForker();
    private ApplicationConfigurationService applicationConfigurationService;

    @Before
	public void setUp() {

		applicationConfigurationService = mock(ApplicationConfigurationService.class);

		rimacUrlForker.setApplicationConfigurationService(applicationConfigurationService);
        when(this.applicationConfigurationService.getProperty("api.connector.emission.rimac.830.url")).thenReturn("https://apitest.rimac.com/vehicular/V1/cotizacion/{ideCotizacion}/emitir");
	}

    @Test
    public void rimacUrlForkerTest_OK() {
        LOGGER.info("RimacUrlForkerTest - Executing rimacUrlForkerTest_OK   ");
        rimacUrlForker.generateUriForSignatureAWS("830", "quotationId");
        rimacUrlForker.generatePropertyKeyName("830");
    }
}