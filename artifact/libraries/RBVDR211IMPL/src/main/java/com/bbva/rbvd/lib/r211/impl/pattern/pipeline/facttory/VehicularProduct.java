package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.*;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public class VehicularProduct extends BasicProduct{

    public VehicularProduct(DependencyBuilder dependencyBuilder) {
        super(dependencyBuilder);
    }

    @Override
    public Pipeline createPipeline(String canal) {
        Pipeline pipeline = super.createPipeline(canal);
        return execute_PC(pipeline);
    }

    Pipeline execute_PC(Pipeline pipeline){
        return pipeline.addStep(2, new FetchRequiredDataVehicular(dependencyBuilder));
    }
}
