package com.bbva.rbvd.lib.r211.impl.pattern.decorator;

import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.EmissionBean;

import java.util.Objects;

public class VehicleInsuranceRimacDecorator extends InsuranceDecorator {

    public VehicleInsuranceRimacDecorator(Insurance decoratedInsurance) {
        super(decoratedInsurance);
    }

    @Override
    public ResponseLibrary<ProcessPrePolicyDTO> createPolicyOfCompany(ProcessPrePolicyDTO processPrePolicyDTO) {
        PolicyDTO requestBody = processPrePolicyDTO.getPolicy();
        PolicyASO asoResponse = processPrePolicyDTO.getAsoResponse();
        RequiredFieldsEmissionDAO emissionDao = processPrePolicyDTO.getRequiredFieldsEmission();
        String secondDataValue = createSecondDataValue(asoResponse);
        EmisionBO rimacRequest = EmissionBean.toRequestBodyRimac(requestBody.getInspection(), secondDataValue, requestBody.getSaleChannelId(), asoResponse.getData().getId(), requestBody.getBank().getBranch().getId());
        if(processPrePolicyDTO.getIsEndorsement()){
            String endosatarioRuc = requestBody.getParticipants().get(1).getIdentityDocument().getNumber();
            Double endosatarioPorcentaje = requestBody.getParticipants().get(1).getBenefitPercentage();
            rimacRequest.getPayload().setEndosatario(new EndosatarioBO(endosatarioRuc, endosatarioPorcentaje.intValue()));
        }
        processPrePolicyDTO.setRimacRequest(rimacRequest);
        processPrePolicyDTO.setQuotationId(emissionDao.getInsuranceCompanyQuotaId());
        processPrePolicyDTO.setTraceId(requestBody.getTraceId());
        processPrePolicyDTO.setProductId(requestBody.getProductId());
        return super.createPolicyOfCompany(processPrePolicyDTO);
    }

    private String createSecondDataValue(PolicyASO asoResponse) {
        RelatedContractASO relatedContract = asoResponse.getData().getPaymentMethod().getRelatedContracts().get(0);
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
