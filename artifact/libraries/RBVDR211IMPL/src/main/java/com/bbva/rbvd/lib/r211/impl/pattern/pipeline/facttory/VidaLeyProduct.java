package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.GetReceiptListStep;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep.SaveContractParticipantStep;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public class VidaLeyProduct implements PipelineFactory{

    private final DependencyBuilder dependencyBuilder;

    public VidaLeyProduct(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    public  Pipeline VidaLey_PC(){
        return new Pipeline().addStep(new GetReceiptListStep(dependencyBuilder))
                    .addStep(new SaveContractParticipantStep(dependencyBuilder));
    }

    @Override
    public Pipeline createPipeline(String canal) {
        return VidaLey_PC();
    }
}
