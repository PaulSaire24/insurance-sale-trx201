package com.bbva.rbvd.lib.r201.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.elara.utility.api.connector.APIConnectorBuilder;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.lib.r047.RBVDR047;
import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.lib.r201.util.RimacUrlForker;
import com.bbva.rbvd.lib.r602.RBVDR602;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class RBVDR201Abstract extends AbstractLibrary implements RBVDR201 {

	protected APIConnector externalApiConnector;

	protected APIConnectorBuilder apiConnectorBuilder;

	protected APIConnector internalApiConnector;

	protected ApplicationConfigurationService applicationConfigurationService;

	protected APIConnector internalApiConnectorImpersonation;

	protected PISDR014 pisdR014;

	protected RBVDR047 rbvdR047;

	protected RBVDR602 rbvdR602;

	protected RimacUrlForker rimacUrlForker;


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
	* @param pisdR014 the this.pisdR014 to set
	*/
	public void setPisdR014(PISDR014 pisdR014) {
		this.pisdR014 = pisdR014;
	}

	/**
	* @param rbvdR047 the this.rbvdR047 to set
	*/
	public void setRbvdR047(RBVDR047 rbvdR047) {
		this.rbvdR047 = rbvdR047;
	}

	/**
	* @param rbvdR602 the this.rbvdR602 to set
	*/
	public void setRbvdR602(RBVDR602 rbvdR602) {
		this.rbvdR602 = rbvdR602;
	}

	public void setInternalApiConnectorImpersonation(APIConnector internalApiConnectorImpersonation) {
		this.internalApiConnectorImpersonation = internalApiConnectorImpersonation;
	}

	public void setRimacUrlForker(RimacUrlForker rimacUrlForker) { this.rimacUrlForker = rimacUrlForker; }


}