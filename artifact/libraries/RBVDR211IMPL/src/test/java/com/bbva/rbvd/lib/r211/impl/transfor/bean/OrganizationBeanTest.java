package com.bbva.rbvd.lib.r211.impl.transfor.bean;

import com.bbva.rbvd.dto.insrncsale.bo.emision.EntidadBO;
import com.bbva.rbvd.dto.insrncsale.utils.PersonTypeEnum;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrganizationBeanTest {


    @Test
    public void getPersonTypeReturnsCorrectTypeForRucIdStartingWith20() {
        // Given
        EntidadBO person = new EntidadBO();
        person.setTipoDocumento(RBVDInternalConstants.Endorsement.RUC_ID);
        person.setNroDocumento("20");

        // When
        PersonTypeEnum result = OrganizationBean.getPersonType(person);

        // Then
        assertEquals(PersonTypeEnum.JURIDIC, result);
    }

    @Test
    public void getPersonTypeReturnsCorrectTypeForRucIdNotStartingWith20() {
        // Given
        EntidadBO person = new EntidadBO();
        person.setTipoDocumento(RBVDInternalConstants.Endorsement.RUC_ID);
        person.setNroDocumento("10");

        // When
        PersonTypeEnum result = OrganizationBean.getPersonType(person);

        // Then
        assertEquals(PersonTypeEnum.NATURAL_WITH_BUSINESS, result);
    }

    @Test
    public void getPersonTypeReturnsCorrectTypeForNonRucId() {
        // Given
        EntidadBO person = new EntidadBO();
        person.setTipoDocumento("nonRucId");

        // When
        PersonTypeEnum result = OrganizationBean.getPersonType(person);

        // Then
        assertEquals(PersonTypeEnum.NATURAL, result);
    }

}