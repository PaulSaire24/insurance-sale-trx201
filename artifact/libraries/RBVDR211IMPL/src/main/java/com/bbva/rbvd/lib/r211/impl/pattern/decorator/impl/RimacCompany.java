package com.bbva.rbvd.lib.r211.impl.pattern.decorator.impl;

import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.Insurance;
import com.bbva.rbvd.lib.r211.impl.service.api.interfaces.PolicyServiceExternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RimacCompany implements Insurance {

    private static final Logger LOGGER = LoggerFactory.getLogger(RimacCompany.class);

    private PolicyServiceExternal policyServiceExternal;

    public RimacCompany(PolicyServiceExternal policyServiceExternal) {
        this.policyServiceExternal = policyServiceExternal;
    }

    @Override
    public ResponseLibrary<ContextEmission> createPolicyOfCompany(ContextEmission processPrePolicy) {
        LOGGER.info("[ CreateInsuranceByRimac]");
        EmisionBO requestBody = processPrePolicy.getRimacRequest();
        String quotationId = processPrePolicy.getQuotationId();
        String traceId =  processPrePolicy.getTraceId();
        String productId = processPrePolicy.getProductId();
        EmisionBO emisionBO = policyServiceExternal.executeCreatePolicy(requestBody,quotationId,traceId,productId);
        if(Objects.isNull(emisionBO)){
            return ResponseLibrary.ResponseServiceBuilder.an().
                    statusIndicatorProcess(RBVDInternalConstants.Status.ENR).build();
        }
        processPrePolicy.setRimacResponse(emisionBO);
        return ResponseLibrary.ResponseServiceBuilder.an()
                        .statusIndicatorProcess(RBVDInternalConstants.Status.ENR)
                        .body(processPrePolicy);
    }
}
