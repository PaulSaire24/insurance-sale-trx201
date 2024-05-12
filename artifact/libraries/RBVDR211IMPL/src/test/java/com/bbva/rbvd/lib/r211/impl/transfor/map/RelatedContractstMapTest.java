package com.bbva.rbvd.lib.r211.impl.transfor.map;

import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RelatedContractstMapTest {

    @Test
    public void relatedContractsToMapsReturnsCorrectMaps() {
        // Given
        List<RelatedContractDAO> relatedContractsDao = new ArrayList<>();
        RelatedContractDAO contract = new RelatedContractDAO();
        contract.setEntityId("entityId");
        contract.setBranchId("branchId");
        contract.setIntAccountId("intAccountId");
        contract.setRelatedContractProductId("productId");
        contract.setLinkedContractId("linkedContractId");
        contract.setStartLinkageDate(String.valueOf(new Date()));
        contract.setEndLinkageDate(String.valueOf(new Date()));
        contract.setContractLinkedStatusType("statusType");
        contract.setCreationUserId("creationUserId");
        contract.setUserAuditId("userAuditId");
        relatedContractsDao.add(contract);

        // When
        Map<String, Object>[] result = RelatedContractstMap.relatedContractsToMaps(relatedContractsDao);

        // Then
        assertEquals(1, result.length);
        assertEquals("entityId", result[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertEquals("branchId", result[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertEquals("intAccountId", result[0].get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertEquals("productId", result[0].get(RBVDProperties.FIELD_PRODUCT_ID.getValue()));
        assertEquals("linkedContractId", result[0].get(RBVDProperties.FIELD_LINKED_CONTRACT_ID.getValue()));
        assertEquals(contract.getStartLinkageDate(), result[0].get(RBVDProperties.FIELD_START_LINKAGE_DATE.getValue()));
        assertEquals(contract.getEndLinkageDate(), result[0].get(RBVDProperties.FIELD_LINKAGE_END_DATE.getValue()));
        assertEquals("statusType", result[0].get(RBVDProperties.FIELD_CONTRACT_LINKED_STATUS_TYPE.getValue()));
        assertEquals("creationUserId", result[0].get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertEquals("userAuditId", result[0].get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
    }

    @Test
    public void relatedContractsToMapsReturnsEmptyArrayForEmptyList() {
        // Given
        List<RelatedContractDAO> relatedContractsDao = new ArrayList<>();

        // When
        Map<String, Object>[] result = RelatedContractstMap.relatedContractsToMaps(relatedContractsDao);

        // Then
        assertEquals(0, result.length);
    }
}