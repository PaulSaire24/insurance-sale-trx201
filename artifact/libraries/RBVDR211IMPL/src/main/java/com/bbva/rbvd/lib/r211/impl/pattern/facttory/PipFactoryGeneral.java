package com.bbva.rbvd.lib.r211.impl.pattern.facttory;

import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.*;
import com.bbva.rbvd.lib.r211.impl.pattern.strategy.EstrategiaFormalizarYCobrar;

public class PipFactoryGeneral implements PipelineFactory{

    private  Pipeline crearPipelineGenerico() {

        return new Pipeline()
                .addPaso(new ValidarConditionsPaso())
                .addPaso(new PreEmisionPaso())
                .addPaso(new GenerarContratoPaso(new EstrategiaFormalizarYCobrar())) // Utiliza una estrategia predeterminada
                .addPaso(new GuardarDatosSeguroPaso())
                .addPaso(new GenerarPagoSeguroPaso()) // Todos los productos generan póliza
                .addPaso(new ValidacionEndoso())
                .addPaso(new InformarColaPaso());
    }
    @Override
    public Pipeline crearPipeline(String canal, String id) {
        return crearPipelineGenerico();
    }
}
