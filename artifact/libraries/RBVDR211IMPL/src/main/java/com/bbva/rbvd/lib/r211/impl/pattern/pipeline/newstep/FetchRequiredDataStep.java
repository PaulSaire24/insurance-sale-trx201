package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.EmissionBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;

import java.util.Map;

public class FetchRequiredDataStep implements Step {
    private final DependencyBuilder dependencyBuilder;

    public FetchRequiredDataStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    /**
     * This method is responsible for executing the generation of a contract step.
     * It fetches and validates necessary data, checks if the product is not a vehicle insurance,
     * and if the customer is a RUC customer, it fetches and validates more data.
     * It also sets the endorsement flag, transforms the policy data to DataASO and RequiredFieldsEmissionDAO,
     * and sets these data along with other data to the process context.
     * Finally, it executes the next step in the bank contract generation process.
     *
     * @param processContextContractAndPolicy The process context for contract and policy generation.
     * @param stepsBankContract The next step in the bank contract generation process.
     */
    @Override
    public void executeStepGenerationContract(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();

        String frequencyType = this.dependencyBuilder.getBasicProductInsuranceProperties().obtainFrequencyTypeByPeriodId(requestBody.getInstallmentPlan().getPeriod().getId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateFrequencyType(frequencyType);
        Map<String,Object> paymentPeriodData = this.dependencyBuilder.getInsrncPaymentPeriodDAO().findPaymentPeriodByFrequencyType(frequencyType);
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validatePaymentPeriodData(paymentPeriodData, frequencyType);
        Map<String, Object> quotationData = this.dependencyBuilder.getInsrncQuotationModDAO().findQuotationByQuotationId(requestBody.getQuotationId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateQuotationData(quotationData, requestBody.getQuotationId());
        Map<String,Object> responseQueryGetProductById = this.dependencyBuilder.getInsuranceProductDAO().findByProductId(requestBody.getProductId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateProductData(responseQueryGetProductById, requestBody.getProductId());



        // Check if the policy is an endorsement and set the flag
        boolean isEndorsement = CrossOperationsBusinessInsuranceContractBank.validateEndorsement(requestBody);
        processContextContractAndPolicy.getBody().setEndorsement(isEndorsement);

        // Transform the policy data to DataASO and RequiredFieldsEmissionDAO
        DataASO dataASO = PrePolicyTransfor.toDataASO(requestBody);
        RequiredFieldsEmissionDAO requiredFieldsEmissionDAO = PrePolicyTransfor.toRequiredFieldsEmissionDAO(quotationData, paymentPeriodData);


        processContextContractAndPolicy.getBody().setPolicy(requestBody);
        processContextContractAndPolicy.getBody().setRequiredFieldsEmission(requiredFieldsEmissionDAO);
        processContextContractAndPolicy.getBody().setOperationGlossaryDesc(quotationData.get(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue()).toString());
        processContextContractAndPolicy.getBody().setQuotationEmailDesc((String) quotationData.get(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue()));
        processContextContractAndPolicy.getBody().setQuotationCustomerPhoneDesc((String) quotationData.get(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue()) );
        processContextContractAndPolicy.getBody().setRimacPaymentAccount((String) quotationData.get(RBVDProperties.FIELD_ACCOUNT_ID.getValue()) );
        processContextContractAndPolicy.getBody().setResponseQueryGetProductById(responseQueryGetProductById);
        processContextContractAndPolicy.getBody().setDataASO(dataASO);

        // Execute the next step in the bank contract generation process
        stepsBankContract.executeStepGenerationContract(processContextContractAndPolicy, stepsBankContract);
    }
}
