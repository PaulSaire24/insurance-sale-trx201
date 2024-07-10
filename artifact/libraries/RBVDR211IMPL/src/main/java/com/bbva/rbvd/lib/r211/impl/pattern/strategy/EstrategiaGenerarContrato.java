package com.bbva.rbvd.lib.r211.impl.pattern.strategy;

import com.bbva.rbvd.dto.insurancemissionsale.context.EmisionContext;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;


public interface EstrategiaGenerarContrato {
    void generar(ResponseLibrary<ProcessPrePolicyDTO> contexto);
}
