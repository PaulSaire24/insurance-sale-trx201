package com.bbva.rbvd.lib.r211.impl.aspects.interfaces;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;

public interface ManagementOperation {

    void afterProcessBusinessExecutionNotLifeCross(ProcessPrePolicyDTO processPrePolicyDTO);
    void afterProcessBusinessExecutionLifeCross(ProcessPrePolicyDTO processPrePolicyDTO);
}
