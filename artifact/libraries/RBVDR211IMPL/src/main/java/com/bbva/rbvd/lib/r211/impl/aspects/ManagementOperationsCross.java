package com.bbva.rbvd.lib.r211.impl.aspects;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessContextContractAndPolicyDTO;
import com.bbva.rbvd.lib.r211.impl.aspects.interfaces.ManagementOperation;
import com.bbva.rbvd.lib.r211.impl.event.GifoleEventInternal;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceContractDAO;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceCtrReceiptsDAO;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceReceiptBean;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;


public class ManagementOperationsCross implements ManagementOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementOperationsCross.class);
    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();
    private IInsuranceContractDAO insuranceContractDAO;
    private MapperHelper mapperHelper;
    private ApplicationConfigurationService applicationConfigurationService;
    private IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO;
    private GifoleEventInternal gifoleEventInternal;


    /**
     * This method is responsible for executing the business logic after the policy emission process.
     * It updates the insurance contract, calculates the validity months of the product, updates the receipts, and updates the endorsement if necessary.
     * It also maps the output fields and generates an event.
     *
     * @param processContextContractAndPolicyDTO The ProcessContextContractAndPolicyDTO object containing the details of the pre-policy process.
     */
    @Override
    public void afterProcessBusinessExecutionNotLifeCross(ProcessContextContractAndPolicyDTO processContextContractAndPolicyDTO){
        EmisionBO rimacResponse = processContextContractAndPolicyDTO.getRimacResponse();
        PolicyDTO policy        = processContextContractAndPolicyDTO.getPolicy();
        String policyNumber;
        if(Objects.nonNull(rimacResponse) && Objects.isNull(rimacResponse.getErrorRimacBO())) {
            LOGGER.info("RBVDR211 rimacResponse cuotasFinanciamiento => {}",rimacResponse.getPayload().getCuotasFinanciamiento());

            Map<String, Object> argumentsRimacContractInformation = this.mapperHelper.getRimacContractInformation(rimacResponse, processContextContractAndPolicyDTO.getAsoResponse().getData().getId());
            argumentsRimacContractInformation.forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - UpdateContract parameter {} with value: {} *****", key, value));

            boolean updatedContract = this.insuranceContractDAO.updateInsuranceContract(argumentsRimacContractInformation);
            if(!updatedContract) {
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getMessage());
                throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);
            }

            String productsCalculateValidityMonths = this.applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt","");
            String operacionGlossaryDesc =  processContextContractAndPolicyDTO.getOperationGlossaryDesc() ;

            if (!Arrays.asList(productsCalculateValidityMonths.split(",")).contains(operacionGlossaryDesc)) {
                List<InsuranceCtrReceiptsDAO> otherReceipts = rimacResponse.getPayload().getCuotasFinanciamiento().stream().
                        filter(cuota -> cuota.getCuota().compareTo(1L) > 0).map(cuota -> InsuranceReceiptBean.generateNextReceipt(processContextContractAndPolicyDTO.getAsoResponse(), cuota)).
                        collect(toList());

                Map<String, Object>[] receiptUpdateArguments = this.mapperHelper.createSaveReceiptsArguments(otherReceipts);

                Arrays.stream(receiptUpdateArguments).forEach(receiptUpdated -> receiptUpdated.forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveReceipt parameter {} with value: {} *****", key, value)));

                boolean isMultipleInsertion = this.insuranceCtrReceiptsDAO.updateExpirationDateReceipts(receiptUpdateArguments);
                if(!isMultipleInsertion){
                    this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getMessage());
                    throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);
                }
            }

            policyNumber = rimacResponse.getPayload().getNumeroPoliza();

            String intAccountId = processContextContractAndPolicyDTO.getAsoResponse().getData().getId().substring(10);

            if(processContextContractAndPolicyDTO.getIsEndorsement()) {
                boolean updateEndorsement = this.insuranceContractDAO.updateEndorsementInContract(policyNumber,intAccountId);
                if(!updateEndorsement){
                    this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getAdviceCode(), RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getMessage());
                    throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE);
                }
            }

        }
        this.mapperHelper.mappingOutputFields(policy, processContextContractAndPolicyDTO.getAsoResponse(), rimacResponse, processContextContractAndPolicyDTO.getRequiredFieldsEmission());
        CreatedInsrcEventDTO createdInsrcEventDTO = this.mapperHelper.buildCreatedInsuranceEventObject(policy);
        this.gifoleEventInternal.executePutEventUpsilonGenerateLeadGifole(createdInsrcEventDTO);
    }

    /**
     * This method is responsible for executing the business logic after the policy emission process.
     * It updates the insurance contract, calculates the validity months of the product, updates the receipts, and updates the endorsement if necessary.
     * It also maps the output fields and generates an event.
     *
     * @param processContextContractAndPolicyDTO The ProcessContextContractAndPolicyDTO object containing the details of the pre-policy process.
     */
    @Override
    public void afterProcessBusinessExecutionLifeCross(ProcessContextContractAndPolicyDTO processContextContractAndPolicyDTO){
        EmisionBO rimacResponse = processContextContractAndPolicyDTO.getRimacResponse();
        PolicyDTO policy        = processContextContractAndPolicyDTO.getPolicy();
        PolicyASO policyASO        = processContextContractAndPolicyDTO.getAsoResponse();

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

            if(processContextContractAndPolicyDTO.getIsEndorsement()) {
                boolean updateEndorsement = this.insuranceContractDAO.updateEndorsementInContract(policyNumber,intAccountId);
                if(!updateEndorsement){
                    this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getAdviceCode(), RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getMessage());
                    throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE);
                }
            }

        }
        this.mapperHelper.mappingOutputFields(policy, processContextContractAndPolicyDTO.getAsoResponse(), rimacResponse, processContextContractAndPolicyDTO.getRequiredFieldsEmission());
        CreatedInsrcEventDTO createdInsrcEventDTO = this.mapperHelper.buildCreatedInsuranceEventObject(policy);
        this.gifoleEventInternal.executePutEventUpsilonGenerateLeadGifole(createdInsrcEventDTO);
    }


    public void setInsuranceContractDAO(IInsuranceContractDAO insuranceContractDAO) {
        this.insuranceContractDAO = insuranceContractDAO;
    }

    public void setMapperHelper(MapperHelper mapperHelper) {
        this.mapperHelper = mapperHelper;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void setInsuranceCtrReceiptsDAO(IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO) {
        this.insuranceCtrReceiptsDAO = insuranceCtrReceiptsDAO;
    }

    public void setGifoleEventInternal(GifoleEventInternal gifoleEventInternal) {
        this.gifoleEventInternal = gifoleEventInternal;
    }
}
