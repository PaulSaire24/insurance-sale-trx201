package com.bbva.rbvd.lib.r211.impl.service;

import com.bbva.pisd.dto.contract.search.CertifyBankCriteria;
import com.bbva.pisd.dto.insurancedao.entities.ContractEntity;

import java.util.Map;

public interface IInsuranceContractDAO {

    Boolean findExistenceInsuranceContract (String quotationId);
    Boolean saveInsuranceContract(Map<String, Object> argumentContract);
    Boolean updateEndorsementInContract(String policyNumber, String intAccountId);

    Boolean updateInsuranceContract(Map<String, Object> argumentContract);
    ContractEntity executeFindByCertifiedBank(CertifyBankCriteria certifyBankCriteria) ;
    void updateInsuranceContractByCertifyBank(ContractEntity contractEntity);
}
