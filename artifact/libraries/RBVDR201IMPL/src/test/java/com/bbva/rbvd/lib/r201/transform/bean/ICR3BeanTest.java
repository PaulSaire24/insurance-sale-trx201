package com.bbva.rbvd.lib.r201.transform.bean;

import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import com.bbva.rbvd.dto.cicsconnection.ic.ICContract;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.mock.EntityMock;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ICR3BeanTest {
    private DataASO input ;
    private String userCode;

    @Before
    public void setUp() throws IOException {
        input = EntityMock.getInstance().buildInputCreateInsurance();
        userCode = "testUserCode";
    }

    @Test
    public void mapIn_shouldReturnICR3Request_whenPaymentIsRequired() {
        input.getFirstInstallment().setIsPaymentRequired(true);

        ICContract result = ICRBean.mapIn(input, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_S);

        assertEquals(PISDConstants.LETTER_SI, result.getCOBRO().getValue());
    }

    @Test
    public void mapIn_shouldReturnICR3Request_whenPaymentIsNotRequired() {
        input.getFirstInstallment().setIsPaymentRequired(false);

        ICContract result = ICRBean.mapIn(input,RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_S);

        assertEquals(PISDConstants.LETTER_NO, result.getCOBRO().getValue());
    }


    @Test
    public void mapIn_shouldReturnICR3Request_withCorrectPaymentType() {
        String paymentType = "testPaymentType";
        String nrocta = "2131231213";
        input.getPaymentMethod().setPaymentType(paymentType);

        ICContract result = ICRBean.mapIn(input,RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_S);
        assertEquals(paymentType, result.getMTDPGO());
        assertEquals(nrocta, result.getNROCTA());
    }
}