package com.bbva.rbvd.lib.r211.impl.service;

import java.util.Map;

public interface IInsuranceContractDAO {

    Boolean findExistenceInsuranceContract (String quotationId);
    Boolean saveInsuranceContract(Map<String, Object> argumentContract);
    Boolean updateEndorsementInContract(String policyNumber, String intAccountId);

    Boolean updateInsuranceContract(Map<String, Object> argumentContract);
}
