package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceCtrReceiptsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class InsuranceCtrReceiptsPISD012DAOImpl implements IInsuranceCtrReceiptsDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsuranceCtrReceiptsPISD012DAOImpl.class);

    private PISDR012 pisdR012;


    @Override
    public Boolean updateExpirationDateReceipts(Map<String, Object>[] argumentsForSaveCtrReceips) {

        int[] result = this.pisdR012.executeMultipleInsertionOrUpdate("PISD.UPDATE_EXPIRATION_DATE_RECEIPTS", argumentsForSaveCtrReceips);
        if(Objects.isNull(result) || Arrays.stream(result).sum() != Arrays.stream(argumentsForSaveCtrReceips).count()) {;
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public  Boolean saveInsuranceReceipts(Map<String, Object>[] receiptsArguments) {
        int[] insertedContract = this.pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(), receiptsArguments);
        if(Objects.isNull(insertedContract) || Arrays.stream(insertedContract).sum() != Arrays.stream(receiptsArguments).count()) {;
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
