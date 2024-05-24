package com.bbva.rbvd.lib.r211.impl.pattern.decorator;

import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;

public class LifeDefaultRimacDecorator extends InsuranceDecorator {

    private MapperHelper mapperHelper ;
    public LifeDefaultRimacDecorator(MapperHelper mapperHelper, Insurance decoratedInsurance) {
        super(decoratedInsurance);
        this.mapperHelper = mapperHelper;
    }

    @Override
    public ResponseLibrary<ProcessPrePolicyDTO> createPolicyOfCompany(ProcessPrePolicyDTO processPrePolicyDTO) {
        PolicyASO asoResponse = processPrePolicyDTO.getAsoResponse();
        String insuranceBusinessName = processPrePolicyDTO.getInsuranceBusinessName();
        String rimacPaymentAccount = processPrePolicyDTO.getRimacPaymentAccount();
        PolicyDTO requestBody = processPrePolicyDTO.getPolicy();
        String branchRequest = requestBody.getBank().getBranch().getId();
        RequiredFieldsEmissionDAO emissionDao = processPrePolicyDTO.getRequiredFieldsEmission();
        RelatedContractASO relatedContractASO = asoResponse.getData().getPaymentMethod().getRelatedContracts().get(0);
        EmisionBO requestEmisionLife = this.mapperHelper.generateRimacRequestLife(
                insuranceBusinessName, requestBody.getSaleChannelId(),
                asoResponse.getData().getId(),
                branchRequest	,
                ValidationUtil.getKindOfAccount(relatedContractASO),
                ValidationUtil.getAccountNumberInDatoParticular(relatedContractASO),
                asoResponse.getData().getFirstInstallment().getOperationNumber(),
                requestBody,
                rimacPaymentAccount);
        if(processPrePolicyDTO.getIsEndorsement()){
            requestEmisionLife.getPayload().setEndosatarios(processPrePolicyDTO.getEndosatarios());
        }
        processPrePolicyDTO.setRimacRequest(requestEmisionLife);
        processPrePolicyDTO.setQuotationId(emissionDao.getInsuranceCompanyQuotaId());
        processPrePolicyDTO.setTraceId(requestBody.getTraceId());
        processPrePolicyDTO.setProductId(requestBody.getProductId());
        return super.createPolicyOfCompany(processPrePolicyDTO);
    }
}
