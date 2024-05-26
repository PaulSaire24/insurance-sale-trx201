package com.bbva.rbvd.lib.r201.transform.bean;

import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import com.bbva.rbvd.dto.cicsconnection.icr3.ICR3Request;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;

public class ICR3Bean {

    private ICR3Bean(){}

    public static ICR3Request mapIn(PolicyASO policyASO, String userCode, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized) {
        ICR3Request icr3Request = new ICR3Request();
        icr3Request.setNUMCON(policyASO.getData().getId());
        icr3Request.setMTDPGO(policyASO.getData().getPaymentMethod().getPaymentType());
        icr3Request.setNROCTA(policyASO.getData().getPaymentMethod().getRelatedContracts().get(0).getNumber());
        icr3Request.setCOBRO(policyASO.getData().getFirstInstallment().getIsPaymentRequired() ? PISDConstants.LETTER_SI : PISDConstants.LETTER_NO);
        icr3Request.setFORMA(indicatorPreFormalized.getValue());
        icr3Request.setOFICON(policyASO.getData().getBank().getBranch().getId());
        icr3Request.setUSUARIO(userCode);
        icr3Request.setFLGOPAY(PISDConstants.LETTER_NO);
        return icr3Request;
    }

}
