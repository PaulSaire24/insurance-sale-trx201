package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory;

import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;

public interface PipelineFactory {
    Pipeline createPipeline(String canal);
}
