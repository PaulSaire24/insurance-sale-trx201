package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

public interface Step {

       void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract);

}
