package com.bbva.rbvd.lib.r211.impl.service.api.interfaces;

import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;

public interface PolicyServiceExternal {

    EmisionBO executeCreatePolicy(EmisionBO requestBody, String quotationId, String traceId, String productId);

    AgregarTerceroBO executeAddParticipantsService(AgregarTerceroBO requestBody, String quotationId, String productId, String traceId);

}
