package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps;

import com.bbva.pisd.dto.contract.search.CertifyBankCriteria;
import com.bbva.pisd.dto.insurancedao.entities.ContractEntity;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceContractBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

public class ValidateConditionsContractFlowPreEmission implements Step {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateFormalizationOracleFlowPreEmission.class);

    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public ValidateConditionsContractFlowPreEmission(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        Boolean enableValidationQuotationAmount = this.dependencyBuilder.getBasicProductInsuranceProperties().enableValidationQuotationAmountByProductIdAndChannelId(requestBody.getProductId(),requestBody.getSaleChannelId());
        Map<String, Object> quotationData = this.dependencyBuilder.getInsrncQuotationModDAO().findQuotationByQuotationId(requestBody.getQuotationId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateQuotationData(quotationData, requestBody.getQuotationId());
        this.dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateQuotationAmount(enableValidationQuotationAmount,quotationData,requestBody);

        String[] certifyBank = FunctionsUtils.generateCodeToSearchContractInOracle(requestBody.getId());
        CertifyBankCriteria certifyBankCriteria = CertifyBankCriteria.CertifyBankCriteriaBuilder.an()
                .withInsuranceContractEntityId(certifyBank[0])
                .withInsuranceContractBranchId(certifyBank[1])
                .withContractFirstVerfnDigitId(certifyBank[2])
                .withContractSecondVerfnDigitId(certifyBank[3])
                .withInsrcContractIntAccountId(certifyBank[4])
                .build();

        ContractEntity contractEntity = this.dependencyBuilder.getInsuranceContractDAO().executeFindByCertifiedBank(certifyBankCriteria);

        if(RBVDInternalConstants.CONTRACT_STATUS_ID.PEN.getValue().equalsIgnoreCase(contractEntity.getContractStatusId())){
            LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy [ SUCCESSFUL ] :: [ PEN ]");

            String officeCode = dependencyBuilder.getBasicProductInsuranceProperties().obtainOfficeTelemarketingCode();
            String channelId = officeCode.equals(requestBody.getBank().getBranch().getId()) ? RBVDInternalConstants.Channel.TELEMARKETING_CODE : requestBody.getSaleChannelId();
            requestBody = PrePolicyTransfor.toMapBranchAndSaleChannelIdOfficial(requestBody.getBank().getBranch().getId(), channelId, requestBody);
            dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().handleNonDigitalSale(requestBody);

            BigDecimal totalInstallments = BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments());
            if (requestBody.getFirstInstallment().getIsPaymentRequired()) {
                totalInstallments = totalInstallments.subtract(BigDecimal.ONE);
            }

            InsuranceContractDAO contractDao = InsuranceContractBean.toInsuranceContractDAO(
                    processContextContractAndPolicy.getBody().getPolicy(),
                    processContextContractAndPolicy.getBody().getRequiredFieldsEmission(),
                    processContextContractAndPolicy.getBody().getPolicy().getId(),
                    processContextContractAndPolicy.getBody().getIsEndorsement(),
                    totalInstallments
            );

            processContextContractAndPolicy.getBody().setContractDao(contractDao);
            stepsBankContract.execute(processContextContractAndPolicy, stepsBankContract);
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
        }else if(!RBVDInternalConstants.CONTRACT_STATUS_ID.PEN.getValue().equalsIgnoreCase(contractEntity.getContractStatusId())){
            LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ {} ]",contractEntity.getContractStatusId());
            String message = String.format(RBVDInternalErrors.ERROR_STATUS_CONTRACT_FLOW_PRE_FORMALIZATION.getMessage(),requestBody.getId(),contractEntity.getContractStatusId());
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_STATUS_CONTRACT_FLOW_PRE_FORMALIZATION.getAdviceCode(), message);
            throw FunctionsUtils.buildValidation(RBVDInternalErrors.ERROR_STATUS_CONTRACT_FLOW_PRE_FORMALIZATION,message);
        }


    }
}
