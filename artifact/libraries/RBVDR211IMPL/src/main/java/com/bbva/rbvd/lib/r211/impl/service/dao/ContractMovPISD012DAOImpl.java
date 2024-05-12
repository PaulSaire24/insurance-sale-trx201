package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.service.IInsrncContractMovDAO;
import com.bbva.rbvd.lib.r211.impl.transfor.map.IsrcContractMovMap;

import java.util.Map;

public class ContractMovPISD012DAOImpl implements IInsrncContractMovDAO {

    private  PISDR012 pisdR012;

    @Override
    public boolean saveInsrncContractmov(IsrcContractMovDAO contractMovDao) {
        Map<String, Object> argumentsForContractMov = IsrcContractMovMap.isrcContractMovToMap(contractMovDao);
        int insertedContractMove = this.pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue(), argumentsForContractMov);
        if(insertedContractMove != 1){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
