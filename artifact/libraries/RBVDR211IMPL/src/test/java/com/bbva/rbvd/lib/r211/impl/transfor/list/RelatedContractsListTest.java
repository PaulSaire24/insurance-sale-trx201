package com.bbva.rbvd.lib.r211.impl.transfor.list;

import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;
import com.bbva.rbvd.dto.insrncsale.policy.ContractDetailsDTO;
import com.bbva.rbvd.dto.insrncsale.policy.FinancialProductDTO;
import com.bbva.rbvd.dto.insrncsale.policy.NumberTypeDTO;
import com.bbva.rbvd.dto.insrncsale.policy.RelatedContractDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RelatedContractsListTest {


    @Test
    public void toRelatedContractDAOReturnsCorrectInternalContract() {
        // Given
        RelatedContractDTO relatedContractDTO = new RelatedContractDTO();
        relatedContractDTO.setContractDetails(new ContractDetailsDTO());
        relatedContractDTO.getContractDetails().setContractType(RBVDInternalConstants.ContractType.FIELD_INTERNAL_CONTRACT);
        relatedContractDTO.getContractDetails().setProduct(new FinancialProductDTO());
        relatedContractDTO.getContractDetails().getProduct().setId("productID");
        relatedContractDTO.getContractDetails().setContractId("contractID");
        InsuranceContractDAO contractDao = new InsuranceContractDAO();

        // When
        RelatedContractDAO result = RelatedContractsList.toRelatedContractDAO(relatedContractDTO, contractDao);

        // Then
        assertEquals("productID", result.getRelatedContractProductId());
        assertEquals("contractID", result.getLinkedContractId());
    }

    @Test
    public void toRelatedContractDAOReturnsCorrectExternalContract() {
        // Given
        RelatedContractDTO relatedContractDTO = new RelatedContractDTO();
        relatedContractDTO.setContractDetails(new ContractDetailsDTO());
        relatedContractDTO.getContractDetails().setContractType(RBVDInternalConstants.ContractType.FIELD_EXTERNAL_CONTRACT);
        relatedContractDTO.getContractDetails().setNumberType(new NumberTypeDTO());
        relatedContractDTO.getContractDetails().getNumberType().setId("numberTypeID");
        relatedContractDTO.getContractDetails().setNumber("number");
        InsuranceContractDAO contractDao = new InsuranceContractDAO();

        // When
        RelatedContractDAO result = RelatedContractsList.toRelatedContractDAO(relatedContractDTO, contractDao);

        // Then
        assertEquals("numberTypeID", result.getRelatedContractProductId());
        assertEquals("number", result.getLinkedContractId());
    }

    @Test
    public void toRelatedContractDAOReturnsNullForUnknownContractType() {
        // Given
        RelatedContractDTO relatedContractDTO = new RelatedContractDTO();
        relatedContractDTO.setContractDetails(new ContractDetailsDTO());
        relatedContractDTO.getContractDetails().setContractType("unknown");
        InsuranceContractDAO contractDao = new InsuranceContractDAO();

        // When
        RelatedContractDAO result = RelatedContractsList.toRelatedContractDAO(relatedContractDTO, contractDao);

        // Then
        assertNull(result);
    }

}