package com.bbva.rbvd.lib.r211.impl.pattern.facttory;

import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.Pipeline;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.*;
import com.bbva.rbvd.lib.r211.impl.pattern.strategy.EstrategiaFormalizarYCobrar;
import org.apache.commons.lang3.StringUtils;


public class PipFactoryVehicular implements PipelineFactory{

    private Pipeline crearPipelineVehicular_PIC(String id) {
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

    public static Pipeline crearPipelineVehicular_ZP(String id) {
        if(StringUtils.isNotEmpty(id)){
            return new Pipeline()
                    .addPaso(new PreEmisionPaso())
                    .addPaso(new ValidacionEndosoPaticipant());
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
            return crearPipelineVehicular_PIC(id);
        } else if (canal.equals("ZP")) {
            return crearPipelineVehicular_ZP(id);
        }else
            return null;
    }
}
