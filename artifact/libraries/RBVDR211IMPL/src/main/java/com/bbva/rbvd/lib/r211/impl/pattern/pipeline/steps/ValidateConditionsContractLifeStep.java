package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;

import java.util.Map;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.ERROR_POLICY_ALREADY_EXISTS;

public class ValidateConditionsContractLifeStep implements Step {

    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public ValidateConditionsContractLifeStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        Boolean existContractWithQuotation = this.dependencyBuilder.getInsuranceContractDAO().findExistenceInsuranceContract(requestBody.getQuotationId());
        CustomerListASO customerList = null;
        String insuranceBusinessName;
        if(existContractWithQuotation){
            String messageErrorContractWithQuotation = String.format(ERROR_POLICY_ALREADY_EXISTS.getMessage(),requestBody.getQuotationId());
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(),messageErrorContractWithQuotation);
            throw new BusinessException(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(), ERROR_POLICY_ALREADY_EXISTS.isRollback(), ERROR_POLICY_ALREADY_EXISTS.getMessage());
        }

        Boolean enableValidationQuotationAmount = this.dependencyBuilder.getBasicProductInsuranceProperties().enableValidationQuotationAmountByProductIdAndChannelId(requestBody.getProductId(),requestBody.getSaleChannelId());
        Map<String, Object> quotationData = this.dependencyBuilder.getInsrncQuotationModDAO().findQuotationByQuotationId(requestBody.getQuotationId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateQuotationData(quotationData, requestBody.getQuotationId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateQuotationAmount(enableValidationQuotationAmount,quotationData,requestBody);

        String productCodesWithoutPartyValidation = this.dependencyBuilder.getBasicProductInsuranceProperties().obtainProductCodesWithoutPartyValidation();
        if(!productCodesWithoutPartyValidation.contains(requestBody.getProductId())){
            customerList = this.dependencyBuilder.getCustomerRBVD066InternalService().findCustomerInformationByCustomerId(requestBody.getHolder().getId());
            this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateCustomerList(customerList);
            Map<String, Object> dataInsuredQuotationFromDB = this.dependencyBuilder.getInsrncQuotationModDAO().getDataInsuredParticipantFromDB(requestBody,quotationData);
            Map<String,Object> responseQueryGetProductById = this.dependencyBuilder.getInsuranceProductDAO().findByProductId(requestBody.getProductId());
            this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateProductData(responseQueryGetProductById, requestBody.getProductId());
            insuranceBusinessName = this.dependencyBuilder.getMapperHelper().getInsuranceBusinessNameFromDB(responseQueryGetProductById);
            AgregarTerceroBO requestAddParticipants = this.dependencyBuilder.getMapperHelper().generateRequestAddParticipantsV2(insuranceBusinessName, requestBody, customerList, quotationData, dataInsuredQuotationFromDB,this.dependencyBuilder.getCustomerRBVD066InternalService());
            this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateFilledAddress(requestAddParticipants);
            String insuranceQuotationCompany = (String) quotationData.get(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue());
            AgregarTerceroBO responseValidateParticipants = this.dependencyBuilder.getPolicyServiceExternal().executeAddParticipantsService(requestAddParticipants,insuranceQuotationCompany, requestBody.getProductId(), requestBody.getTraceId());
            this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateResponseAddParticipantsService(responseValidateParticipants);
        }

        processContextContractAndPolicy.getBody().setCustomerList(customerList);
        stepsBankContract.execute(processContextContractAndPolicy, stepsBankContract);
    }


}
