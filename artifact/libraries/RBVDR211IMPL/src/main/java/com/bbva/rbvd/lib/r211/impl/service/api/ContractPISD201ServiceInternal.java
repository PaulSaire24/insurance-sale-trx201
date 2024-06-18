package com.bbva.rbvd.lib.r211.impl.service.api;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r201.RBVDR201;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContractPISD201ServiceInternal {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractPISD201ServiceInternal.class);

    private RBVDR201 rbvdR201;

    public ResponseLibrary<PolicyASO> generateContractHost(DataASO requestBody, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized){
        LOGGER.info(" ContractPISD201ServiceInternal :: executeGenerateContractHost :: [ RequestBody :: {} ]",requestBody);
        return this.rbvdR201.executePreFormalizationContract(requestBody,indicatorPreFormalized);
    }

    public ResponseLibrary<PolicyASO> generateFormalizationContractAndPayment(DataASO requestBody, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized){
        LOGGER.info(" ContractPISD201ServiceInternal :: executeGenerateContractHost :: [ DataASO :: {} ]",requestBody);
        return this.rbvdR201.executeInsurancePaymentAndFormalization(requestBody,indicatorPreFormalized);
    }

    public void setRbvdR201(RBVDR201 rbvdR201) {
        this.rbvdR201 = rbvdR201;
    }
}
