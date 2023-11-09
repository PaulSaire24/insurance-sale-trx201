package com.bbva.rbvd.lib.r211.service.api;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.lib.r201.RBVDR201;

public class ConsumerInternalService {

    private RBVDR201 rbvdR201;

    public PolicyASO callPrePolicyEmissionASO(DataASO request){
        return rbvdR201.executePrePolicyEmissionASO(request);
    }


    public void setRbvdr201(RBVDR201 rbvdr201) {
        this.rbvdR201 = rbvdr201;
    }
}
