package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps;

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
        // Fetch the policy data from the process context
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        CustomerListASO customerList = null;
        ListBusinessesASO listBusinessesASO = null;
        // Fetch and validate the frequency type
        String frequencyType = dependencyBuilder.getBasicProductInsuranceProperties().obtainFrequencyTypeByPeriodId(requestBody.getInstallmentPlan().getPeriod().getId());
        dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateFrequencyType(frequencyType);
        // Fetch and validate the payment period data
        Map<String,Object> paymentPeriodData = dependencyBuilder.getInsrncPaymentPeriodDAO().findPaymentPeriodByFrequencyType(frequencyType);
        dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validatePaymentPeriodData(paymentPeriodData, frequencyType);
        // Fetch and validate the quotation data
        Map<String, Object> quotationData = dependencyBuilder.getInsrncQuotationModDAO().findQuotationByQuotationId(requestBody.getQuotationId());
        dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateQuotationData(quotationData, requestBody.getQuotationId());
        // Fetch and validate the product data
        Map<String,Object> responseQueryGetProductById = dependencyBuilder.getInsuranceProductDAO().findByProductId(requestBody.getProductId());
        dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateProductData(responseQueryGetProductById, requestBody.getProductId());

        // Check if the product is not a vehicle insurance
        if(!RBVDProperties.INSURANCE_PRODUCT_TYPE_VEH.getValue().equalsIgnoreCase(requestBody.getProductId())){
            // Fetch and validate the customer data
            customerList = dependencyBuilder.getCustomerRBVD066InternalService().findCustomerInformationByCustomerId(requestBody.getHolder().getId());
            dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateCustomerList(customerList);
            CustomerBO customer = customerList.getData().get(0);
            // Check if the customer is a RUC customer
            if(dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().isRucCustomer(customer)){
                // Encrypt the customer ID and validate it
                String customerIdEncrypted = dependencyBuilder.getCryptoServiceInternal().encryptCustomerId(requestBody.getHolder().getId());
                dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateCustomerIdEncryption(customerIdEncrypted);
                // Fetch and validate the list of businesses
                listBusinessesASO = dependencyBuilder.getBusinessRBVD66ServiceInternal().getListBusinessesByCustomerId(customerIdEncrypted);
                dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateListBusinessesASO(listBusinessesASO);
                // Construct the person data and fill the address
                PersonaBO persona = dependencyBuilder.getMapperHelper().constructPerson(requestBody,customer,quotationData);
                String filledAddress = EmissionBean.fillAddress(customerList, persona, new StringBuilder(),requestBody.getSaleChannelId(),dependencyBuilder.getApplicationConfigurationService());
                dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateFilledAddress(filledAddress);
            }
        }

        // Check if the policy is an endorsement and set the flag
        boolean isEndorsement = CrossOperationsBusinessInsuranceContractBank.validateEndorsement(requestBody);
        processContextContractAndPolicy.getBody().setEndorsement(isEndorsement);

        // Transform the policy data to DataASO and RequiredFieldsEmissionDAO
        DataASO dataASO = PrePolicyTransfor.toDataASO(requestBody);
        RequiredFieldsEmissionDAO requiredFieldsEmissionDAO = PrePolicyTransfor.toRequiredFieldsEmissionDAO(quotationData, paymentPeriodData);

        // Set the fetched and transformed data to the process context
        processContextContractAndPolicy.getBody().setCustomerList(customerList);
        processContextContractAndPolicy.getBody().setListBusinessesASO(listBusinessesASO);
        processContextContractAndPolicy.getBody().setPolicy(requestBody);
        processContextContractAndPolicy.getBody().setRequiredFieldsEmission(requiredFieldsEmissionDAO);
        processContextContractAndPolicy.getBody().setOperationGlossaryDesc(quotationData.get(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue()).toString());
        processContextContractAndPolicy.getBody().setQuotationEmailDesc((String) quotationData.get(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue()));
        processContextContractAndPolicy.getBody().setQuotationCustomerPhoneDesc((String) quotationData.get(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue()) );
        processContextContractAndPolicy.getBody().setResponseQueryGetProductById(responseQueryGetProductById);
        processContextContractAndPolicy.getBody().setDataASO(dataASO);

        // Execute the next step in the bank contract generation process
        stepsBankContract.executeStepGenerationContract(processContextContractAndPolicy, stepsBankContract);
    }
}
