package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.service.IInsrncQuotationModDAO;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;

import java.util.Map;

public class QuotationModPISD012DAOImpl implements IInsrncQuotationModDAO {

    private PISDR012 pisdR012;


    @Override
    public Map<String, Object> findQuotationByQuotationId(String quotationId) {
        Map<String, Object> mapQuotationId = FunctionsUtils.createSingleArgument(quotationId, RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue());
        return this.pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), mapQuotationId);
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
