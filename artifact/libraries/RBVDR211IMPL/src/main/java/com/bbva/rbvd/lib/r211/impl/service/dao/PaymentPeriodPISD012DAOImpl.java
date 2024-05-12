package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.service.IInsrncPaymentPeriodDAO;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;

import java.util.Map;

public class PaymentPeriodPISD012DAOImpl implements IInsrncPaymentPeriodDAO {

    private PISDR012 pisdR012;

    @Override
    public Map<String, Object> findPaymentPeriodByFrequencyType(String frequencyType) {
        Map<String, Object> mapFrequencyType = FunctionsUtils.createSingleArgument(frequencyType, RBVDProperties.FIELD_POLICY_PAYMENT_FREQUENCY_TYPE.getValue());
        return this.pisdR012.executeGetASingleRow(RBVDProperties.QUERY_SELECT_PAYMENT_PERIOD.getValue(), mapFrequencyType);
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
