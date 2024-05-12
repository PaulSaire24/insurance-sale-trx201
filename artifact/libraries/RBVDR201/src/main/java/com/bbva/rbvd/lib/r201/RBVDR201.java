package com.bbva.rbvd.lib.r201;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;

import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;

import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

public interface RBVDR201 {

	GetContactDetailsASO executeGetContactDetailsService(String customerId);
	PolicyASO executePrePolicyEmissionASO(DataASO requestBody);
	EmisionBO executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId, String productId);
	CustomerListASO executeGetCustomerInformation(String customerId);
	ListBusinessesASO executeGetListBusinesses(String customerId, String expands);
	String executeCypherService(CypherASO input);
	Integer executePutEventUpsilonService(CreatedInsrcEventDTO createdInsuranceEvent);
	AgregarTerceroBO executeAddParticipantsService(AgregarTerceroBO requestBody, String quotationId, String productId, String traceId);
	ResponseLibrary<PolicyASO> executePrePolicyEmissionCics(DataASO requestBody, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized);
	ResponseLibrary<PolicyASO> executeInsurancePaymentAndFormalization(PolicyASO policyASO);

}
