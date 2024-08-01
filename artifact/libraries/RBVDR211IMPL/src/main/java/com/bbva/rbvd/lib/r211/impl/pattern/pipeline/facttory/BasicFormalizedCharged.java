package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.*;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public class BasicFormalizedCharged implements PipelineFactory{

    protected final DependencyBuilder dependencyBuilder;

    public BasicFormalizedCharged(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    @Override
    public Pipeline createPipeline(String canal) {
        return new Pipeline().addStep(new FetchRequiredDataStep(dependencyBuilder))
                                .addStep(new GenerateFormalizationOracleFlowPreEmission(dependencyBuilder))
                                .addStep(new ValidateConditionsContractFlowPreEmission(dependencyBuilder))
                                .addStep(new UpdateContractFormalizedStep(dependencyBuilder))
                                .addStep(new SaveInsuranceDataStep(dependencyBuilder))
                                .addStep(new ValidateRelatedContractStep(dependencyBuilder))
                                .addStep(new StoreEndorsementStep(dependencyBuilder))
                                .addStep(new GeneratePaymentStep(dependencyBuilder));
    }
}
