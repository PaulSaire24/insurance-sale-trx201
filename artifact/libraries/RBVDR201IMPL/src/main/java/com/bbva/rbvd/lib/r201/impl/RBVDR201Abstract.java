package com.bbva.rbvd.lib.r201.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.elara.utility.api.connector.APIConnectorBuilder;
import com.bbva.pisd.lib.r352.PISDR352;
import com.bbva.rbvd.lib.r046.RBVDR046;
import com.bbva.rbvd.lib.r066.RBVDR066;
import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.lib.r609.RBVDR609;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class RBVDR201Abstract extends AbstractLibrary implements RBVDR201 {

	protected APIConnector externalApiConnector;

	protected APIConnectorBuilder apiConnectorBuilder;

	protected APIConnector internalApiConnector;

	protected ApplicationConfigurationService applicationConfigurationService;

	protected RBVDR066 rbvdR066;

	protected RBVDR046 rbvdR046;

	protected PISDR352 pisdR352;

	protected RBVDR609 rbvdR609;

	protected APIConnector internalApiConnectorImpersonation;


	/**
	* @param externalApiConnector the this.externalApiConnector to set
	*/
	public void setExternalApiConnector(APIConnector externalApiConnector) {
		this.externalApiConnector = externalApiConnector;
	}

	/**
	* @param apiConnectorBuilder the this.apiConnectorBuilder to set
	*/
	public void setApiConnectorBuilder(APIConnectorBuilder apiConnectorBuilder) {
		this.apiConnectorBuilder = apiConnectorBuilder;
	}

	/**
	* @param internalApiConnector the this.internalApiConnector to set
	*/
	public void setInternalApiConnector(APIConnector internalApiConnector) {
		this.internalApiConnector = internalApiConnector;
	}

	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	/**
	* @param rbvdR066 the this.rbvdR066 to set
	*/
	public void setRbvdR066(RBVDR066 rbvdR066) {
		this.rbvdR066 = rbvdR066;
	}

	/**
	* @param rbvdR046 the this.rbvdR046 to set
	*/
	public void setRbvdR046(RBVDR046 rbvdR046) {
		this.rbvdR046 = rbvdR046;
	}

	/**
	* @param pisdR352 the this.pisdR352 to set
	*/
	public void setPisdR352(PISDR352 pisdR352) {
		this.pisdR352 = pisdR352;
	}

	/**
	* @param rbvdR609 the this.rbvdR609 to set
	*/
	public void setRbvdR609(RBVDR609 rbvdR609) {
		this.rbvdR609 = rbvdR609;
	}

	/**
	 * @param internalApiConnectorImpersonation the this.internalApiConnectorImpersonation to set
	 */
	public void setInternalApiConnectorImpersonation(APIConnector internalApiConnectorImpersonation) {
		this.internalApiConnectorImpersonation = internalApiConnectorImpersonation;
	}

}