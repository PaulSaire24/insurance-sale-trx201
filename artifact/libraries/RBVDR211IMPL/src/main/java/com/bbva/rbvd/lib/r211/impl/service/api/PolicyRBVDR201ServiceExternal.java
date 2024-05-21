package com.bbva.rbvd.lib.r211.impl.service.api;

import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.lib.r211.impl.service.api.interfaces.PolicyServiceExternal;

public class PolicyRBVDR201ServiceExternal implements PolicyServiceExternal {

    private RBVDR201 rbvdR201;

    @Override
    public EmisionBO executeCreatePolicy(EmisionBO requestBody, String quotationId, String traceId, String productId) {
        return this.rbvdR201.executePrePolicyEmissionService(requestBody, quotationId, traceId, productId);
    }

    @Override
    public AgregarTerceroBO executeAddParticipantsService(AgregarTerceroBO requestBody, String quotationId, String productId, String traceId) {
        return this.rbvdR201.executeAddParticipantsService(requestBody, quotationId, productId, traceId);
    }

    public void setRbvdR201(RBVDR201 rbvdR201) {
        this.rbvdR201 = rbvdR201;
    }
}
