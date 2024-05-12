package com.bbva.rbvd.lib.r211.impl.service.api;

import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.lib.r211.impl.service.api.interfaces.PolicyServiceExternal;

public class PolicyRBVDR201ServiceExternal implements PolicyServiceExternal {

    private RBVDR201 rbvdR201;

    @Override
    public EmisionBO executeCreatePolicy(EmisionBO requestBody, String quotationId, String traceId, String productId) {
        return rbvdR201.executePrePolicyEmissionService(requestBody, quotationId, traceId, productId);
    }

    public void setRbvdR201(RBVDR201 rbvdR201) {
        this.rbvdR201 = rbvdR201;
    }
}
