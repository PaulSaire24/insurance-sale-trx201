package com.bbva.rbvd.lib.r201;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;

public interface RBVDR201 {

	PolicyASO executePrePolicyEmissionASO(PolicyDTO requestBody);
	EmisionBO executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId);

}
