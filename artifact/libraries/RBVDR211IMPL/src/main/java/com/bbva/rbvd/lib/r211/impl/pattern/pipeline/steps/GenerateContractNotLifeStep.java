package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceContractBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceReceiptBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceContractMap;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceReceiptMap;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class GenerateContractNotLifeStep implements Step {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateContractNotLifeStep.class);

    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public GenerateContractNotLifeStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    @Override
    public void executeStepGenerationContract(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        RequiredFieldsEmissionDAO emissionDao = processContextContractAndPolicy.getBody().getRequiredFieldsEmission();

        Date startDate = dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateStartDate(requestBody);
        Boolean isPaymentRequired = dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().evaluateRequiredPayment(startDate);
        requestBody = PrePolicyTransfor.toIsPaymentRequired(requestBody,isPaymentRequired);
        DataASO dataASO = PrePolicyTransfor.toDataASO(requestBody);

        ResponseLibrary<PolicyASO> responseService = dependencyBuilder.getContractPISD201ServiceInternal().generateContractHost(dataASO, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_CONTRACT_ICR3);
        dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().validateContractGeneration(responseService);

        PolicyASO asoResponse = responseService.getBody();
        String codeOfficeTelemarketing = this.dependencyBuilder.getBasicProductInsuranceProperties().obtainOfficeTelemarketingCode();
        String saleChannelIdOffice = codeOfficeTelemarketing.equals(asoResponse.getData().getBank().getBranch().getId()) ? RBVDInternalConstants.Channel.TELEMARKETING_CODE : requestBody.getSaleChannelId();
        requestBody = PrePolicyTransfor.toMapBranchAndSaleChannelIdOfficial(asoResponse.getData().getBank().getBranch().getId(), saleChannelIdOffice, requestBody);
        dependencyBuilder.getCrossOperationsBusinessInsuranceContractBank().handleNonDigitalSale(requestBody);
        boolean isEndorsement = CrossOperationsBusinessInsuranceContractBank.validateEndorsement(requestBody);
        BigDecimal totalNumberInstallments = (requestBody.getFirstInstallment().getIsPaymentRequired()) ? BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments() - 1) : BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments());
        InsuranceContractDAO contractDao = InsuranceContractBean.toInsuranceContractDAO(requestBody,emissionDao,asoResponse.getData().getId(),isEndorsement,totalNumberInstallments) ;
        Map<String, Object> argumentsForSaveContract = InsuranceContractMap.contractDaoToMap(contractDao);

        argumentsForSaveContract.forEach((key, value) -> LOGGER.info(" :: executeGenerateContract | argumentsForSaveContract [ key {} with value: {} ] ", key, value));

        Boolean isSavedInsuranceContract = this.dependencyBuilder.getInsuranceContractDAO().saveInsuranceContract(argumentsForSaveContract);
        if(!isSavedInsuranceContract){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CONTRACT);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        List<InsuranceCtrReceiptsDAO> receiptsList = InsuranceReceiptBean.toInsuranceCtrReceiptsDAO(asoResponse, requestBody);
        List<String> productsNotGenerateMonthlyReceipts = this.dependencyBuilder.getBasicProductInsuranceProperties().obtainProductsNotGenerateMonthlyReceipts();
        String  operationGlossaryDesc = processContextContractAndPolicy.getBody().getOperationGlossaryDesc();
        if(RBVDInternalConstants.Period.MONTHLY_LARGE.equalsIgnoreCase(requestBody.getInstallmentPlan().getPeriod().getId()) && !productsNotGenerateMonthlyReceipts.contains(operationGlossaryDesc)){
            List<InsuranceCtrReceiptsDAO> receipts = InsuranceReceiptBean.toGenerateMonthlyReceipts(receiptsList.get(0));
            receiptsList.addAll(receipts);
        }
        Map<String, Object>[] receiptsArguments = InsuranceReceiptMap.receiptsToMaps(receiptsList);
        Boolean isSavedInsuranceReceipts = this.dependencyBuilder.getInsuranceCtrReceiptsDAO().saveInsuranceReceipts(receiptsArguments);
        if(!isSavedInsuranceReceipts){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CTR_RECEIPTS);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        processContextContractAndPolicy.getBody().setDataASO(dataASO);
        processContextContractAndPolicy.getBody().setAsoResponse(asoResponse);
        processContextContractAndPolicy.getBody().setPolicy(requestBody);
        processContextContractAndPolicy.getBody().setContractDao(contractDao);
        processContextContractAndPolicy.getBody().setEndorsement(isEndorsement);
        stepsBankContract.executeStepGenerationContract(processContextContractAndPolicy, stepsBankContract);

    }
}
