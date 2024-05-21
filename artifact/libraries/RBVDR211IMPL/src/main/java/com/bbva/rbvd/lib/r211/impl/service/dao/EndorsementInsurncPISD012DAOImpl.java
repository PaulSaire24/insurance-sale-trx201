package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.RBVDR211Impl;
import com.bbva.rbvd.lib.r211.impl.service.IEndorsementInsurncCtrDAO;
import com.bbva.rbvd.lib.r211.impl.transfor.map.EndosermentInsurncCtrMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EndorsementInsurncPISD012DAOImpl implements IEndorsementInsurncCtrDAO {


    private PISDR012 pisdR012;

    @Override
    public boolean saveEndosermentInsurncCtr(InsuranceContractDAO contractDao,String endosatarioRuc, Double endosatarioPorcentaje) {
        Map<String, Object> argumentsForSaveEndorsement = EndosermentInsurncCtrMap.endosermentInsurncCtrToMap(contractDao, endosatarioRuc, endosatarioPorcentaje);
        int insertedContractEndorsement = this.pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_POLICY_ENDORSEMENT.getValue(), argumentsForSaveEndorsement);
        if(insertedContractEndorsement != 1) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
