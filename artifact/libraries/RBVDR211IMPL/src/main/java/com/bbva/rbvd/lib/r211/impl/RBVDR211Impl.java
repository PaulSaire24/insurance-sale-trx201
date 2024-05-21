package com.bbva.rbvd.lib.r211.impl;

import com.bbva.apx.exception.business.BusinessException;

import com.bbva.ksmk.dto.caas.CredentialsDTO;
import com.bbva.ksmk.dto.caas.InputDTO;
import com.bbva.ksmk.dto.caas.OutputDTO;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;

import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;

import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;

import com.bbva.pisd.dto.insurancedao.entities.QuotationEntity;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.CuotaFinancimientoBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.OrganizacionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDTO;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;

import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.BusinessAgentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PromoterDTO;

import com.bbva.rbvd.dto.insrncsale.utils.ContactTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;

import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.business.EmissionPolicyLegacyBusinessImpl;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.nio.charset.StandardCharsets;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import static org.springframework.util.CollectionUtils.isEmpty;


public class RBVDR211Impl extends RBVDR211Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);

	/**
	 * Instance of BasicProductInsuranceProperties.
	 * This property is used to access the configuration properties related to basic insurance products.
	 */
	private BasicProductInsuranceProperties basicProductInsuranceProperties;


	/**
	 * This method is responsible for executing the business logic for the emission of non-life insurance policies.
	 * It first checks if all products are enabled for emission or if the specific product in the request is enabled for emission.
	 * If so, it delegates the emission process to the 'emissionPolicyNotLifeBusiness' service.
	 * If not, it falls back to the legacy emission process.
	 *
	 * @param requestBody The request body containing the details of the policy to be emitted.
	 * @return A ResponseLibrary object containing the details of the emitted policy.
	 */
	@Override
	public ResponseLibrary<PolicyDTO> executeEmissionPolicyNotLifeFlowNew(PolicyDTO requestBody) {
		LOGGER.info(" :: executeBusinessLogicEmissionPolicyFlowNew :: [ START ]");
		LOGGER.info(" :: executeBusinessLogicEmissionPolicyFlowNew :: [ PolicyDTO :: {} ]",requestBody);
		if(basicProductInsuranceProperties.enabledAllProductsEmissionRoyal2_0() || basicProductInsuranceProperties.enabledFlowEmissionRoyal2_0ByProduct(requestBody.getProductId())) {
			ResponseLibrary<PolicyDTO> responseLibrary = this.emissionPolicyNotLifeBusinessImpl.executeEmissionPolicy(requestBody);
			LOGGER.info(" :: executeBusinessLogicEmissionPolicyFlowNew :: [ PolicyDTO :: {} ]",requestBody);
			return responseLibrary;

		}

		PolicyDTO policyContractBankCompany = this.executeEmissionPrePolicyLegacy(requestBody);
		return ResponseLibrary.ResponseServiceBuilder
				.an().flowProcess(RBVDInternalConstants.FlowProcess.LEGACY_FLOW_PROCESS).
				body(policyContractBankCompany);
	}

	/**
	 * This method is responsible for executing the legacy emission process for a policy.
	 * It creates an instance of EmissionPolicyLegacyBusinessImpl and delegates the emission process to it.
	 *
	 * @param requestBody The request body containing the details of the policy to be emitted.
	 * @return A PolicyDTO object containing the details of the emitted policy.
	 */
	@Override
	public PolicyDTO executeEmissionPrePolicyLegacy(PolicyDTO requestBody) {
		EmissionPolicyLegacyBusinessImpl emissionPolicyLegacyBusiness = new EmissionPolicyLegacyBusinessImpl(this.applicationConfigurationService, this.rbvdR201,this.pisdR012,this.ksmkR002,this.pisdR401,this.pisdR350,this.mapperHelper,this.pisdR601);
        return emissionPolicyLegacyBusiness.executeEmissionPrePolicyNotLifeLegacy(requestBody);
	}

	/**
	 * This method is responsible for executing the business logic for the emission of life insurance policies.
	 * It delegates the emission process to the 'executeBusinessLogicEmissionPrePolicyLifeProductLegacy' method.
	 * The result is then wrapped in a ResponseLibrary object and returned.
	 *
	 * @param requestBody The request body containing the details of the policy to be emitted.
	 * @return A ResponseLibrary object containing the details of the emitted policy.
	 */
	@Override
	public ResponseLibrary<PolicyDTO> executeEmissionPrePolicyLifeProductFlowNew(PolicyDTO requestBody) {
		LOGGER.info(" :: executeEmissionPrePolicyLifeProductFlowNew :: [ START ]");
		LOGGER.info(" :: executeEmissionPrePolicyLifeProductFlowNew :: [ PolicyDTO :: {} ]",requestBody);
		if(basicProductInsuranceProperties.enabledAllProductsEmissionRoyal2_0Life() || basicProductInsuranceProperties.enabledFlowEmissionRoyal2_0ByProductLife(requestBody.getProductId())) {
			ResponseLibrary<PolicyDTO> responseLibrary = this.emissionPolicyLifeBusinessImpl.executeEmissionPolicy(requestBody);
			LOGGER.info(" :: executeEmissionPrePolicyLifeProductFlowNew - executeEmissionPolicy :: [ PolicyDTO :: {} ]",requestBody);
			return responseLibrary;

		}
		PolicyDTO policyContractBankCompany = this.executeEmissionPrePolicyLifeProductLegacy(requestBody);
		return ResponseLibrary.ResponseServiceBuilder
				.an().flowProcess(RBVDInternalConstants.FlowProcess.LEGACY_FLOW_PROCESS).
				body(policyContractBankCompany);
	}

	/**
	 * This method is responsible for executing the legacy emission process for a life insurance policy.
	 * It creates an instance of EmissionPolicyLegacyBusinessImpl and delegates the emission process to it.
	 *
	 * @param requestBody The request body containing the details of the life insurance policy to be emitted.
	 * @return A PolicyDTO object containing the details of the emitted life insurance policy.
	 */
	@Override
	public PolicyDTO executeEmissionPrePolicyLifeProductLegacy(PolicyDTO requestBody){
		EmissionPolicyLegacyBusinessImpl emissionPolicyLegacyBusiness = new EmissionPolicyLegacyBusinessImpl(this.applicationConfigurationService, this.rbvdR201,this.pisdR012,this.ksmkR002,this.pisdR401,this.pisdR350,this.mapperHelper,this.pisdR601);
		return emissionPolicyLegacyBusiness.executeEmissionPrePolicyLifeProductLegacy(requestBody);
	}

	/**
	 * @param basicProductInsuranceProperties the this.basicProductInsuranceProperties to set
	 */
	public void setBasicProductInsuranceProperties(BasicProductInsuranceProperties basicProductInsuranceProperties) {
		this.basicProductInsuranceProperties = basicProductInsuranceProperties;
	}

}
