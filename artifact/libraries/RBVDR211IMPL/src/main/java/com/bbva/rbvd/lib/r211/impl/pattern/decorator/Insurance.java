package com.bbva.rbvd.lib.r211.impl.pattern.decorator;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

public interface Insurance {

     ResponseLibrary<ProcessPrePolicyDTO> createPolicyOfCompany(ProcessPrePolicyDTO processPrePolicy);

}
