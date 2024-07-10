package com.bbva.rbvd.lib.r211.impl.pattern.facttory;

import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.Pipeline;

public interface PipelineFactory {
    Pipeline crearPipeline(String canal, String id);
}
