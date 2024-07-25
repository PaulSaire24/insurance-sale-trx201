package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.GenerateContractNotLifeStep;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceReceiptBean;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetReceiptListStep implements Step {
    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public GetReceiptListStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }
    @Override
    public void executeStepGenerationContract(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        List<InsuranceCtrReceiptsDAO> receiptsList = processContextContractAndPolicy.getBody().getReceiptsList();
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        List<String> productsNotGenerateMonthlyReceipts = this.dependencyBuilder.getBasicProductInsuranceProperties().obtainProductsNotGenerateMonthlyReceipts();
        String  operationGlossaryDesc = processContextContractAndPolicy.getBody().getOperationGlossaryDesc();
        if(RBVDInternalConstants.Period.MONTHLY_LARGE.equalsIgnoreCase(requestBody.getInstallmentPlan().getPeriod().getId()) && !productsNotGenerateMonthlyReceipts.contains(operationGlossaryDesc)){
            List<InsuranceCtrReceiptsDAO> receipts = InsuranceReceiptBean.toGenerateMonthlyReceipts(receiptsList.get(0));
            receiptsList.addAll(receipts);
        }
        processContextContractAndPolicy.getBody().setReceiptsList(receiptsList);
        stepsBankContract.executeStepGenerationContract(processContextContractAndPolicy, stepsBankContract);
    }
}
