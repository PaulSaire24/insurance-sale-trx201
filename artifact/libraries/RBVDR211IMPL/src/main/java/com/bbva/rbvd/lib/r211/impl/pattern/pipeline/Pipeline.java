package com.bbva.rbvd.lib.r211.impl.pattern.pipeline;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {
    private List<PasoPipeline> pasos = new ArrayList<>();

    public Pipeline addPaso(PasoPipeline paso) {
        pasos.add(paso);
        return this;
    }

    public void ejecutar(ResponseLibrary<ProcessPrePolicyDTO> contexto) {
        ejecutar(contexto, 0);
    }

    private void ejecutar(ResponseLibrary<ProcessPrePolicyDTO> contexto, int indice) {
        if (indice < pasos.size()) {
            pasos.get(indice).ejecutar(contexto, (ctx, next) -> ejecutar(ctx, indice + 1));
        }
    }
}
