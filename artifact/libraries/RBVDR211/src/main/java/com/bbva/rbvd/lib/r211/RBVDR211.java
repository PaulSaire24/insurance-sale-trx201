package com.bbva.rbvd.lib.r211;

import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

public interface RBVDR211 {

	/**
	 * This method is responsible for executing the business logic for the emission of non-life insurance policies.
	 * It takes a PolicyDTO object as input, which contains the details of the policy to be emitted.
	 * The method processes the policy and returns a ResponseLibrary object containing the details of the emitted policy.
	 *
	 * @param requestBody The PolicyDTO object containing the details of the policy to be emitted.
	 * @return A ResponseLibrary object containing the details of the emitted policy.
	 */
	ResponseLibrary<PolicyDTO> executeEmissionPolicyNotLifeFlowNew(PolicyDTO requestBody);

	/**
	 * This method is responsible for executing the business logic for the emission of insurance policies.
	 * It takes a PolicyDTO object as input, which contains the details of the policy to be emitted.
	 * The method processes the policy and returns a PolicyDTO object containing the details of the emitted policy.
	 *
	 * @param requestBody The PolicyDTO object containing the details of the policy to be emitted.
	 * @return A PolicyDTO object containing the details of the emitted policy.
	 */
	PolicyDTO executeEmissionPrePolicyLegacy(PolicyDTO requestBody);

	/**
	 * This method is responsible for executing the business logic for the emission of life insurance policies.
	 * It takes a PolicyDTO object as input, which contains the details of the policy to be emitted.
	 * The method processes the policy and returns a ResponseLibrary object containing the details of the emitted policy.
	 *
	 * @param requestBody The PolicyDTO object containing the details of the policy to be emitted.
	 * @return A ResponseLibrary object containing the details of the emitted policy.
	 */
	ResponseLibrary<PolicyDTO> executeEmissionPrePolicyLifeProductFlowNew(PolicyDTO requestBody);

	/**
	 * This method is responsible for executing the business logic for the emission of life insurance policies.
	 * It takes a PolicyDTO object as input, which contains the details of the policy to be emitted.
	 * The method processes the policy and returns a PolicyDTO object containing the details of the emitted policy.
	 *
	 * @param requestBody The PolicyDTO object containing the details of the policy to be emitted.
	 * @return A PolicyDTO object containing the details of the emitted policy.
	 */
	PolicyDTO executeEmissionPrePolicyLifeProductLegacy(PolicyDTO requestBody);

	ResponseLibrary<PolicyDTO> executeEmissionPolicy(PolicyDTO requestBody);
}
