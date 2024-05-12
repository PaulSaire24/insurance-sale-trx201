package com.bbva.rbvd.lib.r211.impl.service;

import java.util.Map;

public interface IInsrncPaymentPeriodDAO {
    Map<String, Object> findPaymentPeriodByFrequencyType(String frequencyType) ;
}
