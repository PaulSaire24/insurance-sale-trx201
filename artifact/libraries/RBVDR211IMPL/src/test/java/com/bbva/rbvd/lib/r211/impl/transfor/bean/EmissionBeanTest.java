package com.bbva.rbvd.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;

import com.bbva.pisd.dto.insurance.bo.DocumentTypeBO;
import com.bbva.pisd.dto.insurance.bo.GenderBO;
import com.bbva.pisd.dto.insurance.bo.GeographicGroupTypeBO;
import com.bbva.pisd.dto.insurance.bo.GeographicGroupsBO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;

import com.bbva.pisd.dto.insurance.mock.MockDTO;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;


import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;

import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.*;

import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.commons.DocumentTypeDTO;
import com.bbva.rbvd.dto.insrncsale.commons.IdentityDocumentDTO;
import com.bbva.rbvd.dto.insrncsale.commons.PolicyInspectionDTO;

import com.bbva.rbvd.dto.insrncsale.dao.*;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;

import com.bbva.rbvd.dto.insrncsale.policy.*;

import com.bbva.rbvd.dto.insrncsale.utils.PersonTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.EmissionBean;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;


import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import java.math.BigDecimal;

import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EmissionBeanTest {

    private final MapperHelper mapperHelper = new MapperHelper();
    private ApplicationConfigurationService applicationConfigurationService;
    private final MockData mockData = MockData.getInstance();
    private final MockDTO mockDTO = MockDTO.getInstance();
    private static final String N_VALUE = "N";
    private static final String S_VALUE = "S";
    private static final String NO_EXIST = "NotExist";
    private static final String EXTERNAL_CONTRACT = "EXTERNAL_CONTRACT";
    private InsuranceContractDAO contractDao;
    private InsuranceCtrReceiptsDAO receiptDao;
    private IsrcContractMovDAO contractMovDao;
    private IsrcContractParticipantDAO participantDao;
    private RequiredFieldsEmissionDAO requiredFieldsEmissionDao;
    private PolicyDTO apxRequest;
    private PolicyASO asoResponse;
    private EmisionBO rimacResponse;
    private CustomerListASO customerList;
    private Map<String,Object> responseQueryGetProductById;
    private Map<String,Object> responseQueryGetRequiredFields;

    private RBVDR201 rbvdr201;
    private PISDR350 pisdr350;

    private ProcessPrePolicyDTO processPrePolicyDTO;

    @Before
    public void setUp() throws IOException {
        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        mapperHelper.setApplicationConfigurationService(applicationConfigurationService);

        contractDao = mock(InsuranceContractDAO.class);
        receiptDao = mock(InsuranceCtrReceiptsDAO.class);
        contractMovDao = mock(IsrcContractMovDAO.class);
        participantDao = mock(IsrcContractParticipantDAO.class);

        requiredFieldsEmissionDao = mock(RequiredFieldsEmissionDAO.class);
        when(requiredFieldsEmissionDao.getInsuranceProductId()).thenReturn(BigDecimal.valueOf(1));
        when(requiredFieldsEmissionDao.getContractDurationType()).thenReturn("M");
        when(requiredFieldsEmissionDao.getContractDurationNumber()).thenReturn(BigDecimal.valueOf(12));
        when(requiredFieldsEmissionDao.getPaymentFrequencyId()).thenReturn(BigDecimal.valueOf(1));
        when(requiredFieldsEmissionDao.getInsuranceProductDesc()).thenReturn("productDescription");
        when(requiredFieldsEmissionDao.getInsuranceModalityName()).thenReturn("insuranceModalityName");
        when(requiredFieldsEmissionDao.getInsuranceCompanyQuotaId()).thenReturn("quotaId");
        when(requiredFieldsEmissionDao.getPaymentFrequencyName()).thenReturn("frequencyName");
        when(requiredFieldsEmissionDao.getVehicleBrandName()).thenReturn("brandName");
        when(requiredFieldsEmissionDao.getVehicleModelName()).thenReturn("modelName");
        when(requiredFieldsEmissionDao.getVehicleYearId()).thenReturn("2016");
        when(requiredFieldsEmissionDao.getVehicleLicenseId()).thenReturn("LOT464");
        when(requiredFieldsEmissionDao.getGasConversionType()).thenReturn("S");
        when(requiredFieldsEmissionDao.getVehicleCirculationType()).thenReturn("L");
        when(requiredFieldsEmissionDao.getCommercialVehicleAmount()).thenReturn(BigDecimal.valueOf(9843.234));

        when(receiptDao.getEntityId()).thenReturn("entityId");
        when(receiptDao.getBranchId()).thenReturn("branchId");
        when(receiptDao.getIntAccountId()).thenReturn("intAccountId");
        when(receiptDao.getPolicyReceiptId()).thenReturn(BigDecimal.valueOf(1));
        when(receiptDao.getInsuranceCompanyId()).thenReturn(BigDecimal.valueOf(1));
        when(receiptDao.getPremiumPaymentReceiptAmount()).thenReturn(BigDecimal.valueOf(480.0));
        when(receiptDao.getFixingExchangeRateAmount()).thenReturn(BigDecimal.valueOf(123.0));
        when(receiptDao.getPremiumCurrencyExchAmount()).thenReturn(BigDecimal.valueOf(1));
        when(receiptDao.getPremiumChargeOperationId()).thenReturn("chargeOperationId");
        when(receiptDao.getCurrencyId()).thenReturn("currencyId");
        when(receiptDao.getReceiptIssueDate()).thenReturn("03/02/2021");
        when(receiptDao.getReceiptStartDate()).thenReturn("01/01/0001");
        when(receiptDao.getReceiptEndDate()).thenReturn("01/01/0001");
        when(receiptDao.getReceiptCollectionDate()).thenReturn("03/02/2021");
        when(receiptDao.getReceiptExpirationDate()).thenReturn("03/02/2021");
        when(receiptDao.getReceiptsTransmissionDate()).thenReturn("03/02/2021");
        when(receiptDao.getReceiptCollectionStatusType()).thenReturn("00");
        when(receiptDao.getInsuranceCollectionMoveId()).thenReturn("collectionMoveId");
        when(receiptDao.getPaymentMethodType()).thenReturn("T");
        when(receiptDao.getDebitAccountId()).thenReturn("debitAccountId");
        when(receiptDao.getDebitChannelType()).thenReturn("BI");
        when(receiptDao.getChargeAttemptsNumber()).thenReturn(BigDecimal.valueOf(0));
        when(receiptDao.getInsrncCoReceiptStatusType()).thenReturn("INC");
        when(receiptDao.getReceiptStatusType()).thenReturn("COB");
        when(receiptDao.getCreationUserId()).thenReturn("creationUser");
        when(receiptDao.getUserAuditId()).thenReturn("userAudit");
        when(receiptDao.getManagementBranchId()).thenReturn("branchId");
        when(receiptDao.getVariablePremiumAmount()).thenReturn(BigDecimal.valueOf(0));
        when(receiptDao.getFixPremiumAmount()).thenReturn(BigDecimal.valueOf(100.0));
        when(receiptDao.getSettlementVarPremiumAmount()).thenReturn(BigDecimal.valueOf(0));
        when(receiptDao.getSettlementFixPremiumAmount()).thenReturn(BigDecimal.valueOf(100.0));
        when(receiptDao.getLastChangeBranchId()).thenReturn("0814");
        when(receiptDao.getGlBranchId()).thenReturn("branchId");

        apxRequest = mockData.getCreateInsuranceRequestBody();
        apxRequest.setCreationUser("creationUser");
        apxRequest.setUserAudit("userAudit");
        apxRequest.setSaleChannelId("BI");
        apxRequest.setAap("13000001");

        RelationTypeDTO relationType = new RelationTypeDTO();
        relationType.setId("1");

        apxRequest.getRelatedContracts().get(1).setRelationType(relationType);
        apxRequest.getRelatedContracts().get(2).setRelationType(relationType);
        apxRequest.getRelatedContracts().get(1).getContractDetails().setNumber("00110241709612444994");
        apxRequest.getRelatedContracts().get(2).getContractDetails().setNumber("00110241709612444995");

        asoResponse = mockData.getEmisionASOResponse();
        rimacResponse = mockData.getEmisionRimacResponse();
        customerList = mockDTO.getCustomerDataResponse();
        rbvdr201 = mock(RBVDR201.class);
        pisdr350 = mock(PISDR350.class);

        CustomerListASO customerList = mockDTO.getCustomerDataResponse();
        when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);

        responseQueryGetProductById = new HashMap<>();
        responseQueryGetProductById.put("INSURANCE_BUSINESS_NAME","VIDA");
        responseQueryGetProductById.put("PRODUCT_SHORT_DESC","VIDADINAMICO");

        responseQueryGetRequiredFields = new HashMap<>();
        responseQueryGetRequiredFields.put(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue(), "DESEMPLEO_PRESTAMO");

        when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt","")).thenReturn("DESEMPLEO_PRESTAMO");


        processPrePolicyDTO = new ProcessPrePolicyDTO();

        // Setting mock data
        processPrePolicyDTO.setPolicy(new PolicyDTO(/*mock parameters*/));
        processPrePolicyDTO.setRequiredFieldsEmission(new RequiredFieldsEmissionDAO(/*mock parameters*/));
        processPrePolicyDTO.setAsoResponse(new PolicyASO(/*mock parameters*/));
        processPrePolicyDTO.setContractDao(new InsuranceContractDAO(/*mock parameters*/));

        processPrePolicyDTO.setOperationGlossaryDesc("Mock Operation Glossary Description");
        processPrePolicyDTO.setEndorsement(true);

        EmisionBO mockEmisionBORequest = new EmisionBO(/*mock parameters*/);
        processPrePolicyDTO.setRimacRequest(mockEmisionBORequest);

        processPrePolicyDTO.setQuotationId("QUOTATION12345");
        processPrePolicyDTO.setTraceId("TRACE12345");
        processPrePolicyDTO.setProductId("PRODUCT12345");

        processPrePolicyDTO.setQuotationEmailDesc("mockemail@example.com");
        processPrePolicyDTO.setQuotationCustomerPhoneDesc("123-456-7890");

        Map<String, Object> mockResponseQueryGetProductById = new HashMap<>();
        mockResponseQueryGetProductById.put("key1", "value1");
        mockResponseQueryGetProductById.put("key2", "value2");
        processPrePolicyDTO.setResponseQueryGetProductById(mockResponseQueryGetProductById);

        processPrePolicyDTO.setCustomerList(new CustomerListASO(/*mock parameters*/));
        processPrePolicyDTO.setListBusinessesASO(new ListBusinessesASO(/*mock parameters*/));

        EmisionBO mockEmisionBOResponse = new EmisionBO(/*mock parameters*/);
        processPrePolicyDTO.setRimacResponse(mockEmisionBOResponse);

        processPrePolicyDTO.setInsuranceBusinessName("Mock Insurance Business");

        List<EndosatarioBO> mockEndosatarios = new ArrayList<>();
        mockEndosatarios.add(new EndosatarioBO(/*mock parameters*/));
        mockEndosatarios.add(new EndosatarioBO(/*mock parameters*/));
        processPrePolicyDTO.setEndosatarios(mockEndosatarios);

        processPrePolicyDTO.setRimacPaymentAccount("MOCKACCOUNT12345");
    }



    private static ParticipantDTO dataInsuredParticipant(){
        ParticipantDTO participantDTO = new ParticipantDTO();

        participantDTO.setCustomerId("74857594");

        ParticipantTypeDTO participantTypeDTO = new ParticipantTypeDTO();
        participantTypeDTO.setId("INSURED");
        participantDTO.setParticipantType(participantTypeDTO);

        DocumentTypeDTO documentTypeDTO = new DocumentTypeDTO();
        documentTypeDTO.setId("PASSPORT");
        IdentityDocumentDTO identityDocumentDTO = new IdentityDocumentDTO();
        identityDocumentDTO.setDocumentType(documentTypeDTO);
        identityDocumentDTO.setNumber("39400943");
        participantDTO.setIdentityDocument(identityDocumentDTO);

        return participantDTO;
    }

    private static ParticipantDTO dataLegalRepresentativeParticipant(){
        ParticipantDTO participantDTO = new ParticipantDTO();

        ParticipantTypeDTO participantTypeDTO = new ParticipantTypeDTO();
        participantTypeDTO.setId(ConstantsUtil.Participant.LEGAL_REPRESENTATIVE);
        participantDTO.setParticipantType(participantTypeDTO);

        DocumentTypeDTO documentTypeDTO = new DocumentTypeDTO();
        documentTypeDTO.setId("RUC");
        IdentityDocumentDTO identityDocumentDTO = new IdentityDocumentDTO();
        identityDocumentDTO.setDocumentType(documentTypeDTO);
        identityDocumentDTO.setNumber("3940480943");
        participantDTO.setIdentityDocument(identityDocumentDTO);

        return participantDTO;
    }

    @Test
    public void mapRimacEmisionRequest_OK() {
        when(this.applicationConfigurationService.getProperty("RUC")).thenReturn("R");
        when(this.applicationConfigurationService.getProperty("DNI")).thenReturn("L");
        when(this.applicationConfigurationService.getProperty("MONTHLY")).thenReturn("M");
        when(this.applicationConfigurationService.getProperty("pic.code")).thenReturn("PC");
        when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt","")).thenReturn("DESEMPLEO_PRESTAMO");
        Map<String,Object> requiredFieldsEmisionBDResponse = new HashMap<>();
        requiredFieldsEmisionBDResponse.put(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue(), "jose.sandoval.tirado.contractor@bbva.com");
        requiredFieldsEmisionBDResponse.put(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue(), "993766790");
        requiredFieldsEmisionBDResponse.put(PISDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue(), "33556255");
        requiredFieldsEmisionBDResponse.put(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue(), "HOGAR_TOTAL");
        requiredFieldsEmisionBDResponse.put(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue(), "DESEMPLEO_PRESTAMO");
        EmisionBO emisionInput = new EmisionBO();
        PersonaBO persona = new PersonaBO();
        PolicyDTO requestBody = new PolicyDTO();
        requestBody.setSaleChannelId("PC");
        StringBuilder stringAddress = new StringBuilder();
        String filledAddress = mapperHelper.fillAddress(customerList, persona, stringAddress, "PC");
        DatoParticularBO datoParticular1 = new DatoParticularBO();
        datoParticular1.setCodigo("");
        datoParticular1.setEtiqueta("CANAL_TERCERO");
        datoParticular1.setValor("PC");
        DatoParticularBO datoParticular2 = new DatoParticularBO();
        datoParticular2.setCodigo("");
        datoParticular2.setEtiqueta("DATOS_DE_CUENTA");
        datoParticular2.setValor("CUENTA||***8744||PEN");
        DatoParticularBO datoParticular3 = new DatoParticularBO();
        datoParticular3.setCodigo("");
        datoParticular3.setEtiqueta("NRO_CERT_BANCO");
        datoParticular3.setValor("00117799494000007585");
        DatoParticularBO datoParticular4 = new DatoParticularBO();
        datoParticular4.setCodigo("");
        datoParticular4.setEtiqueta("OFICINA_VENTA");
        datoParticular4.setValor("7799");
        List<DatoParticularBO> datosParticulares = new ArrayList<>();
        datosParticulares.add(datoParticular1);
        datosParticulares.add(datoParticular2);
        datosParticulares.add(datoParticular3);
        datosParticulares.add(datoParticular4);
        PayloadEmisionBO payload = new PayloadEmisionBO();
        payload.setEnvioElectronico("N");
        payload.setIndCobro("N");
        payload.setIndInspeccion(Long.valueOf(1));
        payload.setIndValidaciones("N");
        ContactoInspeccionBO contactoInspeccion = new ContactoInspeccionBO();
        contactoInspeccion.setNombre("Jose Sandoval");
        contactoInspeccion.setCorreo("jose.sandoval.tirado.contractor@bbva.com");
        contactoInspeccion.setTelefono("993766790");
        payload.setContactoInspeccion(contactoInspeccion);
        emisionInput.setPayload(payload);
        emisionInput.getPayload().setDatosParticulares(datosParticulares);

        DocumentTypeBO documentTypeBO1 = new DocumentTypeBO();
        documentTypeBO1.setId("DNI");
        IdentityDocumentsBO identityDocumentsBO1 = new IdentityDocumentsBO();
        identityDocumentsBO1.setDocumentType(documentTypeBO1);
        identityDocumentsBO1.setDocumentNumber("75485245");
        List<IdentityDocumentsBO> identityDocumentsBOs1 = new ArrayList<>();
        identityDocumentsBOs1.add(identityDocumentsBO1);
        customerList.getData().get(0).setSecondLastName("An");
        customerList.getData().get(0).setIdentityDocuments(identityDocumentsBOs1);

        EmisionBO validation1 = EmissionBean.toRequestGeneralBodyRimac(emisionInput, apxRequest,processPrePolicyDTO,customerList,this.applicationConfigurationService,"DESEMPLEO");
        when(filledAddress).thenReturn("JR. UNION 233, URB UNION ");
        assertNotNull(validation1);

        DocumentTypeBO documentTypeBO2 = new DocumentTypeBO();
        documentTypeBO2.setId("ruc");
        IdentityDocumentsBO identityDocumentsBO2 = new IdentityDocumentsBO();
        identityDocumentsBO2.setDocumentType(documentTypeBO2);
        identityDocumentsBO2.setDocumentNumber("3355415");
        List<IdentityDocumentsBO> identityDocumentsBOs2 = new ArrayList<>();
        identityDocumentsBOs2.add(identityDocumentsBO2);
        customerList.getData().get(0).setIdentityDocuments(identityDocumentsBOs2);
        GenderBO gender = new GenderBO();
        gender.setId("FEMALE");
        customerList.getData().get(0).setGender(gender);
        customerList.getData().get(0).setSecondLastName("S");

        GeographicGroupsBO geographicGroupsBO1 = new GeographicGroupsBO();
        geographicGroupsBO1.setName("CIRCUNVALACION BRENE");
        GeographicGroupTypeBO geographicGroupTypeBO1 = new GeographicGroupTypeBO();
        geographicGroupTypeBO1.setId("UNCATEGORIZED");
        geographicGroupTypeBO1.setName("UNCATEGORIZED");
        geographicGroupsBO1.setGeographicGroupType(geographicGroupTypeBO1);

        GeographicGroupsBO geographicGroupsBO2 = new GeographicGroupsBO();
        geographicGroupsBO2.setName("LOS NARANJOS");
        GeographicGroupTypeBO geographicGroupTypeBO2 = new GeographicGroupTypeBO();
        geographicGroupTypeBO2.setId("AAHH");
        geographicGroupTypeBO2.setName("ASENTAMIENTO HUMANO");
        geographicGroupsBO2.setGeographicGroupType(geographicGroupTypeBO2);

        GeographicGroupsBO geographicGroupsDepartment = new GeographicGroupsBO();
        geographicGroupsDepartment.setName("LIMA");
        GeographicGroupTypeBO geographicGroupTypeDepartment = new GeographicGroupTypeBO();
        geographicGroupTypeDepartment.setId("DEPARTMENT");
        geographicGroupTypeDepartment.setName("DEPARTMENT");
        geographicGroupsDepartment.setGeographicGroupType(geographicGroupTypeDepartment);
        geographicGroupsDepartment.setCode("01");

        GeographicGroupsBO geographicGroupsProvince = new GeographicGroupsBO();
        geographicGroupsProvince.setName("LIMA");
        GeographicGroupTypeBO geographicGroupTypeProvince = new GeographicGroupTypeBO();
        geographicGroupTypeProvince.setId("PROVINCE");
        geographicGroupTypeProvince.setName("PROVINCE");
        geographicGroupsProvince.setGeographicGroupType(geographicGroupTypeProvince);
        geographicGroupsProvince.setCode("01");

        GeographicGroupsBO geographicGroupsDistrict = new GeographicGroupsBO();
        geographicGroupsDistrict.setName("CHORRILLOS");
        GeographicGroupTypeBO geographicGroupTypeDistrict = new GeographicGroupTypeBO();
        geographicGroupTypeDistrict.setId("DISTRICT");
        geographicGroupTypeDistrict.setName("DISTRICT");
        geographicGroupsDistrict.setGeographicGroupType(geographicGroupTypeDistrict);
        geographicGroupsDistrict.setCode("009");

        GeographicGroupsBO geographicGroupsExteriorNumber = new GeographicGroupsBO();
        geographicGroupsExteriorNumber.setName("200");
        GeographicGroupTypeBO geographicGroupTypeExteriorNumber = new GeographicGroupTypeBO();
        geographicGroupTypeExteriorNumber.setId("EXTERIOR_NUMBER");
        geographicGroupTypeExteriorNumber.setName("EXTERIOR_NUMBER");
        geographicGroupsExteriorNumber.setGeographicGroupType(geographicGroupTypeExteriorNumber);

        GeographicGroupsBO geographicGroupsUbigeo = new GeographicGroupsBO();
        GeographicGroupTypeBO geographicGroupTypeUbigeo = new GeographicGroupTypeBO();
        geographicGroupTypeUbigeo.setId("UBIGEO");
        geographicGroupTypeUbigeo.setName("UBIGEO");
        geographicGroupsUbigeo.setGeographicGroupType(geographicGroupTypeUbigeo);
        geographicGroupsUbigeo.setCode("0101009");

        List<GeographicGroupsBO> geographicGroupsBOs = new ArrayList<>();
        geographicGroupsBOs.add(geographicGroupsBO1);
        geographicGroupsBOs.add(geographicGroupsBO2);
        geographicGroupsBOs.add(geographicGroupsDepartment);
        geographicGroupsBOs.add(geographicGroupsProvince);
        geographicGroupsBOs.add(geographicGroupsDistrict);
        geographicGroupsBOs.add(geographicGroupsExteriorNumber);
        geographicGroupsBOs.add(geographicGroupsUbigeo);

        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs);
        when(this.applicationConfigurationService.getProperty("RUC")).thenReturn("RC");
        EmisionBO validation2 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);
        assertNotNull(validation2);
        customerList.getData().get(0).setGender(null);

        GeographicGroupsBO geographicGroupsBO3 = new GeographicGroupsBO();
        geographicGroupsBO3.setName("FONAVI UNO");
        GeographicGroupTypeBO geographicGroupTypeBO3 = new GeographicGroupTypeBO();
        geographicGroupTypeBO3.setId("STREET");
        geographicGroupTypeBO3.setName("CAL");
        geographicGroupsBO3.setGeographicGroupType(geographicGroupTypeBO3);

        GeographicGroupsBO geographicGroupsBO4 = new GeographicGroupsBO();
        geographicGroupsBO4.setName("UNCATEGORIZED");
        GeographicGroupTypeBO geographicGroupTypeBO4 = new GeographicGroupTypeBO();
        geographicGroupTypeBO4.setId("UNCATEGORIZED");
        geographicGroupTypeBO4.setName("UNCATEGORIZED");
        geographicGroupsBO4.setGeographicGroupType(geographicGroupTypeBO4);

        GeographicGroupsBO geographicGroupsDepartment1 = new GeographicGroupsBO();
        geographicGroupsDepartment1.setName("HUANUCO");
        GeographicGroupTypeBO geographicGroupTypeDepartment1 = new GeographicGroupTypeBO();
        geographicGroupTypeDepartment1.setId("DEPARTMENT");
        geographicGroupTypeDepartment1.setName("DEPARTMENT");
        geographicGroupsDepartment1.setGeographicGroupType(geographicGroupTypeDepartment1);
        geographicGroupsDepartment1.setCode("01");

        GeographicGroupsBO geographicGroupsProvince1 = new GeographicGroupsBO();
        geographicGroupsProvince1.setName("HUANUCO");
        GeographicGroupTypeBO geographicGroupTypeProvince1 = new GeographicGroupTypeBO();
        geographicGroupTypeProvince1.setId("PROVINCE");
        geographicGroupTypeProvince1.setName("PROVINCE");
        geographicGroupsProvince1.setGeographicGroupType(geographicGroupTypeProvince1);
        geographicGroupsProvince1.setCode("01");

        GeographicGroupsBO geographicGroupsDistrict1 = new GeographicGroupsBO();
        geographicGroupsDistrict1.setName("HUANUCO");
        GeographicGroupTypeBO geographicGroupTypeDistrict1 = new GeographicGroupTypeBO();
        geographicGroupTypeDistrict1.setId("DISTRICT");
        geographicGroupTypeDistrict1.setName("DISTRICT");
        geographicGroupsDistrict1.setGeographicGroupType(geographicGroupTypeDistrict1);
        geographicGroupsDistrict1.setCode("103");

        GeographicGroupsBO geographicGroupsUbigeo1 = new GeographicGroupsBO();
        GeographicGroupTypeBO geographicGroupTypeUbigeo1 = new GeographicGroupTypeBO();
        geographicGroupTypeUbigeo1.setId("UBIGEO");
        geographicGroupTypeUbigeo1.setName("UBIGEO");
        geographicGroupsUbigeo1.setGeographicGroupType(geographicGroupTypeUbigeo1);
        geographicGroupsUbigeo1.setCode("0101103");

        GeographicGroupsBO geographicGroupsExteriorNumber1 = new GeographicGroupsBO();
        geographicGroupsExteriorNumber1.setName("52");
        GeographicGroupTypeBO geographicGroupTypeExteriorNumber1 = new GeographicGroupTypeBO();
        geographicGroupTypeExteriorNumber1.setId("EXTERIOR_NUMBER");
        geographicGroupTypeExteriorNumber1.setName("EXTERIOR_NUMBER");
        geographicGroupsExteriorNumber1.setGeographicGroupType(geographicGroupTypeExteriorNumber1);

        List<GeographicGroupsBO> geographicGroupsBOs1 = new ArrayList<>();
        geographicGroupsBOs1.add(geographicGroupsBO3);
        geographicGroupsBOs1.add(geographicGroupsBO4);
        geographicGroupsBOs1.add(geographicGroupsDepartment1);
        geographicGroupsBOs1.add(geographicGroupsProvince1);
        geographicGroupsBOs1.add(geographicGroupsDistrict1);
        geographicGroupsBOs1.add(geographicGroupsUbigeo1);
        geographicGroupsBOs1.add(geographicGroupsExteriorNumber1);

        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs1);
        EmisionBO validation3 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);
        assertNotNull(validation3);

        GeographicGroupsBO geographicGroupsBO5 = new GeographicGroupsBO();
        geographicGroupsBO5.setName("-");
        GeographicGroupTypeBO geographicGroupTypeBO5 = new GeographicGroupTypeBO();
        geographicGroupTypeBO5.setId("JIRON");
        geographicGroupTypeBO5.setName("JR.");
        geographicGroupsBO5.setGeographicGroupType(geographicGroupTypeBO5);

        GeographicGroupsBO geographicGroupsBO6 = new GeographicGroupsBO();
        geographicGroupsBO6.setName("LOS TEMPLOS");
        GeographicGroupTypeBO geographicGroupTypeBO6 = new GeographicGroupTypeBO();
        geographicGroupTypeBO6.setId("RESIDENTIAL");
        geographicGroupTypeBO6.setName("RES");
        geographicGroupsBO6.setGeographicGroupType(geographicGroupTypeBO6);

        GeographicGroupsBO geographicGroupsBO7 = new GeographicGroupsBO();
        geographicGroupsBO7.setName("56");
        GeographicGroupTypeBO geographicGroupTypeBO7 = new GeographicGroupTypeBO();
        geographicGroupTypeBO7.setId("BLOCK");
        geographicGroupTypeBO7.setName("BLOCK");
        geographicGroupsBO7.setGeographicGroupType(geographicGroupTypeBO7);

        GeographicGroupsBO geographicGroupsBO8 = new GeographicGroupsBO();
        geographicGroupsBO8.setName("7");
        GeographicGroupTypeBO geographicGroupTypeBO8 = new GeographicGroupTypeBO();
        geographicGroupTypeBO8.setId("LOT");
        geographicGroupTypeBO8.setName("LOT");
        geographicGroupsBO8.setGeographicGroupType(geographicGroupTypeBO8);

        GeographicGroupsBO geographicGroupsExteriorNumber2 = new GeographicGroupsBO();
        geographicGroupsExteriorNumber2.setName(NO_EXIST);
        GeographicGroupTypeBO geographicGroupTypeExteriorNumber2 = new GeographicGroupTypeBO();
        geographicGroupTypeExteriorNumber2.setId("EXTERIOR_NUMBER");
        geographicGroupTypeExteriorNumber2.setName(NO_EXIST);
        geographicGroupsExteriorNumber2.setGeographicGroupType(geographicGroupTypeExteriorNumber2);

        List<GeographicGroupsBO> geographicGroupsBOs2 = new ArrayList<>();
        geographicGroupsBOs2.add(geographicGroupsBO5);
        geographicGroupsBOs2.add(geographicGroupsBO6);
        geographicGroupsBOs2.add(geographicGroupsBO7);
        geographicGroupsBOs2.add(geographicGroupsBO8);
        geographicGroupsBOs2.add(geographicGroupsExteriorNumber2);
        geographicGroupsBOs2.add(geographicGroupsDepartment1);
        geographicGroupsBOs2.add(geographicGroupsProvince1);
        geographicGroupsBOs2.add(geographicGroupsDistrict1);
        geographicGroupsBOs2.add(geographicGroupsUbigeo1);

        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs2);
        EmisionBO validation4 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);
        assertNotNull(validation4);

        apxRequest.setHolder(null);
        EmisionBO validation5 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);
        assertNotNull(validation5);

        GeographicGroupsBO geographicGroupsBO9 = new GeographicGroupsBO();
        geographicGroupsBO9.setName("PLAZA NORTE");
        GeographicGroupTypeBO geographicGroupTypeBO9 = new GeographicGroupTypeBO();
        geographicGroupTypeBO9.setId("SQUARE");
        geographicGroupTypeBO9.setName("PLZ");
        geographicGroupsBO9.setGeographicGroupType(geographicGroupTypeBO9);

        List<GeographicGroupsBO> geographicGroupsBOs3 = new ArrayList<>();
        geographicGroupsBOs3.add(geographicGroupsBO1);
        geographicGroupsBOs3.add(geographicGroupsBO9);
        geographicGroupsBOs3.add(geographicGroupsExteriorNumber2);
        geographicGroupsBOs3.add(geographicGroupsDepartment1);
        geographicGroupsBOs3.add(geographicGroupsProvince1);
        geographicGroupsBOs3.add(geographicGroupsDistrict1);
        geographicGroupsBOs3.add(geographicGroupsUbigeo1);

        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs3);
        EmisionBO validation6 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);
        assertNotNull(validation6);

        GeographicGroupsBO geographicGroupsBO10 = new GeographicGroupsBO();
        geographicGroupsBO10.setName("LOS GIRASOLES");
        GeographicGroupTypeBO geographicGroupTypeBO10 = new GeographicGroupTypeBO();
        geographicGroupTypeBO10.setId("URBANIZATION");
        geographicGroupTypeBO10.setName("URB");
        geographicGroupsBO10.setGeographicGroupType(geographicGroupTypeBO10);

        List<GeographicGroupsBO> geographicGroupsBOs4 = new ArrayList<>();
        geographicGroupsBOs4.add(geographicGroupsBO1);
        geographicGroupsBOs4.add(geographicGroupsBO10);
        geographicGroupsBOs4.add(geographicGroupsBO7);
        geographicGroupsBOs4.add(geographicGroupsBO8);
        geographicGroupsBOs4.add(geographicGroupsExteriorNumber2);
        geographicGroupsBOs4.add(geographicGroupsDepartment1);
        geographicGroupsBOs4.add(geographicGroupsProvince1);
        geographicGroupsBOs4.add(geographicGroupsDistrict1);
        geographicGroupsBOs4.add(geographicGroupsUbigeo1);

        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs4);
        EmisionBO validation7 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);
        assertNotNull(validation7);

        List<GeographicGroupsBO> geographicGroupsBOs5 = new ArrayList<>();
        geographicGroupsBOs5.add(geographicGroupsBO1);
        geographicGroupsBOs5.add(geographicGroupsBO6);
        geographicGroupsBOs5.add(geographicGroupsBO7);
        geographicGroupsBOs5.add(geographicGroupsBO8);
        geographicGroupsBOs5.add(geographicGroupsExteriorNumber2);
        geographicGroupsBOs5.add(geographicGroupsDepartment1);
        geographicGroupsBOs5.add(geographicGroupsProvince1);
        geographicGroupsBOs5.add(geographicGroupsDistrict1);
        geographicGroupsBOs5.add(geographicGroupsUbigeo1);

        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs5);
        EmisionBO validation8 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);
        assertNotNull(validation8);

        List<GeographicGroupsBO> geographicGroupsBOs6 = new ArrayList<>();
        geographicGroupsBOs6.add(geographicGroupsBO1);
        geographicGroupsBOs6.add(geographicGroupsBO6);
        geographicGroupsBOs6.add(geographicGroupsExteriorNumber1);
        geographicGroupsBOs6.add(geographicGroupsDepartment1);
        geographicGroupsBOs6.add(geographicGroupsProvince1);
        geographicGroupsBOs6.add(geographicGroupsDistrict1);
        geographicGroupsBOs6.add(geographicGroupsUbigeo1);

        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs6);
        EmisionBO validation9 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);
        assertNotNull(validation9);

        apxRequest.setSaleChannelId("PC");
        when(this.applicationConfigurationService.getProperty("pic.code")).thenReturn("PC");

        GeographicGroupsBO geographicGroupsBONull = new GeographicGroupsBO();
        geographicGroupsBONull.setName("xxxxx");
        GeographicGroupTypeBO geographicGroupTypeBONull = new GeographicGroupTypeBO();
        geographicGroupTypeBONull.setId("xxxxx");
        geographicGroupTypeBONull.setName("xxxxx");
        geographicGroupsBONull.setGeographicGroupType(geographicGroupTypeBONull);

        GeographicGroupsBO geographicGroupsBONull1 = new GeographicGroupsBO();
        geographicGroupsBONull1.setName("xxxxx");
        GeographicGroupTypeBO geographicGroupTypeBONull1 = new GeographicGroupTypeBO();
        geographicGroupTypeBONull1.setId("xxxxx");
        geographicGroupTypeBONull1.setName("xxxxx");
        geographicGroupsBONull1.setGeographicGroupType(geographicGroupTypeBONull1);

        List<GeographicGroupsBO> geographicGroupsBOsNull = new ArrayList<>();
        geographicGroupsBOsNull.add(geographicGroupsBONull);
        geographicGroupsBOsNull.add(geographicGroupsBONull1);
        geographicGroupsBOsNull.add(geographicGroupsExteriorNumber2);
        geographicGroupsBOsNull.add(geographicGroupsDepartment1);
        geographicGroupsBOsNull.add(geographicGroupsProvince1);
        geographicGroupsBOsNull.add(geographicGroupsDistrict1);
        geographicGroupsBOsNull.add(geographicGroupsUbigeo1);

        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOsNull);
        try {
            EmisionBO validationNull = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);
            fail("Se esperaba una BusinessException, pero no se lanzó.");
        } catch (BusinessException e) {
            assertNotNull(e);
        }

        apxRequest.setSaleChannelId("CC");
        when(this.applicationConfigurationService.getProperty("pic.code")).thenReturn("PC");

        GeographicGroupsBO nullChannel = new GeographicGroupsBO();
        nullChannel.setName("xxxxx");
        GeographicGroupTypeBO nullTypeChannel = new GeographicGroupTypeBO();
        nullTypeChannel.setId("xxxxx");
        nullTypeChannel.setName("xxxxx");
        geographicGroupsBONull.setGeographicGroupType(nullTypeChannel);

        GeographicGroupsBO nullChannel1 = new GeographicGroupsBO();
        nullChannel1.setName("xxxxx");
        GeographicGroupTypeBO nullTypeChannel1 = new GeographicGroupTypeBO();
        nullTypeChannel1.setId("xxxxx");
        nullTypeChannel1.setName("xxxxx");
        geographicGroupsBONull1.setGeographicGroupType(nullTypeChannel1);

        List<GeographicGroupsBO> geographicGroupsNull = new ArrayList<>();
        geographicGroupsNull.add(nullChannel);
        geographicGroupsNull.add(nullChannel1);
        geographicGroupsNull.add(geographicGroupsExteriorNumber2);
        geographicGroupsNull.add(geographicGroupsDepartment1);
        geographicGroupsNull.add(geographicGroupsProvince1);
        geographicGroupsNull.add(geographicGroupsDistrict1);
        geographicGroupsNull.add(geographicGroupsUbigeo1);

        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOsNull);

        EmisionBO validationNull = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse,responseQueryGetProductById, customerList);

        assertNotNull(validationNull);

        List<GeographicGroupsBO> geographicGroupsAddress = new ArrayList<>();
        GeographicGroupsBO geographicGroupBlock = new GeographicGroupsBO();
        GeographicGroupTypeBO geographicGroupTypeBlock = new GeographicGroupTypeBO();
        geographicGroupTypeBlock.setId("BLOCK");
        geographicGroupBlock.setGeographicGroupType(geographicGroupTypeBlock);
        geographicGroupBlock.setName("10");
        GeographicGroupsBO geographicGroupLot = new GeographicGroupsBO();
        GeographicGroupTypeBO geographicGroupTypeLot = new GeographicGroupTypeBO();
        geographicGroupTypeLot.setId("LOT");
        geographicGroupLot.setGeographicGroupType(geographicGroupTypeLot);
        geographicGroupLot.setName("2");
        geographicGroupsAddress.add(geographicGroupBlock);
        geographicGroupsAddress.add(geographicGroupLot);
        mapperHelper.fillAddressAditional(geographicGroupsAddress, stringAddress);
        assertEquals("10 2", stringAddress.toString());

        stringAddress.setLength(0);
        geographicGroupsAddress.clear();
        GeographicGroupsBO geographicGroupBlock2 = new GeographicGroupsBO();
        GeographicGroupTypeBO geographicGroupTypeBlock2 = new GeographicGroupTypeBO();
        geographicGroupTypeBlock2.setId("BLOCK");
        geographicGroupBlock2.setGeographicGroupType(geographicGroupTypeBlock2);
        geographicGroupBlock2.setName("23");
        geographicGroupsAddress.add(geographicGroupBlock2);
        mapperHelper.fillAddressAditional(geographicGroupsAddress, stringAddress);
        assertEquals("23", stringAddress.toString());

        stringAddress.setLength(0);
        geographicGroupsAddress.clear();
        GeographicGroupsBO geographicGroupLot2 = new GeographicGroupsBO();
        GeographicGroupTypeBO geographicGroupTypeLot2 = new GeographicGroupTypeBO();
        geographicGroupTypeLot2.setId("LOT");
        geographicGroupLot2.setGeographicGroupType(geographicGroupTypeLot2);
        geographicGroupLot2.setName("52");
        geographicGroupsAddress.add(geographicGroupLot2);
        mapperHelper.fillAddressAditional(geographicGroupsAddress, stringAddress);
        assertEquals("52", stringAddress.toString());

        stringAddress.setLength(0);
        geographicGroupsAddress.clear();
        GeographicGroupsBO geographicGroup2 = new GeographicGroupsBO();
        GeographicGroupTypeBO geographicGroupType2 = new GeographicGroupTypeBO();
        geographicGroupType2.setId("QUINTA");
        geographicGroup2.setGeographicGroupType(geographicGroupType2);
        geographicGroup2.setName("QUINTA");
        geographicGroupsAddress.add(geographicGroup2);
        String result1 = mapperHelper.fillAddressOther(geographicGroupsAddress, stringAddress);
        assertEquals("QUINTA-QUINTA", result1);
        assertEquals("QUINTA QUINTA", stringAddress.toString());

        stringAddress.setLength(0);
        geographicGroupsAddress.clear();
        GeographicGroupsBO geographicGroup3 = new GeographicGroupsBO();
        GeographicGroupTypeBO geographicGroupType3 = new GeographicGroupTypeBO();
        geographicGroupType3.setId("FLOOR");
        geographicGroup3.setGeographicGroupType(geographicGroupType3);
        geographicGroup3.setName("PISO");

        geographicGroupsAddress.add(geographicGroup3);

        String result2 = mapperHelper.fillAddressOther(geographicGroupsAddress, stringAddress);
        assertEquals("PISO-PISO", result2);
        assertEquals("PISO PISO", stringAddress.toString());

    }



}