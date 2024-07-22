package com.bbva.rbvd.lib.r211.impl.transfor.bean;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.FirstInstallmentASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;

public class PolicyASOBean {

    public static PolicyASO PolicyDTOtoPolicyASO(PolicyDTO requestBody) {
        PolicyASO policy = new PolicyASO();
        policy.setData(mapInDataASO(requestBody));
        return policy;
    }

    private static DataASO mapInDataASO(PolicyDTO requestBody) {
        DataASO dataASO = new DataASO();
        dataASO.setId(requestBody.getId());
        dataASO.setFirstInstallment(new FirstInstallmentASO());
        return dataASO;
    }

}
