package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

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

public class PartyValidationStep implements Step {
    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public PartyValidationStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }


    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {

        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        Map<String, Object> quotationData = processContextContractAndPolicy.getBody().getQuotationData();
        CustomerListASO customerList = null;
        String insuranceBusinessName;
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
