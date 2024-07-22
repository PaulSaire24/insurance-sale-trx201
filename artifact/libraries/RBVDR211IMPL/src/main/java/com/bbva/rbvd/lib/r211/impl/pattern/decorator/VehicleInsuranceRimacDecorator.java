package com.bbva.rbvd.lib.r211.impl.pattern.decorator;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.RelatedContractDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessContextContractAndPolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.EmissionBean;

import java.util.Objects;

public class VehicleInsuranceRimacDecorator extends InsuranceDecorator {

    public VehicleInsuranceRimacDecorator(Insurance decoratedInsurance) {
        super(decoratedInsurance);
    }

    @Override
    public ResponseLibrary<ProcessContextContractAndPolicyDTO> createPolicyOfCompany(ProcessContextContractAndPolicyDTO processContextContractAndPolicyDTO) {
        PolicyDTO requestBody = processContextContractAndPolicyDTO.getPolicy();
        PolicyASO asoResponse = processContextContractAndPolicyDTO.getAsoResponse();
        RequiredFieldsEmissionDAO emissionDao = processContextContractAndPolicyDTO.getRequiredFieldsEmission();
        String secondDataValue = createSecondDataValue(requestBody);
        EmisionBO rimacRequest = EmissionBean.toRequestBodyRimac(requestBody.getInspection(), secondDataValue, requestBody.getSaleChannelId(), asoResponse.getData().getId(), requestBody.getBank().getBranch().getId());
        if(processContextContractAndPolicyDTO.getIsEndorsement()){
            String endosatarioRuc = requestBody.getParticipants().get(1).getIdentityDocument().getNumber();
            Double endosatarioPorcentaje = requestBody.getParticipants().get(1).getBenefitPercentage();
            rimacRequest.getPayload().setEndosatario(new EndosatarioBO(endosatarioRuc, endosatarioPorcentaje.intValue()));
        }
        processContextContractAndPolicyDTO.setRimacRequest(rimacRequest);
        processContextContractAndPolicyDTO.setQuotationId(emissionDao.getInsuranceCompanyQuotaId());
        processContextContractAndPolicyDTO.setTraceId(requestBody.getTraceId());
        processContextContractAndPolicyDTO.setProductId(requestBody.getProductId());
        return super.createPolicyOfCompany(processContextContractAndPolicyDTO);
    }

    private String createSecondDataValue(PolicyDTO requestBody) {
        RelatedContractDTO relatedContract = requestBody.getPaymentMethod().getRelatedContracts().get(0);
        String kindOfAccount = getKindOfAccount(relatedContract);
        String accountNumber = getAccountNumberInDatoParticular(relatedContract);
        String accountCurrency = requestBody.getTotalAmount().getCurrency();
        return kindOfAccount.concat("||").concat(accountNumber).concat("||").concat(accountCurrency);
    }

    private String getKindOfAccount(RelatedContractDTO relatedContract){
        if(relatedContract != null && relatedContract.getProduct() != null && Objects.nonNull(relatedContract.getProduct().getId())){
            return relatedContract.getProduct().getId().equals("CARD") ? "TARJETA" : "CUENTA";
        }else{
            return "";
        }
    }

    private String getAccountNumberInDatoParticular(RelatedContractDTO relatedContract){
        if(relatedContract != null && Objects.nonNull(relatedContract.getNumber())){
            int beginIndex = relatedContract.getNumber().length() - 4;
            return "***".concat(relatedContract.getNumber().substring(beginIndex));
        }else{
            return "";
        }
    }


}
