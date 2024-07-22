package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps;

import com.bbva.pisd.dto.insurancedao.entities.ContractEntity;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessContextContractAndPolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceReceiptBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PolicyASOBean;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceReceiptMap;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class GenerateFormalizationOracleFlowPreEmission implements Step {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateFormalizationOracleFlowPreEmission.class);

    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public GenerateFormalizationOracleFlowPreEmission(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    @Override
    public void executeStepGenerationContract(ResponseLibrary<ProcessContextContractAndPolicyDTO> processContextContractAndPolicy, Step stepsBankContract) {
        LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ START ]");
        LOGGER.info(" EmissionFlowPrePolicyBusinessImpl :: executeFlowPreEmissionPolicy :: [ Body :: {} ]",processContextContractAndPolicy);
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        PolicyASO asoResponse = PolicyASOBean.PolicyDTOtoPolicyASO(requestBody);
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

        String[] certifyBank = FunctionsUtils.generateCodeToSearchContractInOracle(requestBody.getId());
        ContractEntity contractEntity = ContractEntity.ContractBuilder.an()
                .withContractStatusId(RBVDInternalConstants.CONTRACT_STATUS_ID.FOR.getValue())
                .withInsuranceContractEntityId(certifyBank[0])
                .withInsuranceContractBranchId(certifyBank[1])
                .withContractFirstVerfnDigitId(certifyBank[2])
                .withContractSecondVerfnDigitId(certifyBank[3])
                .withInsrcContractIntAccountId(certifyBank[4])
                .withUserAuditId(processContextContractAndPolicy.getBody().getPolicy().getUserAudit())
                .build();

        this.dependencyBuilder.getInsuranceContractDAO().updateInsuranceContractByCertifyBank(contractEntity);

        stepsBankContract.executeStepGenerationContract(processContextContractAndPolicy, stepsBankContract);
    }
}
