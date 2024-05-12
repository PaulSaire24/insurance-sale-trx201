package com.bbva.rbvd.lib.r211.impl.pattern.decorator;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.EmissionBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.OrganizationBean;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

public class NotLifeDefaultRimacDecorator extends InsuranceDecorator {

    private final ApplicationConfigurationService applicationConfigurationService;


    public NotLifeDefaultRimacDecorator(Insurance decoratedInsurance,ApplicationConfigurationService applicationConfigurationService) {
        super(decoratedInsurance);
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public ResponseLibrary<ProcessPrePolicyDTO> createPolicyOfCompany(ProcessPrePolicyDTO processPrePolicy) {
        CustomerListASO customerList = processPrePolicy.getCustomerList();
        PolicyASO asoResponse = processPrePolicy.getAsoResponse();
        PolicyDTO requestBody = processPrePolicy.getPolicy();
        RequiredFieldsEmissionDAO emissionDao = processPrePolicy.getRequiredFieldsEmission();
        String secondDataValue = createSecondDataValue(asoResponse);
        EmisionBO rimacRequest = EmissionBean.toRequestBodyRimac(requestBody.getInspection(), secondDataValue, requestBody.getSaleChannelId(), asoResponse.getData().getId(), requestBody.getBank().getBranch().getId());
        EmisionBO generalEmissionRequest = EmissionBean.toRequestGeneralBodyRimac(rimacRequest, requestBody, processPrePolicy,customerList, applicationConfigurationService,processPrePolicy.getOperationGlossaryDesc());
        OrganizationBean.setOrganization(generalEmissionRequest,requestBody,customerList,processPrePolicy);
        processPrePolicy.setRimacRequest(generalEmissionRequest);
        processPrePolicy.setQuotationId(emissionDao.getInsuranceCompanyQuotaId());
        processPrePolicy.setTraceId(requestBody.getTraceId());
        processPrePolicy.setProductId(requestBody.getProductId());
        return super.createPolicyOfCompany(processPrePolicy);
    }

    private String createSecondDataValue(PolicyASO asoResponse) {
        RelatedContractASO relatedContract = CollectionUtils.isEmpty(asoResponse.getData().getPaymentMethod().getRelatedContracts()) ? null : asoResponse.getData().getPaymentMethod().getRelatedContracts().get(0);
        String kindOfAccount = getKindOfAccount(relatedContract);
        String accountNumber = getAccountNumberInDatoParticular(relatedContract);
        String accountCurrency = asoResponse.getData().getTotalAmount().getExchangeRate().getTargetCurrency();
        return kindOfAccount.concat("||").concat(accountNumber).concat("||").concat(accountCurrency);
    }

    private String getKindOfAccount(RelatedContractASO relatedContract){
        if(relatedContract != null && relatedContract.getProduct() != null && Objects.nonNull(relatedContract.getProduct().getId())){
            return relatedContract.getProduct().getId().equals("CARD") ? "TARJETA" : "CUENTA";
        }else{
            return "";
        }
    }

    private String getAccountNumberInDatoParticular(RelatedContractASO relatedContract){
        if(relatedContract != null && Objects.nonNull(relatedContract.getNumber())){
            int beginIndex = relatedContract.getNumber().length() - 4;
            return "***".concat(relatedContract.getNumber().substring(beginIndex));
        }else{
            return "";
        }
    }
}
