package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;

import java.util.Map;

public class FetchRequiredDataLifeStep implements Step {
    private final DependencyBuilder dependencyBuilder;

    public FetchRequiredDataLifeStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }


    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        String frequencyType = this.dependencyBuilder.getBasicProductInsuranceProperties().obtainFrequencyTypeByPeriodId(requestBody.getInstallmentPlan().getPeriod().getId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateFrequencyType(frequencyType);
        Map<String,Object> paymentPeriodData = this.dependencyBuilder.getInsrncPaymentPeriodDAO().findPaymentPeriodByFrequencyType(frequencyType);
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validatePaymentPeriodData(paymentPeriodData, frequencyType);
        Map<String, Object> quotationData = this.dependencyBuilder.getInsrncQuotationModDAO().findQuotationByQuotationId(requestBody.getQuotationId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateQuotationData(quotationData, requestBody.getQuotationId());
        Map<String,Object> responseQueryGetProductById = this.dependencyBuilder.getInsuranceProductDAO().findByProductId(requestBody.getProductId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateProductData(responseQueryGetProductById, requestBody.getProductId());
        RequiredFieldsEmissionDAO requiredFieldsEmissionDAO = PrePolicyTransfor.toRequiredFieldsEmissionDAO(quotationData, paymentPeriodData);

        processContextContractAndPolicy.getBody().setPolicy(requestBody);
        processContextContractAndPolicy.getBody().setRequiredFieldsEmission(requiredFieldsEmissionDAO);
        processContextContractAndPolicy.getBody().setOperationGlossaryDesc(quotationData.get(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue()).toString());
        processContextContractAndPolicy.getBody().setQuotationEmailDesc((String) quotationData.get(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue()));
        processContextContractAndPolicy.getBody().setQuotationCustomerPhoneDesc((String) quotationData.get(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue()) );
        processContextContractAndPolicy.getBody().setRimacPaymentAccount((String) quotationData.get(RBVDProperties.FIELD_ACCOUNT_ID.getValue()) );
        processContextContractAndPolicy.getBody().setResponseQueryGetProductById(responseQueryGetProductById);

        stepsBankContract.execute(processContextContractAndPolicy, stepsBankContract);
    }
}
