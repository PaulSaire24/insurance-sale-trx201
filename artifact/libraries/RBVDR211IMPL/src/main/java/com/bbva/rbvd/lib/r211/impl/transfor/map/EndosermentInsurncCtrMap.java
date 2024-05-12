package com.bbva.rbvd.lib.r211.impl.transfor.map;


import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import java.util.HashMap;
import java.util.Map;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.ContractType;

public class EndosermentInsurncCtrMap {
    public static Map<String, Object> endosermentInsurncCtrToMap(InsuranceContractDAO contractDao, String endosatarioRuc, Double endosatarioPorcentaje) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), contractDao.getEntityId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(), contractDao.getBranchId());
        arguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), contractDao.getIntAccountId());
        arguments.put(RBVDProperties.FIELD_DOCUMENT_TYPE_ID.getValue(), "R");
        arguments.put(RBVDProperties.FIELD_DOCUMENT_ID.getValue(), endosatarioRuc);
        arguments.put(RBVDProperties.FIELD_ENDORSEMENT_SEQUENCE_NUMBER.getValue(), 1);
        arguments.put(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue(), "TO PROCESS");
        arguments.put(RBVDProperties.FIELD_ENDORSEMENT_EFF_START_DATE.getValue(), contractDao.getInsuranceContractStartDate());
        arguments.put(RBVDProperties.FIELD_ENDORSEMENT_EFF_END_DATE.getValue(), contractDao.getInsuranceContractEndDate());
        arguments.put(RBVDProperties.FIELD_POLICY_ENDORSEMENT_PER.getValue(), endosatarioPorcentaje);
        arguments.put(RBVDProperties.FIELD_REGISTRY_SITUATION_TYPE.getValue(), "01");
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), ContractType.FIELD_SYSTEM);
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), ContractType.FIELD_SYSTEM);
        return arguments;
    }
}
