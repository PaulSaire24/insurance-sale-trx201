package com.bbva.rbvd.lib.r211.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.aspects.interfaces.ManagementOperation;
import com.bbva.rbvd.lib.r211.impl.business.EmissionPolicyLegacyBusinessImpl;
import com.bbva.rbvd.lib.r211.impl.pattern.factory.interfaces.InsuranceCompanyFactory;
import com.bbva.rbvd.lib.r211.impl.pattern.facttory.PipelineFactory;
import com.bbva.rbvd.lib.r211.impl.pattern.facttory.impl.FactoryProductImpl;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.Pipeline;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RBVDR211Impl extends RBVDR211Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);
	private InsuranceCompanyFactory insuranceCompanyFactory;
	private ManagementOperation managementOperationsCross;

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

	@Override
	public ResponseLibrary<PolicyDTO> executeEmissionPipeline(PolicyDTO requestBody) {
		ResponseLibrary<ProcessPrePolicyDTO> cotexto = ResponseLibrary.ResponseServiceBuilder.an().body(new ProcessPrePolicyDTO());
		try {
			cotexto.getBody().setPolicy(requestBody);
			PipelineFactory pipelineFactory = FactoryProductImpl.createFactory(requestBody.getProductId());
			Pipeline pipeline = pipelineFactory.crearPipeline(requestBody.getSaleChannelId(), requestBody.getId());
			pipeline.ejecutar(cotexto);
			ResponseLibrary<ProcessPrePolicyDTO> contractRoyalAndPolicyGenerated = insuranceCompanyFactory.createInsuranceByProduct(cotexto.getBody());
			managementOperationsCross.afterProcessBusinessExecutionNotLifeCross(contractRoyalAndPolicyGenerated.getBody());

			return ResponseLibrary.ResponseServiceBuilder.an()
					.flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
					.statusIndicatorProcess(RBVDInternalConstants.Status.OK)
					.body(contractRoyalAndPolicyGenerated.getBody().getPolicy());
		}catch (BusinessException exception){
			LOGGER.error(" :: executeEmissionPolicy[ exceptionCode :: {} ,  message :: {}]",exception.getAdviceCode(),exception.getMessage());
			if(RBVDInternalErrors.ERROR_GENERIC_APX_IN_CALLED_RIMAC.getAdviceCode().equalsIgnoreCase(exception.getAdviceCode())){
				this.mapperHelper.mappingOutputFields(cotexto.getBody().getPolicy(), cotexto.getBody().getAsoResponse(), cotexto.getBody().getRimacResponse(), cotexto.getBody().getRequiredFieldsEmission());
				return ResponseLibrary.ResponseServiceBuilder.an()
						.statusIndicatorProcess(RBVDInternalConstants.Status.OK)
						.flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
						.body(cotexto.getBody().getPolicy());
			}
			return ResponseLibrary.ResponseServiceBuilder.an()
					.statusIndicatorProcess( exception.isHasRollback() ? RBVDInternalConstants.Status.EWR : RBVDInternalConstants.Status.ENR)
					.flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
					.build();
		}
	}

	/**
	 * @param basicProductInsuranceProperties the this.basicProductInsuranceProperties to set
	 */
	public void setBasicProductInsuranceProperties(BasicProductInsuranceProperties basicProductInsuranceProperties) {
		this.basicProductInsuranceProperties = basicProductInsuranceProperties;
	}

	public void setInsuranceCompanyFactory(InsuranceCompanyFactory insuranceCompanyFactory) {
		this.insuranceCompanyFactory = insuranceCompanyFactory;
	}

	public void setManagementOperationsCross(ManagementOperation managementOperationsCross) {
		this.managementOperationsCross = managementOperationsCross;
	}
}
