package com.bbva.rbvd.lib.r211.impl.pattern.facttory.impl;

import com.bbva.rbvd.lib.r211.impl.pattern.facttory.*;

public class FactoryProductImpl {
    public static PipelineFactory createFactory(String productId) {
        if (productId.equals("Vehicular")) {
            return new PipFactoryVehicular();
        } else if (productId.equals("Vida")) {
            return new PipFactoryVida();
        } else if (productId.equals("Hogar")) {
            return new PipFactoryHogar();
        } else {
            return new PipFactoryGeneral();
        }
    }
}
