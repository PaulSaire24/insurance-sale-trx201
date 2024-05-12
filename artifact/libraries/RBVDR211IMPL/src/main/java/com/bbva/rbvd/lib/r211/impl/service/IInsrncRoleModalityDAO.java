package com.bbva.rbvd.lib.r211.impl.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IInsrncRoleModalityDAO {

    List<Map<String, Object>> findByProductIdAndModalityType(BigDecimal productId, String modalityType);
}
