package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.pisd.dto.insurancedao.entities.ContractEntity;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;

public class UpdateContractFormalizedStep implements Step {
    private final DependencyBuilder dependencyBuilder;

    public UpdateContractFormalizedStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }
    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
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
        stepsBankContract.execute(processContextContractAndPolicy, stepsBankContract);
    }
}
