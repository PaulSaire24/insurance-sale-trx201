package com.bbva.rbvd.lib.r211.impl.pattern.decorator;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessContextContractAndPolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

public interface Insurance {

     ResponseLibrary<ProcessContextContractAndPolicyDTO> createPolicyOfCompany(ProcessContextContractAndPolicyDTO processPrePolicy);

}
