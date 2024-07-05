package com.bbva.rbvd.lib.r211.impl.business;

import com.bbva.pisd.dto.contract.search.CertifyBankCriteria;
import com.bbva.pisd.dto.insurancedao.entities.ContractEntity;
import com.bbva.pisd.lib.r226.PISDR226;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.template.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.IInsrncPaymentPeriodDAO;
import com.bbva.rbvd.lib.r211.impl.service.IInsrncQuotationModDAO;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceContractDAO;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EmissionFlowPrePolicyBusinessImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmissionFlowPrePolicyBusinessImpl.class);

    private ContractPISD201ServiceInternal contractPISD201ServiceInternal;
    private IInsuranceContractDAO insuranceContractDAO;
    private IInsrncQuotationModDAO insrncQuotationModDAO;
    private IInsrncPaymentPeriodDAO insrncPaymentPeriodDAO;
    private BasicProductInsuranceProperties basicProductInsuranceProperties;

    private CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank;
    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();


    public ResponseLibrary<ProcessPrePolicyDTO> executeFlowPreEmissionPolicy(PolicyDTO requestBody) {
        LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ START ]");
        LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ Body :: {} ]",requestBody);
        String[] certifyBank = FunctionsUtils.generateCodeToSearchContractInOracle(requestBody.getId());
        CertifyBankCriteria certifyBankCriteria = CertifyBankCriteria.CertifyBankCriteriaBuilder.an()
                .withInsuranceContractEntityId(certifyBank[0])
                .withInsuranceContractBranchId(certifyBank[1])
                .withContractFirstVerfnDigitId(certifyBank[2])
                .withContractSecondVerfnDigitId(certifyBank[3])
                .withInsrcContractIntAccountId(certifyBank[4])
                .build();

        ContractEntity contractEntity = this.insuranceContractDAO.executeFindByCertifiedBank(certifyBankCriteria);

        if(RBVDInternalConstants.CONTRACT_STATUS_ID.PEN.getValue().equalsIgnoreCase(contractEntity.getContractStatusId())){
            LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ PEN ]");
            String frequencyType = basicProductInsuranceProperties.obtainFrequencyTypeByPeriodId(requestBody.getInstallmentPlan().getPeriod().getId());
            crossOperationsBusinessInsuranceContractBank.validateFrequencyType(frequencyType);
            Map<String, Object> quotationData = insrncQuotationModDAO.findQuotationByQuotationId(requestBody.getQuotationId());
            crossOperationsBusinessInsuranceContractBank.validateQuotationData(quotationData, requestBody.getQuotationId());
            Map<String,Object> paymentPeriodData = insrncPaymentPeriodDAO.findPaymentPeriodByFrequencyType(frequencyType);
            crossOperationsBusinessInsuranceContractBank.validatePaymentPeriodData(paymentPeriodData, frequencyType);
            RequiredFieldsEmissionDAO requiredFieldsEmissionDAO = PrePolicyTransfor.toRequiredFieldsEmissionDAO(quotationData, paymentPeriodData);

            DataASO dataASO = PrePolicyTransfor.toDataASO(requestBody);
            dataASO.getFirstInstallment().setIsPaymentRequired(requestBody.getFirstInstallment().getIsPaymentRequired());
            ResponseLibrary<PolicyASO>  contractRoyalFormalized = contractPISD201ServiceInternal.generateContractHost(dataASO, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.FORMALIZED_CONTRACT_ICR3);
            ProcessPrePolicyDTO processPrePolicy = new ProcessPrePolicyDTO();
            processPrePolicy.setAsoResponse(contractRoyalFormalized.getBody());
            processPrePolicy.setPolicy(requestBody);
            processPrePolicy.setRequiredFieldsEmission(requiredFieldsEmissionDAO);
            return ResponseLibrary.ResponseServiceBuilder.an()
                    .statusIndicatorProcess(RBVDInternalConstants.Status.OK)
                    .flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
                    .body(processPrePolicy);
        }else if (RBVDInternalConstants.CONTRACT_STATUS_ID.FOR.getValue().equalsIgnoreCase(contractEntity.getContractStatusId())) {
            LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ FOR ]");
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_STATUS_CONTRACT_FOR.getAdviceCode(), RBVDInternalErrors.ERROR_STATUS_CONTRACT_FOR.getMessage());
            throw FunctionsUtils.buildValidation(RBVDInternalErrors.ERROR_STATUS_CONTRACT_FOR);
        }else if (RBVDInternalConstants.CONTRACT_STATUS_ID.BAJ.getValue().equalsIgnoreCase(contractEntity.getContractStatusId())) {
            LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ BAJ ]");
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_STATUS_CONTRACT_BAJ.getAdviceCode(), RBVDInternalErrors.ERROR_STATUS_CONTRACT_BAJ.getMessage());
            throw FunctionsUtils.buildValidation(RBVDInternalErrors.ERROR_STATUS_CONTRACT_BAJ);
        }else if (RBVDInternalConstants.CONTRACT_STATUS_ID.ANU.getValue().equalsIgnoreCase(contractEntity.getContractStatusId())) {
            LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ ANU ]");
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_STATUS_CONTRACT_ANU.getAdviceCode(), RBVDInternalErrors.ERROR_STATUS_CONTRACT_ANU.getMessage());
            throw FunctionsUtils.buildValidation(RBVDInternalErrors.ERROR_STATUS_CONTRACT_ANU);
        }
        LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ {} ]",contractEntity.getContractStatusId());
        String message = String.format(RBVDInternalErrors.ERROR_STATUS_CONTRACT_FLOW_PRE_FORMALIZATION.getMessage(),requestBody.getId(),contractEntity.getContractStatusId());
        this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_STATUS_CONTRACT_FLOW_PRE_FORMALIZATION.getAdviceCode(), message);
        throw FunctionsUtils.buildValidation(RBVDInternalErrors.ERROR_STATUS_CONTRACT_FLOW_PRE_FORMALIZATION,message);
    }

    public void setCrossOperationsBusinessInsuranceContractBank(CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank) {
        this.crossOperationsBusinessInsuranceContractBank = crossOperationsBusinessInsuranceContractBank;
    }

    public void setBasicProductInsuranceProperties(BasicProductInsuranceProperties basicProductInsuranceProperties) {
        this.basicProductInsuranceProperties = basicProductInsuranceProperties;
    }

    public void setInsrncPaymentPeriodDAO(IInsrncPaymentPeriodDAO insrncPaymentPeriodDAO) {
        this.insrncPaymentPeriodDAO = insrncPaymentPeriodDAO;
    }

    public void setInsrncQuotationModDAO(IInsrncQuotationModDAO insrncQuotationModDAO) {
        this.insrncQuotationModDAO = insrncQuotationModDAO;
    }

    public void setInsuranceContractDAO(IInsuranceContractDAO insuranceContractDAO) {
        this.insuranceContractDAO = insuranceContractDAO;
    }

    public void setContractPISD201ServiceInternal(ContractPISD201ServiceInternal contractPISD201ServiceInternal) {
        this.contractPISD201ServiceInternal = contractPISD201ServiceInternal;
    }
}
