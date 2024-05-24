package com.bbva.rbvd.lib.r211.impl.pattern.factory.interfaces;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

public interface InsuranceCompanyFactory {

    ResponseLibrary<ProcessPrePolicyDTO> createInsuranceByProduct(ProcessPrePolicyDTO processPrePolicyDTO);
}
