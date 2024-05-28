package com.bbva.rbvd.lib.r211.impl.business;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.event.GifoleEventInternal;
import com.bbva.rbvd.lib.r211.impl.pattern.factory.RimacCompanyLifeFactory;
import com.bbva.rbvd.lib.r211.impl.pattern.template.InsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceContractDAO;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class EmissionPolicyLifeBusinessImpl  {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmissionPolicyLifeBusinessImpl.class);

    private InsuranceContractBank insuranceContractBank;
    private RimacCompanyLifeFactory rimacCompanyLifeFactory;
    private IInsuranceContractDAO insuranceContractDAO;
    private MapperHelper mapperHelper;
    private GifoleEventInternal gifoleEventInternal;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    /**
     * This method is responsible for executing the emission of a policy.
     * It first generates an insurance contract and creates an insurance product based on the pre-policy process.
     * If the process is successful, it returns a ResponseLibrary object with the status OK and the policy details.
     * If a BusinessException is caught during the process, it logs the exception and returns a ResponseLibrary object with the status EWR if a rollback is needed, or ENR if not.
     * The flow process is always set as NEW_FLOW_PROCESS.
     *
     * @param requestBody The request body containing the details of the policy to be emitted.
     * @return A ResponseLibrary object containing the status of the process and the details of the emitted policy.
     * @throws BusinessException If an error occurs during the process.
     */
    public ResponseLibrary<PolicyDTO> executeEmissionPolicy(PolicyDTO requestBody) {
        LOGGER.info(" EmissionPolicyLifeBusinessImpl :: executeEmissionPolicy :: [ START ]");
        try {
             ResponseLibrary<ProcessPrePolicyDTO> processPrePolicy  = insuranceContractBank.executeGenerateInsuranceContractRoyal(requestBody);
             ResponseLibrary<ProcessPrePolicyDTO> processRimacPolicy = rimacCompanyLifeFactory.createInsuranceByProduct(processPrePolicy.getBody());
             afterProcessBusinessExecutionCross(processRimacPolicy.getBody());
             return ResponseLibrary.ResponseServiceBuilder.an()
                     .flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
                     .statusIndicatorProcess(RBVDInternalConstants.Status.OK)
                     .body(processRimacPolicy.getBody().getPolicy());
        }catch (BusinessException exception){
            LOGGER.error(" :: EmissionPolicyLifeBusinessImpl[ exceptionCode :: {} ,  message :: {}]",exception.getAdviceCode(),exception.getMessage());
            return ResponseLibrary.ResponseServiceBuilder.an()
                    .statusIndicatorProcess( exception.isHasRollback() ? RBVDInternalConstants.Status.EWR : RBVDInternalConstants.Status.ENR)
                    .flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
                    .build();
        }
    }

    /**
     * This method is responsible for executing the business logic after the policy emission process.
     * It updates the insurance contract, calculates the validity months of the product, updates the receipts, and updates the endorsement if necessary.
     * It also maps the output fields and generates an event.
     *
     * @param processPrePolicyDTO The ProcessPrePolicyDTO object containing the details of the pre-policy process.
     */
    public void afterProcessBusinessExecutionCross(ProcessPrePolicyDTO processPrePolicyDTO){
        EmisionBO rimacResponse = processPrePolicyDTO.getRimacResponse();
        PolicyDTO policy        = processPrePolicyDTO.getPolicy();
        PolicyASO policyASO        = processPrePolicyDTO.getAsoResponse();

        if(Objects.nonNull(rimacResponse)  && Objects.isNull(rimacResponse.getErrorRimacBO())) {
            LOGGER.info(" :: PolicyEmissionService | afterProcessBusinessExecutionCross rimacResponse cuotasFinanciamiento => {} ",rimacResponse.getPayload().getCuotasFinanciamiento());

            Map<String, Object> argumentsRimacContractInformation = this.mapperHelper.getRimacContractInformationLifeEasyYes(rimacResponse, policyASO.getData().getId());
            argumentsRimacContractInformation.forEach(
                    (key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicyLifeEasyYes - UpdateContract parameter {} with value: {} *****", key, value));

            boolean updatedContract = this.insuranceContractDAO.updateInsuranceContract(argumentsRimacContractInformation);
            if(!updatedContract) {
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getMessage());
                throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);
            }

            String policyNumber = rimacResponse.getPayload().getNumeroPoliza();
            String intAccountId = policyASO.getData().getId().substring(10);

            if(processPrePolicyDTO.getIsEndorsement()) {
                boolean updateEndorsement = this.insuranceContractDAO.updateEndorsementInContract(policyNumber,intAccountId);
                if(!updateEndorsement){
                    this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getAdviceCode(), RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getMessage());
                    throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE);
                }
            }

        }
        this.mapperHelper.mappingOutputFields(policy, processPrePolicyDTO.getAsoResponse(), rimacResponse, processPrePolicyDTO.getRequiredFieldsEmission());
        CreatedInsrcEventDTO createdInsrcEventDTO = this.mapperHelper.buildCreatedInsuranceEventObject(policy);
        this.gifoleEventInternal.executePutEventUpsilonGenerateLeadGifole(createdInsrcEventDTO);
    }

    public void setInsuranceContractDAO(IInsuranceContractDAO insuranceContractDAO) {
        this.insuranceContractDAO = insuranceContractDAO;
    }

    public void setMapperHelper(MapperHelper mapperHelper) {
        this.mapperHelper = mapperHelper;
    }

    public void setGifoleEventInternal(GifoleEventInternal gifoleEventInternal) {
        this.gifoleEventInternal = gifoleEventInternal;
    }

    public void setRimacCompanyLifeFactory(RimacCompanyLifeFactory rimacCompanyLifeFactory) {
        this.rimacCompanyLifeFactory = rimacCompanyLifeFactory;
    }

    public void setInsuranceContractBank(InsuranceContractBank insuranceContractBank) {
        this.insuranceContractBank = insuranceContractBank;
    }
}
