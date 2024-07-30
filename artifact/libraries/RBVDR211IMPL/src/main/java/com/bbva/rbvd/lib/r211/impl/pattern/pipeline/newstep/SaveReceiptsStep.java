package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceReceiptMap;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;

import java.util.List;
import java.util.Map;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class SaveReceiptsStep implements Step {

    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public SaveReceiptsStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }
    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        List<InsuranceCtrReceiptsDAO> receiptsList = processContextContractAndPolicy.getBody().getReceiptsList();
        Map<String, Object>[] receiptsArguments = InsuranceReceiptMap.receiptsToMaps(receiptsList);
        Boolean isSavedInsuranceReceipts = this.dependencyBuilder.getInsuranceCtrReceiptsDAO().saveInsuranceReceipts(receiptsArguments);
        if(!isSavedInsuranceReceipts){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(), RBVDInternalConstants.Tables.T_PISD_INSURANCE_CTR_RECEIPTS);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        stepsBankContract.execute(processContextContractAndPolicy, stepsBankContract);
    }
}
