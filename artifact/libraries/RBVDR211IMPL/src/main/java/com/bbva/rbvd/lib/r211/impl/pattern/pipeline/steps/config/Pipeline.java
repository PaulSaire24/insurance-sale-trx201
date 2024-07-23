package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);
    private List<Step> pasos = new ArrayList<>();

    public Pipeline addStep(Step step) {
        pasos.add(step);
        return this;
    }

    public ResponseLibrary<ContextEmission> executeGenerateInsuranceContractRoyal(ResponseLibrary<ContextEmission> processContextContractAndPolicy) {
        LOGGER.info("------> Ejecutando pipeline : " + processContextContractAndPolicy.getBody().getPolicy().getProductId());
        executeGenerateInsuranceContractRoyal(processContextContractAndPolicy, 0);
        return processContextContractAndPolicy;
    }

    private void executeGenerateInsuranceContractRoyal(ResponseLibrary<ContextEmission> processContextContractAndPolicy, int index) {
        if (index < pasos.size()) {
            pasos.get(index).executeStepGenerationContract(processContextContractAndPolicy, (ctx, next) -> executeGenerateInsuranceContractRoyal(ctx, index + 1));
        }
    }
}
