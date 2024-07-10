package com.bbva.rbvd.lib.r211.impl.pattern.facttory;

import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.Pipeline;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.*;
import com.bbva.rbvd.lib.r211.impl.pattern.strategy.EstrategiaFormalizarYCobrar;
import com.bbva.rbvd.lib.r211.impl.pattern.strategy.EstrategiaSoloCobrar;
import org.apache.commons.lang3.StringUtils;

public class PipFactoryHogar implements PipelineFactory{

    private Pipeline crearPipelineHogar(String id) {

        if(StringUtils.isNotEmpty(id)){
            return new Pipeline()
                    .addPaso(new PreEmisionPaso())
                    .addPaso(new ValidacionEndoso());
        }else{
            return new Pipeline()
                    .addPaso(new ValidarConditionsPaso())
                    .addPaso(new ObtenerDatosRequeridosPaso())
                    .addPaso(new GenerarContratoPaso(new EstrategiaFormalizarYCobrar()))
                    .addPaso(new GuardarDatosSeguroPaso())
                    .addPaso(new GuardarDatosSeguroPaso())
                    .addPaso(new GenerarPagoSeguroPaso())
                    .addPaso(new ValidacionEndoso());
        }
    }
    @Override
    public Pipeline crearPipeline(String canal, String id) {
        if (canal.equals("PIC")) {
            return crearPipelineHogar(id);
        }
        return  null;
    }
}
