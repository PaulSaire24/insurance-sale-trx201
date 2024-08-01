package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.product;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.BasicPreFormalizedToCharged;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public class NegocioTuMedidaPreFormalizedToCharged extends BasicPreFormalizedToCharged {
    public NegocioTuMedidaPreFormalizedToCharged(DependencyBuilder dependencyBuilder) {
        super(dependencyBuilder);
    }

    @Override
    public Pipeline createPipeline(String canal) {
        //switch
        return  super.createPipeline(canal);
    }
}