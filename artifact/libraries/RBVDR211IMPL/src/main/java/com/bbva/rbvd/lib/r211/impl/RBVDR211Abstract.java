package com.bbva.rbvd.lib.r211.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.ksmk.lib.r002.KSMKR002;
import com.bbva.pbtq.lib.r002.PBTQR002;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r021.PISDR021;
import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.lib.r211.RBVDR211;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class RBVDR211Abstract extends AbstractLibrary implements RBVDR211 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected RBVDR201 rbvdR201;

	protected PISDR021 pisdR021;

	protected PISDR012 pisdR012;

	protected KSMKR002 ksmkR002;

	protected PBTQR002 pbtqR002;

	protected MapperHelper mapperHelper;


	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	/**
	* @param rbvdR201 the this.rbvdR201 to set
	*/
	public void setRbvdR201(RBVDR201 rbvdR201) {
		this.rbvdR201 = rbvdR201;
	}

	/**
	* @param pisdR021 the this.pisdR021 to set
	*/
	public void setPisdR021(PISDR021 pisdR021) {
		this.pisdR021 = pisdR021;
	}

	/**
	* @param pisdR012 the this.pisdR012 to set
	*/
	public void setPisdR012(PISDR012 pisdR012) {
		this.pisdR012 = pisdR012;
	}

	/**
	* @param ksmkR002 the this.ksmkR002 to set
	*/
	public void setKsmkR002(KSMKR002 ksmkR002) {
		this.ksmkR002 = ksmkR002;
	}

	/**
	* @param pbtqR002 the this.pbtqR002 to set
	*/
	public void setPbtqR002(PBTQR002 pbtqR002) {
		this.pbtqR002 = pbtqR002;
	}

	public void setMapperHelper(MapperHelper mapperHelper) {this.mapperHelper = mapperHelper;}

}