package com.bbva.rbvd.lib.r211.impl.service;

import java.util.Map;

public interface IInsuranceProductDAO {

    Map<String, Object> findByProductId(String productId);

}
