package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.lib.r401.PISDR401;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceProductDAO;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class ProductPISD401DaoImpl implements IInsuranceProductDAO {

    private PISDR401 pisdR401;


    public Map<String, Object> findByProductId(String productId) {
        Map<String,Object> productMap = (Map<String, Object>) this.pisdR401.executeGetProductById(ConstantsUtil.Queries.QUERY_SELECT_PRODUCT_BY_PRODUCT_TYPE, singletonMap(RBVDProperties.FIELD_INSURANCE_PRODUCT_TYPE.getValue(), productId));
        return productMap;
    }

    public void setPisdR401(PISDR401 pisdR401) {
        this.pisdR401 = pisdR401;
    }
}
