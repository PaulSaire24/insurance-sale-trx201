package com.bbva.rbvd.lib.r211.impl.service;

import java.util.Map;

public interface IInsuranceCtrReceiptsDAO {

    Boolean updateExpirationDateReceipts(Map<String, Object>[] argumentsForSaveCtrReceips);

    Boolean saveInsuranceReceipts(Map<String, Object>[] argumentsForSaveCtrReceips);

}
