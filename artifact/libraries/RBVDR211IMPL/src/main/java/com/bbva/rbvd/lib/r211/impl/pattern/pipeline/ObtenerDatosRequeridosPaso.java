package com.bbva.rbvd.lib.r211.impl.pattern.pipeline;


import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.template.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.*;
import com.bbva.rbvd.lib.r211.impl.service.api.BusinessRBVD66ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CryptoServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.EmissionBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

import java.util.Map;

public class ObtenerDatosRequeridosPaso implements PasoPipeline{

    private IInsrncQuotationModDAO insrncQuotationModDAO;
    private IInsrncPaymentPeriodDAO insrncPaymentPeriodDAO;
    private BasicProductInsuranceProperties basicProductInsuranceProperties;
    private CryptoServiceInternal cryptoServiceInternal;
    private CustomerRBVD066InternalService customerRBVD066InternalService;
    private IInsuranceProductDAO insuranceProductDAO;
    private BusinessRBVD66ServiceInternal businessRBVD66ServiceInternal;
    private ApplicationConfigurationService applicationConfigurationService;

    private MapperHelper mapperHelper;
    private CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank;

    @Override
    public void ejecutar(ResponseLibrary<ProcessPrePolicyDTO> contexto, PasoPipeline siguiente) {
        CustomerListASO customerList = null;
        ListBusinessesASO listBusinessesASO = null;
        PolicyDTO requestBody = contexto.getBody().getPolicy();
        String frequencyType = basicProductInsuranceProperties.obtainFrequencyTypeByPeriodId(requestBody.getInstallmentPlan().getPeriod().getId());
        crossOperationsBusinessInsuranceContractBank.validateFrequencyType(frequencyType);
        Map<String,Object> paymentPeriodData = insrncPaymentPeriodDAO.findPaymentPeriodByFrequencyType(frequencyType);
        crossOperationsBusinessInsuranceContractBank.validatePaymentPeriodData(paymentPeriodData, frequencyType);
        Map<String, Object> quotationData = insrncQuotationModDAO.findQuotationByQuotationId(requestBody.getQuotationId());
        crossOperationsBusinessInsuranceContractBank.validateQuotationData(quotationData, requestBody.getQuotationId());
        Map<String,Object> responseQueryGetProductById = insuranceProductDAO.findByProductId(requestBody.getProductId());
        crossOperationsBusinessInsuranceContractBank.validateProductData(responseQueryGetProductById, requestBody.getProductId());

        if(!RBVDProperties.INSURANCE_PRODUCT_TYPE_VEH.getValue().equalsIgnoreCase(requestBody.getProductId())){
            customerList = customerRBVD066InternalService.findCustomerInformationByCustomerId(requestBody.getHolder().getId());
            crossOperationsBusinessInsuranceContractBank.validateCustomerList(customerList);
            CustomerBO customer = customerList.getData().get(0);
            if(crossOperationsBusinessInsuranceContractBank.isRucCustomer(customer)){
                String customerIdEncrypted = cryptoServiceInternal.encryptCustomerId(requestBody.getHolder().getId());
                crossOperationsBusinessInsuranceContractBank.validateCustomerIdEncryption(customerIdEncrypted);
                listBusinessesASO = businessRBVD66ServiceInternal.getListBusinessesByCustomerId(customerIdEncrypted);
                crossOperationsBusinessInsuranceContractBank.validateListBusinessesASO(listBusinessesASO);
                PersonaBO persona = mapperHelper.constructPerson(requestBody,customer,quotationData);
                String filledAddress = EmissionBean.fillAddress(customerList, persona, new StringBuilder(),requestBody.getSaleChannelId(),applicationConfigurationService);
                crossOperationsBusinessInsuranceContractBank.validateFilledAddress(filledAddress);
            }
        }
        RequiredFieldsEmissionDAO requiredFieldsEmissionDAO = PrePolicyTransfor.toRequiredFieldsEmissionDAO(quotationData, paymentPeriodData);
        contexto.getBody().setPolicy(requestBody);
        contexto.getBody().setCustomerList(customerList);
        contexto.getBody().setListBusinessesASO(listBusinessesASO);
        contexto.getBody().setRequiredFieldsEmission(requiredFieldsEmissionDAO);
        contexto.getBody().setOperationGlossaryDesc(quotationData.get(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue()).toString());
        contexto.getBody().setQuotationEmailDesc((String) quotationData.get(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue()));
        contexto.getBody().setQuotationCustomerPhoneDesc((String) quotationData.get(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue()) );
        contexto.getBody().setRimacPaymentAccount((String) quotationData.get(RBVDProperties.FIELD_ACCOUNT_ID.getValue()) );
        contexto.getBody().setResponseQueryGetProductById(responseQueryGetProductById);
        siguiente.ejecutar(contexto, siguiente);
    }

}
