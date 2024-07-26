package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.FetchRequiredDataStep;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.FetchRequiredDataVehicular;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.GenerateContractStep;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.ValidateConditionsContractNotLifeStep;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public class VehicularProduct implements PipelineFactory{

    private final DependencyBuilder dependencyBuilder;

    public VehicularProduct(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    Pipeline Vehicular_PC(String canal){

        return new Pipeline().addStep(new ValidateConditionsContractNotLifeStep(dependencyBuilder))
                            .addStep(new FetchRequiredDataVehicular(dependencyBuilder))
                            .addStep(new GenerateContractStep(dependencyBuilder));
    }


    @Override
    public Pipeline createPipeline(String canal) {
        //switch
        return Vehicular_PC(canal);
    }
}
