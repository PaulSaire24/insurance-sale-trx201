package com.bbva.rbvd.lib.r211.impl.pattern.decorator;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.RelatedContractDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class LifeDefaultRimacDecorator extends InsuranceDecorator {

    private MapperHelper mapperHelper ;
    public LifeDefaultRimacDecorator(MapperHelper mapperHelper, Insurance decoratedInsurance) {
        super(decoratedInsurance);
        this.mapperHelper = mapperHelper;
    }

    @Override
    public ResponseLibrary<ContextEmission> createPolicyOfCompany(ContextEmission contextEmission) {
        PolicyASO asoResponse = contextEmission.getAsoResponse();
        String insuranceBusinessName = contextEmission.getInsuranceBusinessName();
        String rimacPaymentAccount = contextEmission.getRimacPaymentAccount();
        PolicyDTO requestBody = contextEmission.getPolicy();
        String branchRequest = requestBody.getBank().getBranch().getId();
        RequiredFieldsEmissionDAO emissionDao = contextEmission.getRequiredFieldsEmission();
        RelatedContractDTO relatedContractASO = requestBody.getPaymentMethod().getRelatedContracts().get(0);

        String operationNumber = Objects.isNull(asoResponse.getData().getFirstInstallment()) ? StringUtils.EMPTY : asoResponse.getData().getFirstInstallment().getOperationNumber();
        EmisionBO requestEmisionLife = this.mapperHelper.generateRimacRequestLife(
                insuranceBusinessName, requestBody.getSaleChannelId(),
                asoResponse.getData().getId(),
                branchRequest	,
                ValidationUtil.getKindOfAccount(relatedContractASO),
                ValidationUtil.getAccountNumberInDatoParticular(relatedContractASO),
                operationNumber,
                requestBody,
                rimacPaymentAccount);
        if(contextEmission.getIsEndorsement()){
            requestEmisionLife.getPayload().setEndosatarios(contextEmission.getEndosatarios());
        }
        contextEmission.setRimacRequest(requestEmisionLife);
        contextEmission.setQuotationId(emissionDao.getInsuranceCompanyQuotaId());
        contextEmission.setTraceId(requestBody.getTraceId());
        contextEmission.setProductId(requestBody.getProductId());
        return super.createPolicyOfCompany(contextEmission);
    }
}
