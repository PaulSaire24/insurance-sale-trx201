package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.service.IInsurncRelatedContract;
import com.bbva.rbvd.lib.r211.impl.transfor.map.RelatedContractstMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RelatedContractPISD012DAOImpl implements IInsurncRelatedContract {

    private PISDR012 pisdR012;


    @Override
    public boolean savedContractDetails(List<RelatedContractDAO> relatedContractsDao) {
        Map<String, Object>[] relatedContractsArguments = RelatedContractstMap.relatedContractsToMaps(relatedContractsDao);
        int[] insertedContractDetails =  this.pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSURANCE_CONTRACT_DETAILS.getValue(), relatedContractsArguments);
        if(Objects.isNull(insertedContractDetails) || Arrays.stream(insertedContractDetails).sum() != relatedContractsDao.size()) {;
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
