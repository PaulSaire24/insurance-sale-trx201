package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.EmissionBean;

import java.util.Map;

public class FetchRequiredDataNonVehicular implements Step {
    private final DependencyBuilder dependencyBuilder;

    public FetchRequiredDataNonVehicular(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }
    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        CustomerListASO customerList = null;
        ListBusinessesASO listBusinessesASO = null;
        Map<String, Object> quotationData = this.dependencyBuilder.getInsrncQuotationModDAO().findQuotationByQuotationId(requestBody.getQuotationId());
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

        // Set the fetched and transformed data to the process context
        processContextContractAndPolicy.getBody().setCustomerList(customerList);
        processContextContractAndPolicy.getBody().setListBusinessesASO(listBusinessesASO);
        // Execute the next step in the bank contract generation process
        stepsBankContract.execute(processContextContractAndPolicy, stepsBankContract);
    }
}
