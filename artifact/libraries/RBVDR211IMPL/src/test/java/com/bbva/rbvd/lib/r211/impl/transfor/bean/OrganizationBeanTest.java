package com.bbva.rbvd.lib.r211.impl.transfor.bean;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.ContactTypeBO;
import com.bbva.pisd.dto.insurance.bo.DocumentTypeBO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.rbvd.dto.insrncsale.aso.*;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.*;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.ContactTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.PersonTypeEnum;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessContextContractAndPolicyDTO;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class OrganizationBeanTest {



    @Test
    public void setOrganizationSetsOrganizationsForRucIdStartingWith20() throws IOException {
        // Given
        EmisionBO emision = new EmisionBO();
        emision.setPayload(new PayloadEmisionBO());
        emision.getPayload().setAgregarPersona(new AgregarPersonaBO());
        emision.getPayload().getAgregarPersona().setPersona(Collections.singletonList(new PersonaBO()));

        PolicyDTO requestBody = MockData.getInstance().getCreateInsuranceRequestBody();

        CustomerBO customer = new CustomerBO();
        customer.setContactDetails(Collections.singletonList(new ContactDetailsBO()));
        customer.getContactDetails().get(0).setContactType(new ContactTypeBO());
        customer.getContactDetails().get(0).getContactType().setId(ContactTypeEnum.PHONE_NUMBER.getValue());
        customer.getContactDetails().get(0).setContact("1234567890");

        CustomerListASO customerList = new CustomerListASO();
        customerList.setData(Collections.singletonList(customer));
        customerList.getData().get(0).setIdentityDocuments(Collections.singletonList(new IdentityDocumentsBO()));
        customerList.getData().get(0).getIdentityDocuments().get(0).setDocumentType(new DocumentTypeBO());
        customerList.getData().get(0).getIdentityDocuments().get(0).getDocumentType().setId(RBVDInternalConstants.Endorsement.RUC_ID);
        customerList.getData().get(0).getIdentityDocuments().get(0).setDocumentNumber("20");


        BusinessASO business = new BusinessASO();
        business.setFormation(new FormationASO());
        business.getFormation().setCountry(new CountryASO());
        business.getFormation().getCountry().setName("USA");
        business.getFormation().setDate(new LocalDate());
        business.setLegalName("Test Business");
        business.setBusinessDocuments(Collections.singletonList(new BusinessDocumentASO()));
        business.getBusinessDocuments().get(0).setDocumentNumber("123456");
        business.setAnnualSales(new SaleASO());
        business.getAnnualSales().setStartDate(new LocalDate());
        business.setBusinessGroup(new BusinessGroupASO());
        business.getBusinessGroup().setId("1");
        business.setEconomicActivity(new EconomicActivityASO());
        business.getEconomicActivity().setId("1");

        ProcessContextContractAndPolicyDTO processPrePolicy = new ProcessContextContractAndPolicyDTO();
        processPrePolicy.setListBusinessesASO(new ListBusinessesASO());
        processPrePolicy.getListBusinessesASO().setData(Collections.singletonList(business));

        // When
        OrganizationBean.setOrganization(emision, requestBody, customerList, processPrePolicy);

        // Then
        assertNull(emision.getPayload().getAgregarPersona().getPersona());
        assertNotNull(emision.getPayload().getAgregarPersona().getOrganizacion());
    }

    /*@Test
    public void setOrganizationDoesNotSetOrganizationsForRucIdNotStartingWith20() {
        // Given
        EmisionBO emision = new EmisionBO();
        emision.setPayload(new PayloadEmisionBO());
        emision.getPayload().setAgregarPersona(new AgregarPersonaBO());
        emision.getPayload().getAgregarPersona().setPersona(Collections.singletonList(new PersonaBO()));

        PolicyDTO requestBody = new PolicyDTO();

        CustomerListASO customerList = new CustomerListASO();
        customerList.setData(Collections.singletonList(new CustomerBO()));
        customerList.getData().get(0).setIdentityDocuments(Collections.singletonList(new IdentityDocumentsBO()));
        customerList.getData().get(0).getIdentityDocuments().get(0).setDocumentType(new DocumentTypeBO());
        customerList.getData().get(0).getIdentityDocuments().get(0).getDocumentType().setId(RBVDInternalConstants.Endorsement.RUC_ID);
        customerList.getData().get(0).getIdentityDocuments().get(0).setDocumentNumber("10");

        ProcessContextContractAndPolicyDTO processPrePolicy = new ProcessContextContractAndPolicyDTO();
        processPrePolicy.setListBusinessesASO(new ListBusinessesASO());
        processPrePolicy.getListBusinessesASO().setData(Collections.singletonList(new BusinessASO()));

        // When
        OrganizationBean.setOrganization(emision, requestBody, customerList, processPrePolicy);

        // Then
        assertNotNull(emision.getPayload().getAgregarPersona().getPersona());
        assertNull(emision.getPayload().getAgregarPersona().getOrganizacion());
    }

    @Test
    public void setOrganizationDoesNotSetOrganizationsForNonRucId() {
        // Given
        EmisionBO emision = new EmisionBO();
        emision.setPayload(new PayloadEmisionBO());
        emision.getPayload().setAgregarPersona(new AgregarPersonaBO());
        emision.getPayload().getAgregarPersona().setPersona(Collections.singletonList(new PersonaBO()));

        PolicyDTO requestBody = new PolicyDTO();

        CustomerListASO customerList = new CustomerListASO();
        customerList.setData(Collections.singletonList(new CustomerBO()));
        customerList.getData().get(0).setIdentityDocuments(Collections.singletonList(new IdentityDocumentsBO()));
        customerList.getData().get(0).getIdentityDocuments().get(0).setDocumentType(new DocumentTypeBO());
        customerList.getData().get(0).getIdentityDocuments().get(0).getDocumentType().setId("nonRucId");

        ProcessContextContractAndPolicyDTO processPrePolicy = new ProcessContextContractAndPolicyDTO();
        processPrePolicy.setListBusinessesASO(new ListBusinessesASO());
        processPrePolicy.getListBusinessesASO().setData(Collections.singletonList(new BusinessASO()));

        // When
        OrganizationBean.setOrganization(emision, requestBody, customerList, processPrePolicy);

        // Then
        assertNotNull(emision.getPayload().getAgregarPersona().getPersona());
        assertNull(emision.getPayload().getAgregarPersona().getOrganizacion());
    }*/

    @Test
    public void mapOrganizationsReturnsCorrectOrganizationsForBusinessWithFormation() throws IOException {
        // Given
        BusinessASO business = new BusinessASO();
        business.setFormation(new FormationASO());
        business.getFormation().setCountry(new CountryASO());
        business.getFormation().getCountry().setName("USA");
        business.getFormation().setDate(new LocalDate());
        business.setLegalName("Test Business");
        business.setBusinessDocuments(Collections.singletonList(new BusinessDocumentASO()));
        business.getBusinessDocuments().get(0).setDocumentNumber("123456");
        business.setAnnualSales(new SaleASO());
        business.getAnnualSales().setStartDate(new LocalDate());
        business.setBusinessGroup(new BusinessGroupASO());
        business.getBusinessGroup().setId("1");
        business.setEconomicActivity(new EconomicActivityASO());
        business.getEconomicActivity().setId("1");

        PersonaBO persona = new PersonaBO();
        persona.setDireccion("Test Address");

        CustomerBO customer = new CustomerBO();
        customer.setContactDetails(Collections.singletonList(new ContactDetailsBO()));
        customer.getContactDetails().get(0).setContactType(new ContactTypeBO());
        customer.getContactDetails().get(0).getContactType().setId(ContactTypeEnum.PHONE_NUMBER.getValue());
        customer.getContactDetails().get(0).setContact("1234567890");

        PolicyDTO requestBody = MockData.getInstance().getCreateInsuranceRequestBody();

        // When
        List<OrganizacionBO> result = OrganizationBean.mapOrganizations(business, persona, customer, requestBody);

        // Then
        assertEquals(3, result.size());
        assertEquals("Test Business", result.get(0).getRazonSocial());
        assertEquals("USA", result.get(0).getPaisOrigen());
        assertEquals("989595932", result.get(0).getCelular());
    }

    @Test
    public void mapOrganizationsReturnsCorrectOrganizationsForBusinessWithoutFormation() throws IOException {
        // Given
        BusinessASO business = new BusinessASO();
        business.setLegalName("Test Business");
        business.setBusinessDocuments(Collections.singletonList(new BusinessDocumentASO()));
        business.getBusinessDocuments().get(0).setDocumentNumber("123456");

        PersonaBO persona = new PersonaBO();
        persona.setDireccion("Test Address");

        CustomerBO customer = new CustomerBO();
        customer.setContactDetails(Collections.singletonList(new ContactDetailsBO()));
        customer.getContactDetails().get(0).setContactType(new ContactTypeBO());
        customer.getContactDetails().get(0).getContactType().setId("PHONE");
        customer.getContactDetails().get(0).setContact("1234567890");


        PolicyDTO requestBody = MockData.getInstance().getCreateInsuranceRequestBody();

        // When
        List<com.bbva.rbvd.dto.insrncsale.bo.emision.OrganizacionBO> result = OrganizationBean.mapOrganizations(business, persona, customer, requestBody);

        // Then
        assertEquals(3, result.size());
        assertEquals("Test Business", result.get(0).getRazonSocial());
        assertEquals("PERU", result.get(0).getPaisOrigen());
        assertNull(result.get(0).getFechaConstitucion());
        assertEquals("989595932", result.get(0).getCelular());
    }


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