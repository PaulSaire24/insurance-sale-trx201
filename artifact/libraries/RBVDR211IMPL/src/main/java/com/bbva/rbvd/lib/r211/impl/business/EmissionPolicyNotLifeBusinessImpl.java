package com.bbva.rbvd.lib.r211.impl.business;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.event.GifoleEventInternal;
import com.bbva.rbvd.lib.r211.impl.pattern.factory.RimacCompanyNotLifeFactory;
import com.bbva.rbvd.lib.r211.impl.pattern.template.InsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceContractDAO;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceCtrReceiptsDAO;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceReceiptBean;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class EmissionPolicyNotLifeBusinessImpl extends AbstractLibrary {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmissionPolicyNotLifeBusinessImpl.class);

    private InsuranceContractBank insuranceContractBank;
    private RimacCompanyNotLifeFactory rimacCompanyNotLifeFactory;
    private IInsuranceContractDAO insuranceContractDAO;
    private MapperHelper mapperHelper;
    private GifoleEventInternal gifoleEventInternal;
    private ApplicationConfigurationService applicationConfigurationService;

    private IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO;

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
        LOGGER.info(" EmissionPolicyBusinessImpl :: executeEmissionPolicy :: [ START ]");
        try {
             ResponseLibrary<ProcessPrePolicyDTO> processPrePolicy  = insuranceContractBank.executeGenerateInsuranceContractRoyal(requestBody);
             ResponseLibrary<ProcessPrePolicyDTO> processRimacPolicy = rimacCompanyNotLifeFactory.createInsuranceByProduct(processPrePolicy.getBody());
             afterProcessBusinessExecutionCross(processRimacPolicy.getBody());
             return ResponseLibrary.ResponseServiceBuilder.an()
                     .flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
                     .statusIndicatorProcess(RBVDInternalConstants.Status.OK)
                     .body(processRimacPolicy.getBody().getPolicy());
        }catch (BusinessException exception){
            LOGGER.error(" :: executeEmissionPolicy[ exceptionCode :: {} ,  message :: {}]",exception.getAdviceCode(),exception.getMessage());
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
        String policyNumber;
        if(Objects.nonNull(rimacResponse)) {
            LOGGER.info("RBVDR211 rimacResponse cuotasFinanciamiento => {}",rimacResponse.getPayload().getCuotasFinanciamiento());

            Map<String, Object> argumentsRimacContractInformation = this.mapperHelper.getRimacContractInformation(rimacResponse, processPrePolicyDTO.getAsoResponse().getData().getId());
            argumentsRimacContractInformation.forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - UpdateContract parameter {} with value: {} *****", key, value));

            boolean updatedContract = this.insuranceContractDAO.updateInsuranceContract(argumentsRimacContractInformation);
            if(!updatedContract) {
                this.addAdviceWithDescription(RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getMessage());
                throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);
            }

            String productsCalculateValidityMonths = this.applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt","");
            String operacionGlossaryDesc =  processPrePolicyDTO.getOperationGlossaryDesc() ;

            if (!Arrays.asList(productsCalculateValidityMonths.split(",")).contains(operacionGlossaryDesc)) {
                List<InsuranceCtrReceiptsDAO> otherReceipts = rimacResponse.getPayload().getCuotasFinanciamiento().stream().
                        filter(cuota -> cuota.getCuota().compareTo(1L) > 0).map(cuota -> InsuranceReceiptBean.generateNextReceipt(processPrePolicyDTO.getAsoResponse(), cuota)).
                        collect(toList());

                Map<String, Object>[] receiptUpdateArguments = this.mapperHelper.createSaveReceiptsArguments(otherReceipts);

                Arrays.stream(receiptUpdateArguments).forEach(receiptUpdated -> receiptUpdated.forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveReceipt parameter {} with value: {} *****", key, value)));

                boolean isMultipleInsertion = this.insuranceCtrReceiptsDAO.updateExpirationDateReceipts(receiptUpdateArguments);
                if(!isMultipleInsertion){
                    this.addAdviceWithDescription(RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getMessage());
                    throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);
                }
            }

            policyNumber = rimacResponse.getPayload().getNumeroPoliza();

            String intAccountId = processPrePolicyDTO.getAsoResponse().getData().getId().substring(10);

            if(processPrePolicyDTO.getIsEndorsement()) {
                boolean updateEndorsement = this.insuranceContractDAO.updateEndorsementInContract(policyNumber,intAccountId);
                if(!updateEndorsement){
                    this.addAdviceWithDescription(RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getAdviceCode(), RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getMessage());
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

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void setInsuranceCtrReceiptsDAO(IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO) {
        this.insuranceCtrReceiptsDAO = insuranceCtrReceiptsDAO;
    }

    public void setRimacCompanyNotLifeFactory(RimacCompanyNotLifeFactory rimacCompanyNotLifeFactory) {
        this.rimacCompanyNotLifeFactory = rimacCompanyNotLifeFactory;
    }

    public void setInsuranceContractBank(InsuranceContractBank insuranceContractBank) {
        this.insuranceContractBank = insuranceContractBank;
    }
}
