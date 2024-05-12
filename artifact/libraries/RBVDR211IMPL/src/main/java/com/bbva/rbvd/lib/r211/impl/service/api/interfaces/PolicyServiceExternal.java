package com.bbva.rbvd.lib.r211.impl.service.api.interfaces;

import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;

public interface PolicyServiceExternal {

    EmisionBO executeCreatePolicy(EmisionBO requestBody, String quotationId, String traceId, String productId);
}
