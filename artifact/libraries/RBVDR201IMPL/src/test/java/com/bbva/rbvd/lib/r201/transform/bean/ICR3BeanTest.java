package com.bbva.rbvd.lib.r201.transform.bean;

import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import com.bbva.rbvd.dto.cicsconnection.icr3.ICR3Request;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.mock.EntityMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ICR3BeanTest {
    private PolicyASO policyASO;
    private String userCode;

    @Before
    public void setUp() {
        policyASO = EntityMock.getInstance().createMockPolicyASO();
        userCode = "testUserCode";
    }

    @Test
    public void mapIn_shouldReturnICR3Request_whenPaymentIsRequired() {
        policyASO.getData().getFirstInstallment().setIsPaymentRequired(true);

        ICR3Request result = ICR3Bean.mapIn(policyASO, userCode);

        assertEquals(PISDConstants.LETTER_SI, result.getCOBRO());
    }

    @Test
    public void mapIn_shouldReturnICR3Request_whenPaymentIsNotRequired() {
        policyASO.getData().getFirstInstallment().setIsPaymentRequired(false);

        ICR3Request result = ICR3Bean.mapIn(policyASO, userCode);

        assertEquals(PISDConstants.LETTER_NO, result.getCOBRO());
    }

    @Test
    public void mapIn_shouldReturnICR3Request_withCorrectUserCode() {
        ICR3Request result = ICR3Bean.mapIn(policyASO, userCode);

        assertEquals(userCode, result.getUSUARIO());
    }

    @Test
    public void mapIn_shouldReturnICR3Request_withCorrectPaymentType() {
        String paymentType = "testPaymentType";
        String nrocta = "01230192830129830128";
        policyASO.getData().getPaymentMethod().setPaymentType(paymentType);

        ICR3Request result = ICR3Bean.mapIn(policyASO, userCode);
        assertEquals(paymentType, result.getMTDPGO());
        assertEquals(nrocta, result.getNROCTA());
    }
}