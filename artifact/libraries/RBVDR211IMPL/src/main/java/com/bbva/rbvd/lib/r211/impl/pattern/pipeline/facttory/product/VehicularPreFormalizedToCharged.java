package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.product;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.BasicPreFormalizedToCharged;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public class VehicularPreFormalizedToCharged extends BasicPreFormalizedToCharged {

    public VehicularPreFormalizedToCharged(DependencyBuilder dependencyBuilder) {
        super(dependencyBuilder);
    }

    @Override
    public Pipeline createPipeline(String canal) {
        Pipeline pipeline = super.createPipeline(canal);
        return execute_PC(pipeline);
    }

    Pipeline execute_PC(Pipeline pipeline){
        return pipeline.removeStep(2);
    }
}
