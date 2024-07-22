package com.bbva.rbvd.lib.r211.impl.aspects.interfaces;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessContextContractAndPolicyDTO;

public interface ManagementOperation {

    void afterProcessBusinessExecutionNotLifeCross(ProcessContextContractAndPolicyDTO processContextContractAndPolicyDTO);
    void afterProcessBusinessExecutionLifeCross(ProcessContextContractAndPolicyDTO processContextContractAndPolicyDTO);
}
