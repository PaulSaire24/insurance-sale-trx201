package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.product;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.PipelineFactory;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.*;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public class VidaLeyProduct implements PipelineFactory {

    private final DependencyBuilder dependencyBuilder;

    public VidaLeyProduct(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    public  Pipeline VidaLey_PC(){
        return new Pipeline().addStep(new ValidateConditionsContractStep(dependencyBuilder))
                            .addStep(new PartyValidationStep(dependencyBuilder))
                            .addStep(new FetchRequiredDataStep(dependencyBuilder))
                            .addStep(new GenerateContractStep(dependencyBuilder))
                            .addStep(new SaveInsuranceDataStep(dependencyBuilder))
                            .addStep(new ValidateRoles(dependencyBuilder))
                            .addStep(new StoreEndorsementStep(dependencyBuilder))
                            .addStep(new GeneratePaymentStep(dependencyBuilder));
    }

    @Override
    public Pipeline createPipeline(String canal) {
        return VidaLey_PC();
    }
}
