package com.bbva.rbvd.lib.r211.impl.transfor.list;

import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.RelatedContractDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.ContractType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RelatedContractsList {

    public static List<RelatedContractDAO> toRelatedContractDAOList (PolicyDTO requestBody, InsuranceContractDAO contractDao) {
        return requestBody.getRelatedContracts().stream().map(contract -> toRelatedContractDAO(contract, contractDao)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static RelatedContractDAO toRelatedContractDAO(RelatedContractDTO relatedContractDTO, InsuranceContractDAO contractDao) {
        RelatedContractDAO relatedContract = new RelatedContractDAO();
        if(ContractType.FIELD_INTERNAL_CONTRACT.equals(relatedContractDTO.getContractDetails().getContractType())) {
            relatedContract.setRelatedContractProductId(relatedContractDTO.getContractDetails().getProduct().getId());
            relatedContract.setLinkedContractId(relatedContractDTO.getContractDetails().getContractId());
        } else if (ContractType.FIELD_EXTERNAL_CONTRACT.equals(relatedContractDTO.getContractDetails().getContractType())){
            relatedContract.setRelatedContractProductId(relatedContractDTO.getContractDetails().getNumberType().getId());
            relatedContract.setLinkedContractId(relatedContractDTO.getContractDetails().getNumber());
        } else {
            return null;
        }
        relatedContract.setEntityId(contractDao.getEntityId());
        relatedContract.setBranchId(contractDao.getBranchId());
        relatedContract.setIntAccountId(contractDao.getIntAccountId());
        relatedContract.setStartLinkageDate(contractDao.getInsuranceContractStartDate());
        relatedContract.setEndLinkageDate(contractDao.getEndLinkageDate());
        relatedContract.setContractLinkedStatusType("01");
        relatedContract.setCreationUserId(contractDao.getCreationUserId());
        relatedContract.setUserAuditId(contractDao.getUserAuditId());
        return relatedContract;
    }

}
