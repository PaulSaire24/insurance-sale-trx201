package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;

import java.util.Objects;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.ERROR_RESPONSE_SERVICE_ICR2;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class GeneratePaymentStep implements Step {

    private final DependencyBuilder dependencyBuilder;


    public GeneratePaymentStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        if(this.dependencyBuilder.getBasicProductInsuranceProperties().enabledPaymentICR2()){
            DataASO asoRequest = processContextContractAndPolicy.getBody().getDataASO();
            if(Objects.isNull(processContextContractAndPolicy.getBody().getPolicy().getId())){
                asoRequest.setId(processContextContractAndPolicy.getBody().getAsoResponse().getData().getId());
            }else{
                asoRequest.setId(processContextContractAndPolicy.getBody().getPolicy().getId());
            }
            RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.FORMALIZED_ACCOUNTING_ICR2;
            if(processContextContractAndPolicy.getBody().getPolicy().getFirstInstallment().getIsPaymentRequired()){
                indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.FORMALIZED_COLLECT_ACCOUNTING_ICR2;
            }
            ResponseLibrary<PolicyASO> responseGeneratePayment = this.dependencyBuilder.getContractPISD201ServiceInternal().generateFormalizationContractAndPayment(asoRequest, indicatorPreFormalized);
            if(!RBVDInternalConstants.Status.OK.equalsIgnoreCase(responseGeneratePayment.getStatusProcess())){
                throw buildValidation(ERROR_RESPONSE_SERVICE_ICR2);
            }
            processContextContractAndPolicy.getBody().setAsoResponse(responseGeneratePayment.getBody());
        }
        stepsBankContract.execute(processContextContractAndPolicy, stepsBankContract);
    }


}
