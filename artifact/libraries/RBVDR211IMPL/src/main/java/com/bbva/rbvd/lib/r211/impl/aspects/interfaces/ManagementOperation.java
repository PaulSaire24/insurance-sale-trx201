package com.bbva.rbvd.lib.r211.impl.aspects.interfaces;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;

public interface ManagementOperation {

    void afterProcessBusinessExecutionNotLifeCross(ContextEmission contextEmission);
    void afterProcessBusinessExecutionLifeCross(ContextEmission contextEmission);
}
