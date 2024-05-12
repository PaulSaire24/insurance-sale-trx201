package com.bbva.rbvd.lib.r211.impl.transfor.map;

import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatedContractstMap {
    public static Map<String, Object>[] relatedContractsToMaps(List<RelatedContractDAO> relatedContractsDao) {
        return relatedContractsDao.stream().map(RelatedContractstMap::relatedContracttoMap).toArray(HashMap[]::new);
    }

    private static Map<String,Object> relatedContracttoMap(RelatedContractDAO contract){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(),contract.getEntityId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(),contract.getBranchId());
        arguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(),contract.getIntAccountId());
        arguments.put(RBVDProperties.FIELD_PRODUCT_ID.getValue(), contract.getRelatedContractProductId());
        arguments.put(RBVDProperties.FIELD_LINKED_CONTRACT_ID.getValue(), contract.getLinkedContractId());
        arguments.put(RBVDProperties.FIELD_START_LINKAGE_DATE.getValue(), contract.getStartLinkageDate());
        arguments.put(RBVDProperties.FIELD_LINKAGE_END_DATE.getValue(), contract.getEndLinkageDate());
        arguments.put(RBVDProperties.FIELD_CONTRACT_LINKED_STATUS_TYPE.getValue(), contract.getContractLinkedStatusType());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), contract.getCreationUserId());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), contract.getUserAuditId());
        return arguments;
    }
}
