package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.service.IInsurncCtrParticipantDAO;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsurncCtrParticipantMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InsurncCtrParticipantPISD012DAOImpl implements IInsurncCtrParticipantDAO {

    private PISDR012 pisdR012;

    @Override
    public boolean savedContractParticipant(List<IsrcContractParticipantDAO> participants) {
        Map<String, Object>[] participantsArguments = InsurncCtrParticipantMap.participantsToMaps(participants);
        int[] insertedRows = this.pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSRNC_CTR_PARTICIPANT.getValue(), participantsArguments);
        if(Objects.isNull(insertedRows) || Arrays.stream(insertedRows).sum() != Arrays.stream(participantsArguments).count()) {;
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
