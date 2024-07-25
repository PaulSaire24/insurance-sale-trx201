package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;

import java.util.Map;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.ERROR_POLICY_ALREADY_EXISTS;

public class ValidateConditionsContractStep implements Step {
    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public ValidateConditionsContractStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    @Override
    public void executeStepGenerationContract(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        Boolean existContractWithQuotation = this.dependencyBuilder.getInsuranceContractDAO().findExistenceInsuranceContract(requestBody.getQuotationId());
        if(existContractWithQuotation){
            String messageErrorContractWithQuotation = String.format(ERROR_POLICY_ALREADY_EXISTS.getMessage(),requestBody.getQuotationId());
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(),messageErrorContractWithQuotation);
            throw new BusinessException(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(), ERROR_POLICY_ALREADY_EXISTS.isRollback(), ERROR_POLICY_ALREADY_EXISTS.getMessage());
        }
        Boolean enableValidationQuotationAmount = this.dependencyBuilder.getBasicProductInsuranceProperties().enableValidationQuotationAmountByProductIdAndChannelId(requestBody.getProductId(),requestBody.getSaleChannelId());
        Map<String, Object> quotationData = this.dependencyBuilder.getInsrncQuotationModDAO().findQuotationByQuotationId(requestBody.getQuotationId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateQuotationData(quotationData, requestBody.getQuotationId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateQuotationAmount(enableValidationQuotationAmount,quotationData,requestBody);

        processContextContractAndPolicy.getBody().setQuotationData(quotationData);
        stepsBankContract.executeStepGenerationContract(processContextContractAndPolicy, stepsBankContract);
    }
}
