package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.*;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public class NegocioTuMedidaProduct extends BasicProduct{
    public NegocioTuMedidaProduct(DependencyBuilder dependencyBuilder) {
        super(dependencyBuilder);
    }

    @Override
    public Pipeline createPipeline(String canal) {
        //switch
        return  super.createPipeline(canal);
    }
}
