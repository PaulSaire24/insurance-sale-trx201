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

    public ResponseLibrary<ContextEmission> generateContract(ResponseLibrary<ContextEmission> processContextContractAndPolicy) {
        LOGGER.info("------> Ejecutando pipeline : " + processContextContractAndPolicy.getBody().getPolicy().getProductId());
        generateContract(processContextContractAndPolicy, 0);
        return processContextContractAndPolicy;
    }

    private void generateContract(ResponseLibrary<ContextEmission> processContextContractAndPolicy, int index) {
        if (index < pasos.size()) {
            pasos.get(index).executeStepGenerationContract(processContextContractAndPolicy, (ctx, next) -> generateContract(ctx, index + 1));
        }
    }
}
