package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.*;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public class VehicularProduct implements PipelineFactory{

    private final DependencyBuilder dependencyBuilder;

    public VehicularProduct(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    Pipeline Vehicular_PC(String canal){

        return new Pipeline().addStep(new ValidateConditionsContractStep(dependencyBuilder))
                            .addStep(new FetchRequiredDataStep(dependencyBuilder))
                            .addStep(new FetchRequiredDataVehicular(dependencyBuilder))
                            .addStep(new GenerateContractStep(dependencyBuilder))
                            .addStep(new GetReceiptListStep(dependencyBuilder))
                            .addStep(new SaveReceiptsStep(dependencyBuilder))
                            .addStep(new SaveInsuranceDataStep(dependencyBuilder))
                            .addStep(new ValidateRelatedContractStep(dependencyBuilder))
                            .addStep(new StoreEndorsementStep(dependencyBuilder))
                            .addStep(new SaveContractParticipantStep(dependencyBuilder))
                            .addStep(new GeneratePaymentStep(dependencyBuilder));
    }


    @Override
    public Pipeline createPipeline(String canal) {
        //switch
        return Vehicular_PC(canal);
    }
}
