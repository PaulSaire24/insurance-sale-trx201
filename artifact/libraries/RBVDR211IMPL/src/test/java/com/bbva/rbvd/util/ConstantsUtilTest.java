package com.bbva.rbvd.util;

import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConstantsUtilTest {

    @Test
    public void participantTest() {
        assertEquals("ENDORSEE", ConstantsUtil.Participant.ENDORSEE);
        assertEquals("PAYMENT_MANAGER", ConstantsUtil.Participant.PAYMENT_MANAGER);
        assertEquals("INSURED", ConstantsUtil.Participant.INSURED);
        assertEquals("LEGAL_REPRESENTATIVE", ConstantsUtil.Participant.LEGAL_REPRESENTATIVE);
        assertEquals("BENEFICIARY", ConstantsUtil.Participant.BENEFICIARY);
    }

    @Test
    public void documentTypeTest() {
        assertEquals("RUC", ConstantsUtil.DocumentType.RUC);
    }

    @Test
    public void queriesTest() {
        assertEquals("PISD.SELECT_PRODUCT_BY_PRODUCT_TYPE", ConstantsUtil.Queries.QUERY_SELECT_PRODUCT_BY_PRODUCT_TYPE);
        assertEquals("PISD.GET_INSURED_DATA_LIFE", ConstantsUtil.Queries.QUERY_GET_INSURED_DATA_LIFE);
    }

    @Test
    public void delimeterTest() {
        assertEquals("|", ConstantsUtil.Delimeter.VERTICAL_BAR);
    }

    @Test
    public void numberTest() {
        assertEquals(10, ConstantsUtil.Number.DIEZ);
        assertEquals(3, ConstantsUtil.Number.TRES);
        assertEquals(2, ConstantsUtil.Number.DOS);
        assertEquals(1, ConstantsUtil.Number.UNO);
        assertEquals(0, ConstantsUtil.Number.CERO);
    }

}


