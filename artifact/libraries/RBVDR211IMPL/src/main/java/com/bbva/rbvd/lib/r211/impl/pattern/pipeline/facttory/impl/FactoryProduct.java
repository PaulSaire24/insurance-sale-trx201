package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.impl;

import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.PipelineFactory;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.VehicularProduct;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.VidaLeyProduct;

public class FactoryProduct {

    public static PipelineFactory getInstance(String productId, DependencyBuilder dependencyBuilder){
        switch (productId) {
            case "Vehicular":
                return new VehicularProduct(dependencyBuilder);
            case "842":
                return new VidaLeyProduct(dependencyBuilder);
            default:
                return null;
        }
    }
}
