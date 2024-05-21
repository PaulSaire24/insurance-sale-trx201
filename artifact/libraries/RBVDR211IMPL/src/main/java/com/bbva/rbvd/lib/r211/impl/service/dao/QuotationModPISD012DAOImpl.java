package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import com.bbva.rbvd.lib.r211.impl.service.IInsrncQuotationModDAO;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;

import java.util.HashMap;
import java.util.Map;

public class QuotationModPISD012DAOImpl implements IInsrncQuotationModDAO {

    private PISDR012 pisdR012;

    protected PISDR350 pisdR350;


    @Override
    public Map<String, Object> findQuotationByQuotationId(String quotationId) {
        Map<String, Object> mapQuotationId = FunctionsUtils.createSingleArgument(quotationId, RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue());
        return this.pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), mapQuotationId);
    }

    @Override
    public Map<String, Object> getDataInsuredParticipantFromDB(PolicyDTO requestBody, Map<String, Object> responseQueryGetRequiredFields) {
        Map<String,Object> argumentForGetDataInsured = new HashMap<>();
        argumentForGetDataInsured.put(RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue(),
                requestBody.getQuotationId());
        argumentForGetDataInsured.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(),
                responseQueryGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()));
        argumentForGetDataInsured.put(RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(), requestBody.getProductPlan().getId());
        return this.pisdR350.executeGetASingleRow(ConstantsUtil.Queries.QUERY_GET_INSURED_DATA_LIFE,argumentForGetDataInsured);
    }

    public void setPisdR350(PISDR350 pisdR350) {
        this.pisdR350 = pisdR350;
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
