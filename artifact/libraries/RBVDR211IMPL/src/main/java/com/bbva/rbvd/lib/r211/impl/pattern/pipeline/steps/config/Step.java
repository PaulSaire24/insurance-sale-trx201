package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessContextContractAndPolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

public interface Step {

       void executeStepGenerationContract(ResponseLibrary<ProcessContextContractAndPolicyDTO> processContextContractAndPolicy, Step stepsBankContract);

}
