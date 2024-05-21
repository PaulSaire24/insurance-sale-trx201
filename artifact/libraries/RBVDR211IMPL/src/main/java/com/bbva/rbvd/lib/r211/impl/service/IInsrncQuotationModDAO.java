package com.bbva.rbvd.lib.r211.impl.service;

import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;

import java.util.Map;

public interface IInsrncQuotationModDAO {

    Map<String, Object> findQuotationByQuotationId(String quotationId) ;

    Map<String, Object> getDataInsuredParticipantFromDB(PolicyDTO requestBody, Map<String, Object> responseQueryGetRequiredFields);
}
