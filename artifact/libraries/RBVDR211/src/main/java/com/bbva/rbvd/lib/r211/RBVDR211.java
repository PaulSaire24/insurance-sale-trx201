package com.bbva.rbvd.lib.r211;

import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;

public interface RBVDR211 {

	PolicyDTO executeBusinessLogicEmissionPrePolicy(PolicyDTO requestBody);
	PolicyDTO executeBusinessLogicEmissionPrePolicyLifeEasyYes(PolicyDTO requestBody);
}
