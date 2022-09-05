package com.bbva.rbvd.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.bo.DocumentTypeBO;
import com.bbva.pisd.dto.insurance.bo.GenderBO;
import com.bbva.pisd.dto.insurance.bo.GeographicGroupTypeBO;
import com.bbva.pisd.dto.insurance.bo.GeographicGroupsBO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;

import com.bbva.pisd.dto.insurance.mock.MockDTO;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.homeinsrc.dao.SimltInsuredHousingDAO;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.*;

import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.commons.DocumentTypeDTO;
import com.bbva.rbvd.dto.insrncsale.commons.IdentityDocumentDTO;
import com.bbva.rbvd.dto.insrncsale.commons.PolicyInspectionDTO;

import com.bbva.rbvd.dto.insrncsale.dao.*;

import com.bbva.rbvd.dto.insrncsale.mock.MockData;

import com.bbva.rbvd.dto.insrncsale.policy.*;

import com.bbva.rbvd.dto.insrncsale.utils.PersonTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

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

import static org.mockito.Mockito.*;

public class MapperHelperTest {

    private final MapperHelper mapperHelper = new MapperHelper();
    private ApplicationConfigurationService applicationConfigurationService;
    private final MockData mockData = MockData.getInstance();
    private final MockDTO mockDTO = MockDTO.getInstance();
    private static final String N_VALUE = "N";
    private static final String S_VALUE = "S";

    private InsuranceContractDAO contractDao;
    private InsuranceCtrReceiptsDAO receiptDao;
    private IsrcContractMovDAO contractMovDao;
    private IsrcContractParticipantDAO participantDao;
    private RequiredFieldsEmissionDAO requiredFieldsEmissionDao;
    private PolicyDTO apxRequest;
    private PolicyASO asoResponse;
    private EmisionBO rimacResponse;
    private CustomerListASO customerList;

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
        asoResponse = mockData.getEmisionASOResponse();
        rimacResponse = mockData.getEmisionRimacResponse();
        customerList = mockDTO.getCustomerDataResponse();
    }

    @Test
    public void buildAsoRequest_OK() {
        DataASO validation = mapperHelper.buildAsoRequest(apxRequest);

        assertNotNull(validation.getQuotationId());
        assertNotNull(validation.getProductId());
        assertNotNull(validation.getProductPlan());
        assertNotNull(validation.getProductPlan().getId());
        assertNotNull(validation.getPaymentMethod());
        assertNotNull(validation.getPaymentMethod().getPaymentType());
        assertNotNull(validation.getPaymentMethod().getInstallmentFrequency());
        assertFalse(validation.getPaymentMethod().getRelatedContracts().isEmpty());
        assertNotNull(validation.getPaymentMethod().getRelatedContracts().get(0).getNumber());
        assertNotNull(validation.getPaymentMethod().getRelatedContracts().get(0).getProduct());
        assertNotNull(validation.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId());
        assertNotNull(validation.getValidityPeriod());
        assertNotNull(validation.getValidityPeriod().getStartDate());
        assertNotNull(validation.getTotalAmount());
        assertNotNull(validation.getTotalAmount().getAmount());
        assertNotNull(validation.getTotalAmount().getCurrency());
        assertNotNull(validation.getInsuredAmount());
        assertNotNull(validation.getInsuredAmount().getAmount());
        assertNotNull(validation.getInsuredAmount().getCurrency());
        assertNotNull(validation.getHolder());
        assertNotNull(validation.getHolder().getIdentityDocument());
        assertNotNull(validation.getHolder().getIdentityDocument().getDocumentType());
        assertNotNull(validation.getHolder().getIdentityDocument().getDocumentType().getId());
        assertNotNull(validation.getHolder().getIdentityDocument().getNumber());
        assertNotNull(validation.getInstallmentPlan());
        assertNotNull(validation.getInstallmentPlan().getStartDate());
        assertNotNull(validation.getInstallmentPlan().getMaturityDate());
        assertNotNull(validation.getInstallmentPlan().getTotalNumberInstallments());
        assertNotNull(validation.getInstallmentPlan().getPeriod());
        assertNotNull(validation.getInstallmentPlan().getPeriod().getId());
        assertNotNull(validation.getInstallmentPlan().getPaymentAmount());
        assertNotNull(validation.getInstallmentPlan().getPaymentAmount().getAmount());
        assertNotNull(validation.getInstallmentPlan().getPaymentAmount().getCurrency());
        assertNotNull(validation.getFirstInstallment());
        assertFalse(validation.getFirstInstallment().getIsPaymentRequired());
        assertFalse(validation.getParticipants().isEmpty());
        assertNotNull(validation.getParticipants().get(0).getParticipantType());
        assertNotNull(validation.getParticipants().get(0).getParticipantType().getId());
        assertNotNull(validation.getParticipants().get(0).getCustomerId());
        assertNotNull(validation.getParticipants().get(0).getIdentityDocument());
        assertNotNull(validation.getParticipants().get(0).getIdentityDocument().getDocumentType());
        assertNotNull(validation.getParticipants().get(0).getIdentityDocument().getDocumentType().getId());
        assertNotNull(validation.getParticipants().get(0).getIdentityDocument().getNumber());
        assertNotNull(validation.getBusinessAgent());
        assertNotNull(validation.getBusinessAgent().getId());
        assertNotNull(validation.getPromoter());
        assertNotNull(validation.getPromoter().getId());
        assertNotNull(validation.getBank());
        assertNotNull(validation.getBank().getId());
        assertNotNull(validation.getBank().getBranch());
        assertNotNull(validation.getBank().getBranch().getId());
        assertNotNull(validation.getInsuranceCompany());
        assertNotNull(validation.getInsuranceCompany().getId());

        /*Faltan escenarios null con BA y PROMOTER*/
    }

    @Test
    public void buildRequestBodyRimac_OK() {
        PolicyInspectionDTO inspection = apxRequest.getInspection();
        EmisionBO validation = mapperHelper.buildRequestBodyRimac(inspection, "secondValue", "channelCode",
                "dataId", "saleOffice");

        assertNull(validation.getPayload().getContactoInspeccion());

        assertFalse(validation.getPayload().getDatosParticulares().isEmpty());
        assertNotNull(validation.getPayload().getDatosParticulares().get(0).getEtiqueta());
        assertNotNull(validation.getPayload().getDatosParticulares().get(0).getCodigo());
        assertNotNull(validation.getPayload().getDatosParticulares().get(0).getValor());
        assertNotNull(validation.getPayload().getDatosParticulares().get(1).getEtiqueta());
        assertNotNull(validation.getPayload().getDatosParticulares().get(1).getCodigo());
        assertNotNull(validation.getPayload().getDatosParticulares().get(1).getValor());
        assertNotNull(validation.getPayload().getDatosParticulares().get(2).getEtiqueta());
        assertNotNull(validation.getPayload().getDatosParticulares().get(2).getCodigo());
        assertNotNull(validation.getPayload().getDatosParticulares().get(2).getValor());
        assertNotNull(validation.getPayload().getDatosParticulares().get(3).getEtiqueta());
        assertNotNull(validation.getPayload().getDatosParticulares().get(3).getCodigo());
        assertNotNull(validation.getPayload().getDatosParticulares().get(3).getValor());


        assertNotNull(validation.getPayload().getEnvioElectronico());
        assertNotNull(validation.getPayload().getIndCobro());
        assertNotNull(validation.getPayload().getIndInspeccion());
        assertNotNull(validation.getPayload().getIndValidaciones());


        assertEquals("CANAL_TERCERO", validation.getPayload().getDatosParticulares().get(0).getEtiqueta());
        assertEquals("channelCode", validation.getPayload().getDatosParticulares().get(0).getValor());
        assertEquals("DATOS_DE_CUENTA", validation.getPayload().getDatosParticulares().get(1).getEtiqueta());
        assertEquals("secondValue", validation.getPayload().getDatosParticulares().get(1).getValor());
        assertEquals("NRO_CERT_BANCO", validation.getPayload().getDatosParticulares().get(2).getEtiqueta());
        assertEquals("dataId", validation.getPayload().getDatosParticulares().get(2).getValor());
        assertEquals("OFICINA_VENTA", validation.getPayload().getDatosParticulares().get(3).getEtiqueta());
        assertEquals("saleOffice", validation.getPayload().getDatosParticulares().get(3).getValor());
        assertEquals(N_VALUE, validation.getPayload().getEnvioElectronico());
        assertEquals(N_VALUE, validation.getPayload().getIndCobro());
        assertEquals(Optional.of(0L).get(), validation.getPayload().getIndInspeccion());
        assertEquals(N_VALUE, validation.getPayload().getIndValidaciones());

        apxRequest.getInspection().setIsRequired(true);

        validation = mapperHelper.buildRequestBodyRimac(inspection, "secondValue", "channelCode",
                "dataId", "saleOffice");

        assertNotNull(validation.getPayload().getContactoInspeccion());
        assertNotNull(validation.getPayload().getContactoInspeccion().getNombre());
        assertNotNull(validation.getPayload().getContactoInspeccion().getCorreo());
        assertNotNull(validation.getPayload().getContactoInspeccion().getTelefono());
        assertEquals(inspection.getFullName(), validation.getPayload().getContactoInspeccion().getNombre());
        assertEquals(inspection.getContactDetails().get(0).getContact().getAddress(), validation.getPayload().getContactoInspeccion().getCorreo());
        assertEquals(inspection.getContactDetails().get(1).getContact().getPhoneNumber(), validation.getPayload().getContactoInspeccion().getTelefono());
        assertEquals(Optional.of(1L).get(), validation.getPayload().getIndInspeccion());

        PolicyInspectionDTO inspectionDTO = new PolicyInspectionDTO();
        inspectionDTO.setIsRequired(true);
        inspectionDTO.setFullName("Luis Estrada");
        inspectionDTO.setContactDetails(new ArrayList<ContactDetailDTO>());
        validation = mapperHelper.buildRequestBodyRimac(inspectionDTO, "secondValue", "channelCode",
                "dataId", "saleOffice");

        assertNotNull(validation.getPayload().getContactoInspeccion());
        assertNotNull(validation.getPayload().getContactoInspeccion().getNombre());
        assertNull(validation.getPayload().getContactoInspeccion().getCorreo());
        assertNull(validation.getPayload().getContactoInspeccion().getTelefono());
        assertEquals(inspection.getFullName(), validation.getPayload().getContactoInspeccion().getNombre());
        assertEquals(Optional.of(1L).get(), validation.getPayload().getIndInspeccion());
    }

    @Test
    public void buildInsuranceContract_OK() {
        /* ........................ Pruebas p030557 ........................ */

        InsuranceContractDAO validation = mapperHelper.buildInsuranceContract(apxRequest, requiredFieldsEmissionDao, "00110241400000001102", false);

        assertNotNull(validation.getEntityId());
        assertNotNull(validation.getBranchId());
        assertNotNull(validation.getIntAccountId());
        assertNotNull(validation.getFirstVerfnDigitId());
        assertNotNull(validation.getSecondVerfnDigitId());
        assertNotNull(validation.getPolicyQuotaInternalId());
        assertNotNull(validation.getInsuranceProductId());
        assertNotNull(validation.getInsuranceModalityType());
        assertNotNull(validation.getInsuranceCompanyId());
        assertNotNull(validation.getInsuranceManagerId());
        assertNotNull(validation.getInsurancePromoterId());
        assertNotNull(validation.getContractManagerBranchId());

        assertNotNull(validation.getInsuranceContractStartDate());
        assertNotNull(validation.getValidityMonthsNumber());

        assertNull(validation.getCustomerId());
        assertNotNull(validation.getDomicileContractId());
        assertNotNull(validation.getCardIssuingMarkType());
        assertNotNull(validation.getIssuedReceiptNumber());
        assertNotNull(validation.getPaymentFrequencyId());
        assertNotNull(validation.getPremiumAmount());
        assertNotNull(validation.getSettlePendingPremiumAmount());
        assertNotNull(validation.getCurrencyId());
        assertNotNull(validation.getInsuredAmount());
        assertNotNull(validation.getBeneficiaryType());
        assertNotNull(validation.getRenewalNumber());
        assertNotNull(validation.getCtrctDisputeStatusType());
        assertNotNull(validation.getEndorsementPolicyIndType());
        assertNotNull(validation.getInsrncCoContractStatusType());
        assertNotNull(validation.getContractStatusId());
        assertNotNull(validation.getCreationUserId());
        assertNotNull(validation.getUserAuditId());
        assertNotNull(validation.getInsurPendingDebtIndType());
        assertNotNull(validation.getTotalDebtAmount());
        assertNotNull(validation.getPrevPendBillRcptsNumber());
        assertNotNull(validation.getSettlementVarPremiumAmount());
        assertNotNull(validation.getSettlementFixPremiumAmount());
        assertNotNull(validation.getAutomaticDebitIndicatorType());
        assertNotNull(validation.getBiometryTransactionId());

        assertEquals("0011", validation.getEntityId());
        assertEquals("0241", validation.getBranchId());
        assertEquals("0000001102", validation.getIntAccountId());
        assertEquals("4", validation.getFirstVerfnDigitId());
        assertEquals("0", validation.getSecondVerfnDigitId());
        assertEquals(apxRequest.getQuotationId(), validation.getPolicyQuotaInternalId());
        assertEquals(BigDecimal.valueOf(1), validation.getInsuranceProductId());
        assertEquals(apxRequest.getProductPlan().getId(), validation.getInsuranceModalityType());
        assertEquals(new BigDecimal(apxRequest.getInsuranceCompany().getId()), validation.getInsuranceCompanyId());
        assertEquals(apxRequest.getBusinessAgent().getId(), validation.getInsuranceManagerId());
        assertEquals(apxRequest.getPromoter().getId(), validation.getInsurancePromoterId());
        assertEquals(apxRequest.getBank().getBranch().getId(), validation.getContractManagerBranchId());
        assertEquals(requiredFieldsEmissionDao.getContractDurationNumber(), validation.getValidityMonthsNumber());
        assertEquals(apxRequest.getHolder().getId(), validation.getCustomerId());
        assertEquals(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getContractId(), validation.getDomicileContractId());
        assertEquals(N_VALUE, validation.getCardIssuingMarkType());
        assertEquals(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()), validation.getIssuedReceiptNumber());

        assertEquals(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()), validation.getPremiumAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getTotalAmount().getAmount()), validation.getSettlePendingPremiumAmount());
        assertEquals(apxRequest.getInstallmentPlan().getPaymentAmount().getCurrency(), validation.getCurrencyId());
        assertEquals(BigDecimal.valueOf(apxRequest.getInsuredAmount().getAmount()), validation.getInsuredAmount());
        assertEquals("08", validation.getBeneficiaryType());
        assertEquals(BigDecimal.valueOf(0), validation.getRenewalNumber());
        assertEquals(N_VALUE, validation.getPolicyPymtPendDueDebtType());
        assertEquals("BI", validation.getCtrctDisputeStatusType());
        assertEquals(N_VALUE, validation.getEndorsementPolicyIndType());
        assertEquals("ERR", validation.getInsrncCoContractStatusType());
        assertEquals("FOR", validation.getContractStatusId());
        assertEquals(apxRequest.getCreationUser(), validation.getCreationUserId());
        assertEquals(apxRequest.getUserAudit(), validation.getUserAuditId());
        assertEquals(S_VALUE, validation.getInsurPendingDebtIndType());
        assertEquals(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()), validation.getTotalDebtAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()), validation.getPrevPendBillRcptsNumber());
        assertEquals(BigDecimal.valueOf(0), validation.getSettlementVarPremiumAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getTotalAmount().getAmount()), validation.getSettlementFixPremiumAmount());
        assertEquals(S_VALUE, validation.getAutomaticDebitIndicatorType());
        assertEquals(apxRequest.getIdentityVerificationCode(), validation.getBiometryTransactionId());

        CuotaFinancimientoBO cuota = new CuotaFinancimientoBO();
        cuota.setCuota(1L);
        cuota.setMonto(77.03);
        cuota.setFechaVencimiento(LocalDate.now());
        cuota.setMoneda("USD");

        rimacResponse.getPayload().setCuotasFinanciamiento(singletonList(cuota));

        apxRequest.getFirstInstallment().setIsPaymentRequired(true);
        apxRequest.getPaymentMethod().setPaymentType("somethingElse");

        when(requiredFieldsEmissionDao.getContractDurationType()).thenReturn("A");
        when(requiredFieldsEmissionDao.getContractDurationNumber()).thenReturn(BigDecimal.ONE);

        apxRequest.setBusinessAgent(null);
        apxRequest.setPromoter(null);

        validation = mapperHelper.buildInsuranceContract(apxRequest, requiredFieldsEmissionDao, "00110241400000001102", true);

        assertEquals(S_VALUE, validation.getEndorsementPolicyIndType());
        assertEquals(BigDecimal.valueOf(0), validation.getTotalDebtAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments() - 1), validation.getPrevPendBillRcptsNumber());
        assertEquals(BigDecimal.valueOf(12), validation.getValidityMonthsNumber());
        assertEquals(N_VALUE, validation.getInsurPendingDebtIndType());
        assertEquals(N_VALUE, validation.getAutomaticDebitIndicatorType());

        /* .................................................................................................... */
    }

    @Test
    public void createSaveContractArguments_OK() {
        when(contractDao.getEntityId()).thenReturn("entityId");
        when(contractDao.getBranchId()).thenReturn("branchId");
        when(contractDao.getIntAccountId()).thenReturn("intAccountId");
        when(contractDao.getFirstVerfnDigitId()).thenReturn("firstVerfnDigit");
        when(contractDao.getSecondVerfnDigitId()).thenReturn("secondVerfnDigit");
        when(contractDao.getPolicyQuotaInternalId()).thenReturn("policyQuotaInternal");
        when(contractDao.getInsuranceProductId()).thenReturn(BigDecimal.valueOf(1));
        when(contractDao.getInsuranceModalityType()).thenReturn("modalityType");
        when(contractDao.getInsuranceCompanyId()).thenReturn(BigDecimal.valueOf(1));
        when(contractDao.getPolicyId()).thenReturn("policyId");
        when(contractDao.getInsuranceManagerId()).thenReturn("managerId");
        when(contractDao.getInsurancePromoterId()).thenReturn("promoterId");
        when(contractDao.getContractManagerBranchId()).thenReturn("managerBranchId");
        when(contractDao.getContractInceptionDate()).thenReturn("28/06/2021");
        when(contractDao.getInsuranceContractStartDate()).thenReturn("28/06/2021");
        when(contractDao.getInsuranceContractEndDate()).thenReturn("28/06/2021");
        when(contractDao.getCustomerId()).thenReturn("customerId");
        when(contractDao.getDomicileContractId()).thenReturn("domicileContract");
        when(contractDao.getCardIssuingMarkType()).thenReturn(N_VALUE);
        when(contractDao.getIssuedReceiptNumber()).thenReturn(BigDecimal.valueOf(12));
        when(contractDao.getValidityMonthsNumber()).thenReturn(BigDecimal.valueOf(12));
        when(contractDao.getInsurancePolicyEndDate()).thenReturn("02/06/2022");
        when(contractDao.getPaymentFrequencyId()).thenReturn(BigDecimal.valueOf(1));
        when(contractDao.getPremiumAmount()).thenReturn(BigDecimal.valueOf(124.0));
        when(contractDao.getSettlePendingPremiumAmount()).thenReturn(BigDecimal.valueOf(124.0));
        when(contractDao.getCurrencyId()).thenReturn("currencyId");
        when(contractDao.getLastInstallmentDate()).thenReturn("28/06/2021");
        when(contractDao.getInstallmentPeriodFinalDate()).thenReturn("28/06/2021");
        when(contractDao.getInsuredAmount()).thenReturn(BigDecimal.valueOf(1024.0));
        when(contractDao.getBeneficiaryType()).thenReturn("08");
        when(contractDao.getRenewalNumber()).thenReturn(BigDecimal.valueOf(0));
        when(contractDao.getPolicyPymtPendDueDebtType()).thenReturn(N_VALUE);
        when(contractDao.getCtrctDisputeStatusType()).thenReturn(N_VALUE);
        when(contractDao.getPeriodNextPaymentDate()).thenReturn("17/07/2021");
        when(contractDao.getEndorsementPolicyIndType()).thenReturn(S_VALUE);
        when(contractDao.getInsrncCoContractStatusType()).thenReturn("PEN");
        when(contractDao.getContractStatusId()).thenReturn("FOR");
        when(contractDao.getCreationUserId()).thenReturn("creationUser");
        when(contractDao.getUserAuditId()).thenReturn("userAudit");
        when(contractDao.getInsurPendingDebtIndType()).thenReturn(N_VALUE);
        when(contractDao.getTotalDebtAmount()).thenReturn(BigDecimal.valueOf(124.0));
        when(contractDao.getPrevPendBillRcptsNumber()).thenReturn(BigDecimal.valueOf(11));
        when(contractDao.getSettlementVarPremiumAmount()).thenReturn(BigDecimal.valueOf(0));
        when(contractDao.getSettlementFixPremiumAmount()).thenReturn(BigDecimal.valueOf(124.0));
        when(contractDao.getInsuranceCompanyProductId()).thenReturn("830");
        when(contractDao.getAutomaticDebitIndicatorType()).thenReturn(S_VALUE);
        when(contractDao.getBiometryTransactionId()).thenReturn("transactionId");
        when(contractDao.getTelemarketingTransactionId()).thenReturn("transactionId");

        Map<String, Object> validation = mapperHelper.createSaveContractArguments(contractDao);

        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CONTRACT_FIRST_VERFN_DIGIT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CONTRACT_SECOND_VERFN_DIGIT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_POLICY_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_MANAGER_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_PROMOTER_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CONTRACT_MANAGER_BRANCH_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CONTRACT_INCEPTION_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSRNC_VALIDITY_MONTHS_NUMBER.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CUSTOMER_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_DOMICILE_CONTRACT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CARD_ISSUING_MARK_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_ISSUED_RECEIPT_NUMBER.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_PAYMENT_FREQUENCY_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_PREMIUM_AMOUNT.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_SETTLE_PENDING_PREMIUM_AMOUNT.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CURRENCY_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSTALLMENT_PERIOD_FINAL_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURED_AMOUNT.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_BENEFICIARY_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_RENEWAL_NUMBER.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CTRCT_DISPUTE_STATUS_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_ENDORSEMENT_POLICY_IND_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CONTRACT_STATUS_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSUR_PENDING_DEBT_IND_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_TOTAL_DEBT_AMOUNT.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_PREV_PEND_BILL_RCPTS_NUMBER.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_SETTLEMENT_VAR_PREMIUM_AMOUNT.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_SETTLEMENT_FIX_PREMIUM_AMOUNT.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_COMPANY_PRODUCT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_AUTOMATIC_DEBIT_INDICATOR_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_BIOMETRY_TRANSACTION_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_TELEMARKETING_TRANSACTION_ID.getValue()));

        assertEquals(contractDao.getEntityId(), validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertEquals(contractDao.getBranchId(), validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertEquals(contractDao.getIntAccountId(), validation.get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertEquals(contractDao.getFirstVerfnDigitId(), validation.get(RBVDProperties.FIELD_CONTRACT_FIRST_VERFN_DIGIT_ID.getValue()));
        assertEquals(contractDao.getSecondVerfnDigitId(), validation.get(RBVDProperties.FIELD_CONTRACT_SECOND_VERFN_DIGIT_ID.getValue()));
        assertEquals(contractDao.getPolicyQuotaInternalId(), validation.get(RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue()));
        assertEquals(contractDao.getInsuranceProductId(), validation.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()));
        assertEquals(contractDao.getInsuranceModalityType(), validation.get(RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue()));
        assertEquals(contractDao.getInsuranceCompanyId(), validation.get(RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue()));
        assertEquals(contractDao.getPolicyId(), validation.get(RBVDProperties.FIELD_POLICY_ID.getValue()));
        assertEquals(contractDao.getInsuranceManagerId(), validation.get(RBVDProperties.FIELD_INSURANCE_MANAGER_ID.getValue()));
        assertEquals(contractDao.getInsurancePromoterId(), validation.get(RBVDProperties.FIELD_INSURANCE_PROMOTER_ID.getValue()));
        assertEquals(contractDao.getContractManagerBranchId(), validation.get(RBVDProperties.FIELD_CONTRACT_MANAGER_BRANCH_ID.getValue()));
        assertEquals(contractDao.getContractInceptionDate(), validation.get(RBVDProperties.FIELD_CONTRACT_INCEPTION_DATE.getValue()));
        assertEquals(contractDao.getInsuranceContractStartDate(), validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue()));
        assertEquals(contractDao.getInsuranceContractEndDate(), validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue()));
        assertEquals(contractDao.getValidityMonthsNumber(), validation.get(RBVDProperties.FIELD_INSRNC_VALIDITY_MONTHS_NUMBER.getValue()));
        assertEquals(contractDao.getCustomerId(), validation.get(RBVDProperties.FIELD_CUSTOMER_ID.getValue()));
        assertEquals(contractDao.getDomicileContractId(), validation.get(RBVDProperties.FIELD_DOMICILE_CONTRACT_ID.getValue()));
        assertEquals(contractDao.getCardIssuingMarkType(), validation.get(RBVDProperties.FIELD_CARD_ISSUING_MARK_TYPE.getValue()));
        assertEquals(contractDao.getIssuedReceiptNumber(), validation.get(RBVDProperties.FIELD_ISSUED_RECEIPT_NUMBER.getValue()));
        assertEquals(contractDao.getPaymentFrequencyId(), validation.get(RBVDProperties.FIELD_PAYMENT_FREQUENCY_ID.getValue()));
        assertEquals(contractDao.getPremiumAmount(), validation.get(RBVDProperties.FIELD_PREMIUM_AMOUNT.getValue()));
        assertEquals(contractDao.getSettlePendingPremiumAmount(), validation.get(RBVDProperties.FIELD_SETTLE_PENDING_PREMIUM_AMOUNT.getValue()));
        assertEquals(contractDao.getCurrencyId(), validation.get(RBVDProperties.FIELD_CURRENCY_ID.getValue()));
        assertEquals(contractDao.getLastInstallmentDate(), validation.get(RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue()));
        assertEquals(contractDao.getInstallmentPeriodFinalDate(), validation.get(RBVDProperties.FIELD_INSTALLMENT_PERIOD_FINAL_DATE.getValue()));
        assertEquals(contractDao.getInsuredAmount(), validation.get(RBVDProperties.FIELD_INSURED_AMOUNT.getValue()));
        assertEquals(contractDao.getBeneficiaryType(), validation.get(RBVDProperties.FIELD_BENEFICIARY_TYPE.getValue()));
        assertEquals(contractDao.getRenewalNumber(), validation.get(RBVDProperties.FIELD_RENEWAL_NUMBER.getValue()));
        assertEquals(contractDao.getCtrctDisputeStatusType(), validation.get(RBVDProperties.FIELD_CTRCT_DISPUTE_STATUS_TYPE.getValue()));
        assertEquals(contractDao.getPeriodNextPaymentDate(), validation.get(RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue()));
        assertEquals(contractDao.getEndorsementPolicyIndType(), validation.get(RBVDProperties.FIELD_ENDORSEMENT_POLICY_IND_TYPE.getValue()));
        assertEquals(contractDao.getInsrncCoContractStatusType(), validation.get(RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue()));
        assertEquals(contractDao.getContractStatusId(), validation.get(RBVDProperties.FIELD_CONTRACT_STATUS_ID.getValue()));
        assertEquals(contractDao.getCreationUserId(), validation.get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertEquals(contractDao.getUserAuditId(), validation.get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
        assertEquals(contractDao.getInsurPendingDebtIndType(), validation.get(RBVDProperties.FIELD_INSUR_PENDING_DEBT_IND_TYPE.getValue()));
        assertEquals(contractDao.getTotalDebtAmount(), validation.get(RBVDProperties.FIELD_TOTAL_DEBT_AMOUNT.getValue()));
        assertEquals(contractDao.getPrevPendBillRcptsNumber(), validation.get(RBVDProperties.FIELD_PREV_PEND_BILL_RCPTS_NUMBER.getValue()));
        assertEquals(contractDao.getSettlementVarPremiumAmount(), validation.get(RBVDProperties.FIELD_SETTLEMENT_VAR_PREMIUM_AMOUNT.getValue()));
        assertEquals(contractDao.getSettlementFixPremiumAmount(), validation.get(RBVDProperties.FIELD_SETTLEMENT_FIX_PREMIUM_AMOUNT.getValue()));
        assertEquals(contractDao.getInsuranceCompanyProductId(), validation.get(RBVDProperties.FIELD_INSURANCE_COMPANY_PRODUCT_ID.getValue()));
        assertEquals(contractDao.getAutomaticDebitIndicatorType(), validation.get(RBVDProperties.FIELD_AUTOMATIC_DEBIT_INDICATOR_TYPE.getValue()));
        assertEquals(contractDao.getBiometryTransactionId(), validation.get(RBVDProperties.FIELD_BIOMETRY_TRANSACTION_ID.getValue()));
        assertEquals(contractDao.getTelemarketingTransactionId(), validation.get(RBVDProperties.FIELD_TELEMARKETING_TRANSACTION_ID.getValue()));
    }

    @Test
    public void getFirstReceiptInformation_OK() {
        InsuranceCtrReceiptsDAO validation = mapperHelper.getFirstReceiptInformation(asoResponse, apxRequest);

        assertNotNull(validation.getEntityId());
        assertNotNull(validation.getBranchId());
        assertNotNull(validation.getIntAccountId());
        assertNotNull(validation.getPremiumPaymentReceiptAmount());
        assertNotNull(validation.getFixingExchangeRateAmount());
        assertNotNull(validation.getPremiumCurrencyExchAmount());
        assertNotNull(validation.getPremiumChargeOperationId());
        assertNotNull(validation.getCurrencyId());
        assertNotNull(validation.getReceiptIssueDate());
        assertNotNull(validation.getReceiptCollectionDate());
        assertNotNull(validation.getReceiptsTransmissionDate());
        assertNotNull(validation.getReceiptExpirationDate());
        assertNotNull(validation.getInsuranceCollectionMoveId());
        assertNotNull(validation.getPaymentMethodType());
        assertNotNull(validation.getDebitAccountId());
        assertNotNull(validation.getDebitChannelType());
        assertNotNull(validation.getCreationUserId());
        assertNotNull(validation.getUserAuditId());
        assertNotNull(validation.getManagementBranchId());
        assertNotNull(validation.getFixPremiumAmount());
        assertNotNull(validation.getSettlementFixPremiumAmount());
        assertNotNull(validation.getLastChangeBranchId());
        assertNotNull(validation.getInsuranceCompanyId());
        assertNotNull(validation.getGlBranchId());

        assertEquals(asoResponse.getData().getId().substring(0, 4), validation.getEntityId());
        assertEquals(asoResponse.getData().getId().substring(4, 8), validation.getBranchId());
        assertEquals(asoResponse.getData().getId().substring(10), validation.getIntAccountId());
        assertEquals(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()),
                validation.getPremiumPaymentReceiptAmount());
        assertEquals(BigDecimal.valueOf(asoResponse.getData()
                .getFirstInstallment().getExchangeRate().getDetail().getFactor().getRatio()), validation.getFixingExchangeRateAmount());
        assertEquals(BigDecimal.valueOf(asoResponse.getData()
                .getFirstInstallment().getExchangeRate().getDetail().getFactor().getValue()), validation.getPremiumCurrencyExchAmount());
        assertEquals(asoResponse.getData().getFirstInstallment().getOperationNumber().substring(1),
                validation.getPremiumChargeOperationId());
        assertEquals(apxRequest.getFirstInstallment().getPaymentAmount().getCurrency(), validation.getCurrencyId());
        assertEquals("01/01/0001", validation.getReceiptIssueDate());
        assertEquals("01/01/0001", validation.getReceiptCollectionDate());
        assertEquals("01/01/0001", validation.getReceiptsTransmissionDate());
        assertEquals("15/06/2021", validation.getReceiptExpirationDate());
        assertEquals(asoResponse.getData().getFirstInstallment().getTransactionNumber(),
                validation.getInsuranceCollectionMoveId());
        assertEquals("T", validation.getPaymentMethodType());
        assertEquals(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getContractId(),
                validation.getDebitAccountId());
        assertEquals(apxRequest.getSaleChannelId(), validation.getDebitChannelType());
        assertEquals(apxRequest.getCreationUser(), validation.getCreationUserId());
        assertEquals(apxRequest.getUserAudit(), validation.getUserAuditId());
        assertEquals(apxRequest.getBank().getBranch().getId(), validation.getManagementBranchId());
        assertEquals(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()),
                validation.getFixPremiumAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getTotalAmount().getAmount()), validation.getSettlementFixPremiumAmount());
        assertEquals(apxRequest.getBank().getBranch().getId(), validation.getLastChangeBranchId());
        assertEquals(new BigDecimal(apxRequest.getInsuranceCompany().getId()), validation.getInsuranceCompanyId());
        assertEquals(asoResponse.getData().getId().substring(4, 8), validation.getGlBranchId());

        /* Otherwise cases */

        apxRequest.getFirstInstallment().setIsPaymentRequired(true);
        apxRequest.getPaymentMethod().getRelatedContracts().get(0).getProduct().setId("ACCOUNT");

        validation = mapperHelper.getFirstReceiptInformation(asoResponse, apxRequest);

        assertEquals("12/07/2021", validation.getReceiptIssueDate());
        assertEquals("12/07/2021", validation.getReceiptCollectionDate());
        assertEquals("12/07/2021", validation.getReceiptsTransmissionDate());
        assertEquals("C", validation.getPaymentMethodType());
    }

    @Test
    public void buildIsrcContractMov_OK() {
        IsrcContractMovDAO isrcContractMovDao = mapperHelper.buildIsrcContractMov(asoResponse, "creationUser", "userAudit");

        assertNotNull(isrcContractMovDao.getEntityId());
        assertNotNull(isrcContractMovDao.getBranchId());
        assertNotNull(isrcContractMovDao.getIntAccountId());
        assertNotNull(isrcContractMovDao.getPolicyMovementNumber());
        assertNotNull(isrcContractMovDao.getGlAccountDate());
        assertNotNull(isrcContractMovDao.getGlBranchId());
        assertNotNull(isrcContractMovDao.getMovementType());
        assertNotNull(isrcContractMovDao.getAdditionalDataDesc());
        assertNotNull(isrcContractMovDao.getContractStatusId());
        assertNotNull(isrcContractMovDao.getMovementStatusType());
        assertNotNull(isrcContractMovDao.getCreationUserId());
        assertNotNull(isrcContractMovDao.getUserAuditId());

        assertEquals("0011", isrcContractMovDao.getEntityId());
        assertEquals("0241", isrcContractMovDao.getBranchId());
        assertEquals("0000001102", isrcContractMovDao.getIntAccountId());
        assertEquals(BigDecimal.valueOf(1), isrcContractMovDao.getPolicyMovementNumber());
        assertEquals("0241", isrcContractMovDao.getGlBranchId());
        assertEquals("01", isrcContractMovDao.getMovementType());
        assertEquals("ALTA DE SEGURO", isrcContractMovDao.getAdditionalDataDesc());
        assertEquals("FOR", isrcContractMovDao.getContractStatusId());
        assertEquals("01", isrcContractMovDao.getMovementStatusType());
        assertEquals("creationUser", isrcContractMovDao.getCreationUserId());
        assertEquals("userAudit", isrcContractMovDao.getUserAuditId());
    }

    @Test
    public void createSaveContractMovArguments_OK() {
        when(contractMovDao.getEntityId()).thenReturn("entityId");
        when(contractMovDao.getBranchId()).thenReturn("branchId");
        when(contractMovDao.getIntAccountId()).thenReturn("intAccountId");
        when(contractMovDao.getPolicyMovementNumber()).thenReturn(BigDecimal.valueOf(1));
        when(contractMovDao.getGlAccountDate()).thenReturn("01/07/2021");
        when(contractMovDao.getGlBranchId()).thenReturn("branchId");
        when(contractMovDao.getMovementType()).thenReturn("01");
        when(contractMovDao.getAdditionalDataDesc()).thenReturn("ALTA DE SEGURO");
        when(contractMovDao.getContractStatusId()).thenReturn("FOR");
        when(contractMovDao.getMovementStatusType()).thenReturn("01");
        when(contractMovDao.getCreationUserId()).thenReturn("creationUser");
        when(contractMovDao.getUserAuditId()).thenReturn("userAudit");

        Map<String, Object> validation = mapperHelper.createSaveContractMovArguments(contractMovDao);

        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_POLICY_MOVEMENT_NUMBER.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_GL_ACCOUNT_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_GL_BRANCH_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_MOVEMENT_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_ADDITIONAL_DATA_DESC.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CONTRACT_STATUS_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_MOVEMENT_STATUS_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));

        assertEquals(contractMovDao.getEntityId(), validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertEquals(contractMovDao.getBranchId(), validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertEquals(contractMovDao.getIntAccountId(), validation.get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertEquals(contractMovDao.getPolicyMovementNumber(), validation.get(RBVDProperties.FIELD_POLICY_MOVEMENT_NUMBER.getValue()));
        assertEquals(contractMovDao.getGlAccountDate(), validation.get(RBVDProperties.FIELD_GL_ACCOUNT_DATE.getValue()));
        assertEquals(contractMovDao.getGlBranchId(), validation.get(RBVDProperties.FIELD_GL_BRANCH_ID.getValue()));
        assertEquals(contractMovDao.getMovementType(), validation.get(RBVDProperties.FIELD_MOVEMENT_TYPE.getValue()));
        assertEquals(contractMovDao.getAdditionalDataDesc(), validation.get(RBVDProperties.FIELD_ADDITIONAL_DATA_DESC.getValue()));
        assertEquals(contractMovDao.getContractStatusId(), validation.get(RBVDProperties.FIELD_CONTRACT_STATUS_ID.getValue()));
        assertEquals(contractMovDao.getMovementStatusType(), validation.get(RBVDProperties.FIELD_MOVEMENT_STATUS_TYPE.getValue()));
        assertEquals(contractMovDao.getCreationUserId(), validation.get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertEquals(contractMovDao.getUserAuditId(), validation.get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
    }

    @Test
    public void buildIsrcContractParticipants_OK() {
        List<Map<String, Object>> responseQueryRoles = new ArrayList<>();
        Map<String, Object> firstRole = new HashMap<>();
        firstRole.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(), BigDecimal.valueOf(1));
        responseQueryRoles.add(firstRole);
        Map<String, Object> secondRole = new HashMap<>();
        secondRole.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(), BigDecimal.valueOf(2));
        responseQueryRoles.add(secondRole);
        Map<String, Object> response = new HashMap<>();
        response.put(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), responseQueryRoles);

        when(applicationConfigurationService.getProperty("DNI")).thenReturn("L");

        List<IsrcContractParticipantDAO> validation = mapperHelper.buildIsrcContractParticipants(apxRequest, response, "00163789533573412294");

        ParticipantDTO participant = apxRequest.getParticipants().get(0);

        assertNotNull(validation.get(0).getEntityId());
        assertNotNull(validation.get(0).getEntityId());
        assertNotNull(validation.get(0).getIntAccountId());
        assertNotNull(validation.get(0).getParticipantRoleId());
        assertNotNull(validation.get(0).getPartyOrderNumber());
        assertNotNull(validation.get(0).getPersonalDocType());
        assertNotNull(validation.get(0).getParticipantPersonalId());
        assertNotNull(validation.get(0).getCustomerId());
        assertNotNull(validation.get(0).getCustomerRelationshipType());
        assertNotNull(validation.get(0).getRegistrySituationType());
        assertNotNull(validation.get(0).getCreationUserId());
        assertNotNull(validation.get(0).getUserAuditId());

        assertNotNull(validation.get(1).getEntityId());
        assertNotNull(validation.get(1).getEntityId());
        assertNotNull(validation.get(1).getIntAccountId());
        assertNotNull(validation.get(1).getParticipantRoleId());
        assertNotNull(validation.get(1).getPartyOrderNumber());
        assertNotNull(validation.get(1).getPersonalDocType());
        assertNotNull(validation.get(1).getParticipantPersonalId());
        assertNotNull(validation.get(1).getCustomerId());
        assertNotNull(validation.get(1).getCustomerRelationshipType());
        assertNotNull(validation.get(1).getRegistrySituationType());
        assertNotNull(validation.get(1).getCreationUserId());
        assertNotNull(validation.get(1).getUserAuditId());

        assertEquals("0016", validation.get(0).getEntityId());
        assertEquals("3789", validation.get(0).getBranchId());
        assertEquals("3573412294", validation.get(0).getIntAccountId());
        assertEquals(BigDecimal.valueOf(1), validation.get(0).getParticipantRoleId());
        assertEquals(BigDecimal.valueOf(1), validation.get(0).getPartyOrderNumber());
        assertEquals("L", validation.get(0).getPersonalDocType());
        assertEquals(participant.getIdentityDocument().getNumber(), validation.get(0).getParticipantPersonalId());
        assertEquals(participant.getCustomerId(), validation.get(0).getCustomerId());
        assertEquals("TI", validation.get(0).getCustomerRelationshipType());
        assertEquals("01", validation.get(0).getRegistrySituationType());
        assertEquals(apxRequest.getCreationUser(), validation.get(0).getCreationUserId());
        assertEquals(apxRequest.getUserAudit(), validation.get(0).getUserAuditId());

        assertEquals("0016", validation.get(1).getEntityId());
        assertEquals("3789", validation.get(1).getBranchId());
        assertEquals("3573412294", validation.get(1).getIntAccountId());
        assertEquals(BigDecimal.valueOf(2), validation.get(1).getParticipantRoleId());
        assertEquals(BigDecimal.valueOf(1), validation.get(1).getPartyOrderNumber());
        assertEquals("L", validation.get(1).getPersonalDocType());
        assertEquals(participant.getIdentityDocument().getNumber(), validation.get(1).getParticipantPersonalId());
        assertEquals(participant.getCustomerId(), validation.get(1).getCustomerId());
        assertEquals("TI", validation.get(1).getCustomerRelationshipType());
        assertEquals("01", validation.get(1).getRegistrySituationType());
        assertEquals(apxRequest.getCreationUser(), validation.get(1).getCreationUserId());
        assertEquals(apxRequest.getUserAudit(), validation.get(1).getUserAuditId());
    }

    @Test
    public void createSaveParticipantArguments_OK() {
        when(participantDao.getEntityId()).thenReturn("entityId");
        when(participantDao.getBranchId()).thenReturn("branchId");
        when(participantDao.getIntAccountId()).thenReturn("intAccountId");
        when(participantDao.getParticipantRoleId()).thenReturn(BigDecimal.valueOf(1));
        when(participantDao.getPartyOrderNumber()).thenReturn(BigDecimal.valueOf(1));
        when(participantDao.getPersonalDocType()).thenReturn("DNI");
        when(participantDao.getParticipantPersonalId()).thenReturn("04040075");
        when(participantDao.getCustomerId()).thenReturn("");
        when(participantDao.getCustomerRelationshipType()).thenReturn("TI");
        when(participantDao.getRegistrySituationType()).thenReturn("01");
        when(participantDao.getCreationUserId()).thenReturn("creationUser");
        when(participantDao.getUserAuditId()).thenReturn("userAudit");

        Map<String, Object>[] validation = mapperHelper.createSaveParticipantArguments(Collections.singletonList(participantDao));

        assertNotNull(validation[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_PARTY_ORDER_NUMBER.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_PERSONAL_DOC_TYPE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_CUSTOMER_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_CUSTOMER_RELATIONSHIP_TYPE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_REGISTRY_SITUATION_TYPE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));

        assertEquals(participantDao.getEntityId(), validation[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertEquals(participantDao.getBranchId(), validation[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertEquals(participantDao.getIntAccountId(), validation[0].get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertEquals(participantDao.getParticipantRoleId(), validation[0].get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue()));
        assertEquals(participantDao.getPartyOrderNumber(), validation[0].get(RBVDProperties.FIELD_PARTY_ORDER_NUMBER.getValue()));
        assertEquals(participantDao.getPersonalDocType(), validation[0].get(RBVDProperties.FIELD_PERSONAL_DOC_TYPE.getValue()));
        assertEquals(participantDao.getParticipantPersonalId(), validation[0].get(RBVDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue()));
        assertEquals(participantDao.getCustomerId(), validation[0].get(RBVDProperties.FIELD_CUSTOMER_ID.getValue()));
        assertEquals(participantDao.getCustomerRelationshipType(), validation[0].get(RBVDProperties.FIELD_CUSTOMER_RELATIONSHIP_TYPE.getValue()));
        assertEquals(participantDao.getRegistrySituationType(), validation[0].get(RBVDProperties.FIELD_REGISTRY_SITUATION_TYPE.getValue()));
        assertEquals(participantDao.getCreationUserId(), validation[0].get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertEquals(participantDao.getUserAuditId(), validation[0].get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
    }

    @Test
    public void createSaveEndorsementArguments_OK() {
        when(contractDao.getEntityId()).thenReturn("entityId");
        when(contractDao.getBranchId()).thenReturn("branchId");
        when(contractDao.getIntAccountId()).thenReturn("intAccountId");
        when(contractDao.getFirstVerfnDigitId()).thenReturn("firstVerfnDigit");
        when(contractDao.getSecondVerfnDigitId()).thenReturn("secondVerfnDigit");
        when(contractDao.getPolicyQuotaInternalId()).thenReturn("policyQuotaInternal");
        when(contractDao.getInsuranceProductId()).thenReturn(BigDecimal.valueOf(1));
        when(contractDao.getInsuranceModalityType()).thenReturn("modalityType");
        when(contractDao.getInsuranceCompanyId()).thenReturn(BigDecimal.valueOf(1));
        when(contractDao.getPolicyId()).thenReturn("policyId");
        when(contractDao.getInsuranceManagerId()).thenReturn("managerId");
        when(contractDao.getInsurancePromoterId()).thenReturn("promoterId");
        when(contractDao.getContractManagerBranchId()).thenReturn("managerBranchId");
        when(contractDao.getContractInceptionDate()).thenReturn("28/06/2021");
        when(contractDao.getInsuranceContractStartDate()).thenReturn("28/06/2021");
        when(contractDao.getInsuranceContractEndDate()).thenReturn("28/06/2021");
        when(contractDao.getCustomerId()).thenReturn("customerId");
        when(contractDao.getDomicileContractId()).thenReturn("domicileContract");
        when(contractDao.getCardIssuingMarkType()).thenReturn(N_VALUE);
        when(contractDao.getIssuedReceiptNumber()).thenReturn(BigDecimal.valueOf(12));
        when(contractDao.getValidityMonthsNumber()).thenReturn(BigDecimal.valueOf(12));
        when(contractDao.getInsurancePolicyEndDate()).thenReturn("02/06/2022");
        when(contractDao.getPaymentFrequencyId()).thenReturn(BigDecimal.valueOf(1));
        when(contractDao.getPremiumAmount()).thenReturn(BigDecimal.valueOf(124.0));
        when(contractDao.getSettlePendingPremiumAmount()).thenReturn(BigDecimal.valueOf(124.0));
        when(contractDao.getCurrencyId()).thenReturn("currencyId");
        when(contractDao.getLastInstallmentDate()).thenReturn("28/06/2021");
        when(contractDao.getInstallmentPeriodFinalDate()).thenReturn("28/06/2021");
        when(contractDao.getInsuredAmount()).thenReturn(BigDecimal.valueOf(1024.0));
        when(contractDao.getBeneficiaryType()).thenReturn("08");
        when(contractDao.getRenewalNumber()).thenReturn(BigDecimal.valueOf(0));
        when(contractDao.getPolicyPymtPendDueDebtType()).thenReturn(N_VALUE);
        when(contractDao.getCtrctDisputeStatusType()).thenReturn(N_VALUE);
        when(contractDao.getPeriodNextPaymentDate()).thenReturn("17/07/2021");
        when(contractDao.getEndorsementPolicyIndType()).thenReturn(S_VALUE);
        when(contractDao.getInsrncCoContractStatusType()).thenReturn("PEN");
        when(contractDao.getContractStatusId()).thenReturn("FOR");
        when(contractDao.getCreationUserId()).thenReturn("creationUser");
        when(contractDao.getUserAuditId()).thenReturn("userAudit");
        when(contractDao.getInsurPendingDebtIndType()).thenReturn(N_VALUE);
        when(contractDao.getTotalDebtAmount()).thenReturn(BigDecimal.valueOf(124.0));
        when(contractDao.getPrevPendBillRcptsNumber()).thenReturn(BigDecimal.valueOf(11));
        when(contractDao.getSettlementVarPremiumAmount()).thenReturn(BigDecimal.valueOf(0));
        when(contractDao.getSettlementFixPremiumAmount()).thenReturn(BigDecimal.valueOf(124.0));
        when(contractDao.getInsuranceCompanyProductId()).thenReturn("830");
        when(contractDao.getAutomaticDebitIndicatorType()).thenReturn(S_VALUE);
        when(contractDao.getBiometryTransactionId()).thenReturn("transactionId");
        when(contractDao.getTelemarketingTransactionId()).thenReturn("transactionId");

        Map<String, Object> validation = mapperHelper.createSaveEndorsementArguments(contractDao, "1245", 100.00);

        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_DOCUMENT_TYPE_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_DOCUMENT_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_ENDORSEMENT_SEQUENCE_NUMBER.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_ENDORSEMENT_EFF_START_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_ENDORSEMENT_EFF_END_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_POLICY_ENDORSEMENT_PER.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_REGISTRY_SITUATION_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));

        assertEquals(contractDao.getEntityId(), validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertEquals(contractDao.getBranchId(), validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertEquals(contractDao.getIntAccountId(), validation.get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertEquals("R", validation.get(RBVDProperties.FIELD_DOCUMENT_TYPE_ID.getValue()));
        assertEquals("1245", validation.get(RBVDProperties.FIELD_DOCUMENT_ID.getValue()));
        assertEquals(1, validation.get(RBVDProperties.FIELD_ENDORSEMENT_SEQUENCE_NUMBER.getValue()));
        assertEquals(contractDao.getPolicyId(), validation.get(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue()));
        assertEquals(contractDao.getInsuranceContractStartDate(), validation.get(RBVDProperties.FIELD_ENDORSEMENT_EFF_START_DATE.getValue()));
        assertEquals(contractDao.getInsuranceContractEndDate(), validation.get(RBVDProperties.FIELD_ENDORSEMENT_EFF_END_DATE.getValue()));
        assertEquals(100.00, validation.get(RBVDProperties.FIELD_POLICY_ENDORSEMENT_PER.getValue()));
        assertEquals("01", validation.get(RBVDProperties.FIELD_REGISTRY_SITUATION_TYPE.getValue()));
        assertEquals("SYSTEM", validation.get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertEquals("SYSTEM", validation.get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
    }

    @Test
    public void mapRimacEmisionRequest_OK() {
        when(this.applicationConfigurationService.getProperty("RUC")).thenReturn("R");
        when(this.applicationConfigurationService.getProperty("DNI")).thenReturn("L");
        when(this.applicationConfigurationService.getProperty("MONTHLY")).thenReturn("M");
        Map<String,Object> requiredFieldsEmisionBDResponse = new HashMap<>();
        requiredFieldsEmisionBDResponse.put(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue(), "jose.sandoval.tirado.contractor@bbva.com");
        requiredFieldsEmisionBDResponse.put(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue(), "993766790");
        requiredFieldsEmisionBDResponse.put(PISDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue(), "33556255");
        requiredFieldsEmisionBDResponse.put(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue(), "HOGAR_TOTAL");
        EmisionBO emisionInput = new EmisionBO();
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
        customerList.getData().get(0).setIdentityDocuments(identityDocumentsBOs1);

        EmisionBO validation1 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse, customerList);

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

        GeographicGroupsBO geographicGroupsBO = new GeographicGroupsBO();
        geographicGroupsBO.setName("NO APLICA");
        GeographicGroupTypeBO geographicGroupTypeBO = new GeographicGroupTypeBO();
        geographicGroupTypeBO.setId("UNCATEGORIZED");
        geographicGroupTypeBO.setName("NO APLICA");
        geographicGroupsBO.setGeographicGroupType(geographicGroupTypeBO);
        GeographicGroupsBO geographicGroupsBO1 = new GeographicGroupsBO();
        geographicGroupsBO1.setName("233");
        GeographicGroupTypeBO geographicGroupTypeBO1 = new GeographicGroupTypeBO();
        geographicGroupTypeBO1.setId("EXTERIOR_NUMBER");
        geographicGroupTypeBO1.setName("EXTERIOR_NUMBER");
        geographicGroupsBO1.setGeographicGroupType(geographicGroupTypeBO1);
        List<GeographicGroupsBO> geographicGroupsBOs = new ArrayList<>();
        geographicGroupsBOs.add(geographicGroupsBO);
        geographicGroupsBOs.add(geographicGroupsBO1);
        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs);
        when(this.applicationConfigurationService.getProperty("RUC")).thenReturn("RC");
        EmisionBO validation2 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse, customerList);
        assertNotNull(validation2);

        customerList.getData().get(0).setGender(null);

        GeographicGroupsBO geographicGroupsBO2 = new GeographicGroupsBO();
        geographicGroupsBO2.setName("MZ E");
        GeographicGroupTypeBO geographicGroupTypeBO2 = new GeographicGroupTypeBO();
        geographicGroupTypeBO2.setId("BLOCK");
        geographicGroupTypeBO2.setName("BLOCK");
        geographicGroupsBO2.setGeographicGroupType(geographicGroupTypeBO2);
        GeographicGroupsBO geographicGroupsBO3 = new GeographicGroupsBO();
        geographicGroupsBO2.setName("LT 8");
        GeographicGroupTypeBO geographicGroupTypeBO3 = new GeographicGroupTypeBO();
        geographicGroupTypeBO3.setId("LOT");
        geographicGroupTypeBO3.setName("LOT");
        geographicGroupsBO3.setGeographicGroupType(geographicGroupTypeBO3);
        List<GeographicGroupsBO> geographicGroupsBOs2 = new ArrayList<>();
        geographicGroupsBOs2.add(geographicGroupsBO2);
        geographicGroupsBOs2.add(geographicGroupsBO3);
        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs2);
        EmisionBO validation3 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse, customerList);
        assertNotNull(validation3);

        GeographicGroupsBO geographicGroupsBO4 = new GeographicGroupsBO();
        geographicGroupsBO2.setName("xxxxx");
        GeographicGroupTypeBO geographicGroupTypeBO4 = new GeographicGroupTypeBO();
        geographicGroupTypeBO4.setId("xxxxx");
        geographicGroupTypeBO4.setName("xxxx");
        geographicGroupsBO4.setGeographicGroupType(geographicGroupTypeBO4);
        List<GeographicGroupsBO> geographicGroupsBOs4 = new ArrayList<>();
        geographicGroupsBOs4.add(geographicGroupsBO4);
        customerList.getData().get(0).getAddresses().get(0).getLocation().setGeographicGroups(geographicGroupsBOs4);
        EmisionBO validation4 = mapperHelper.mapRimacEmisionRequest(emisionInput, apxRequest, requiredFieldsEmisionBDResponse, customerList);
        assertNotNull(validation4);
    }

    @Test
    public void getRimacContractInformation_OK() {
        String contractNumber = "00117799454000009162";

        Map<String, Object> validation = mapperHelper.getRimacContractInformation(rimacResponse, contractNumber);

        assertNotNull(validation.get(RBVDProperties.FIELD_POLICY_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_COMPANY_PRODUCT_ID.getValue()));

        rimacResponse.getPayload().getCuotasFinanciamiento().clear();

        CuotaFinancimientoBO cuotaAnual = new CuotaFinancimientoBO();
        cuotaAnual.setCuota(1L);
        cuotaAnual.setFechaVencimiento(new LocalDate(new Date(), DateTimeZone.UTC));

        rimacResponse.getPayload().setCuotasFinanciamiento(of(cuotaAnual).collect(toList()));

        validation = mapperHelper.getRimacContractInformation(rimacResponse, contractNumber);

        assertEquals("02/06/2022", validation.get(RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue()));

        rimacResponse.getPayload().getCuotasFinanciamiento().clear();

        validation = mapperHelper.getRimacContractInformation(rimacResponse, contractNumber);

        assertNull(validation.get(RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue()));
    }

    @Test
    public void buildNextInsuranceCtrReceipt_OK() {
        List<InsuranceCtrReceiptsDAO> validation = mapperHelper.buildNextInsuranceCtrReceipt(receiptDao, rimacResponse);

        String defaultDate = "01/01/0001";

        validation.forEach(receipt -> {
            assertNotNull(receipt.getEntityId());
            assertNotNull(receipt.getBranchId());
            assertNotNull(receipt.getIntAccountId());
            assertNotNull(receipt.getPolicyReceiptId());
            assertNotNull(receipt.getInsuranceCompanyId());
            assertNotNull(receipt.getPremiumPaymentReceiptAmount());
            assertNull(receipt.getPremiumChargeOperationId());
            assertNotNull(receipt.getCurrencyId());
            assertNotNull(receipt.getReceiptIssueDate());
            assertNotNull(receipt.getReceiptStartDate());
            assertNotNull(receipt.getReceiptEndDate());
            assertNotNull(receipt.getReceiptCollectionDate());
            assertNotNull(receipt.getReceiptExpirationDate());
            assertNotNull(receipt.getReceiptsTransmissionDate());
            assertNotNull(receipt.getReceiptCollectionStatusType());
            assertNull(receipt.getInsuranceCollectionMoveId());
            assertNotNull(receipt.getPaymentMethodType());
            assertNotNull(receipt.getDebitAccountId());
            assertNull(receipt.getDebitChannelType());
            assertNotNull(receipt.getChargeAttemptsNumber());
            assertNotNull(receipt.getInsrncCoReceiptStatusType());
            assertNotNull(receipt.getReceiptStatusType());
            assertNotNull(receipt.getCreationUserId());
            assertNotNull(receipt.getUserAuditId());
            assertNotNull(receipt.getManagementBranchId());
            assertNotNull(receipt.getVariablePremiumAmount());
            assertNotNull(receipt.getFixPremiumAmount());
            assertNotNull(receipt.getSettlementVarPremiumAmount());
            assertNotNull(receipt.getSettlementFixPremiumAmount());
            assertNotNull(receipt.getLastChangeBranchId());
            assertNotNull(receipt.getGlBranchId());

            assertEquals(receiptDao.getEntityId(), receipt.getEntityId());
            assertEquals(receiptDao.getBranchId(), receipt.getBranchId());
            assertEquals(receiptDao.getIntAccountId(), receipt.getIntAccountId());
            assertEquals(BigDecimal.valueOf(1), receipt.getInsuranceCompanyId());
            assertEquals(BigDecimal.valueOf(0), receipt.getPremiumPaymentReceiptAmount());
            assertEquals(BigDecimal.valueOf(0), receipt.getFixingExchangeRateAmount());
            assertEquals(BigDecimal.valueOf(0), receipt.getPremiumCurrencyExchAmount());
            assertEquals(receiptDao.getCurrencyId(), receipt.getCurrencyId());
            assertEquals(defaultDate, receipt.getReceiptIssueDate());
            assertEquals(defaultDate, receipt.getReceiptStartDate());
            assertEquals(defaultDate, receipt.getReceiptEndDate());
            assertEquals(defaultDate, receipt.getReceiptCollectionDate());
            assertEquals(defaultDate, receipt.getReceiptsTransmissionDate());
            assertEquals("02", receipt.getReceiptCollectionStatusType());
            assertEquals(receiptDao.getPaymentMethodType(), receipt.getPaymentMethodType());
            assertEquals(receiptDao.getDebitAccountId(), receipt.getDebitAccountId());
            assertEquals(BigDecimal.valueOf(0), receipt.getChargeAttemptsNumber());
            assertEquals(receiptDao.getInsrncCoReceiptStatusType(), receipt.getInsrncCoReceiptStatusType());
            assertEquals("INC", receipt.getReceiptStatusType());
            assertEquals(receiptDao.getCreationUserId(), receipt.getCreationUserId());
            assertEquals(receiptDao.getUserAuditId(), receipt.getUserAuditId());
            assertEquals(receiptDao.getManagementBranchId(), receipt.getManagementBranchId());
            assertEquals(BigDecimal.valueOf(0), receipt.getVariablePremiumAmount());
            assertEquals(receiptDao.getFixPremiumAmount(), receipt.getFixPremiumAmount());
            assertEquals(BigDecimal.valueOf(0), receipt.getSettlementVarPremiumAmount());
            assertEquals(BigDecimal.valueOf(0), receipt.getSettlementFixPremiumAmount());
            assertEquals(receiptDao.getLastChangeBranchId(), receipt.getLastChangeBranchId());
            assertEquals(receiptDao.getGlBranchId(), receipt.getGlBranchId());
        });

        rimacResponse.getPayload().getCuotasFinanciamiento().clear();

        validation = mapperHelper.buildNextInsuranceCtrReceipt(receiptDao, rimacResponse);

        assertTrue(validation.isEmpty());
    }

    @Test
    public void createSaveReceiptsArguments_OK() {
        Map<String, Object>[] validation = mapperHelper.createSaveReceiptsArguments(Collections.singletonList(receiptDao));

        assertNotNull(validation[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_POLICY_RECEIPT_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_PREMIUM_PAYMENT_RECEIPT_AMOUNT.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_FIXING_EXCHANGE_RATE_AMOUNT.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_PREMIUM_CURRENCY_EXCH_AMOUNT.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_PREMIUM_CHARGE_OPERATION_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_CURRENCY_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_RECEIPT_ISSUE_DATE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_RECEIPT_START_DATE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_RECEIPT_END_DATE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_RECEIPT_COLLECTION_DATE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_RECEIPT_EXPIRATION_DATE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_RECEIPTS_TRANSMISSION_DATE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_RECEIPT_COLLECTION_STATUS_TYPE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_INSURANCE_COLLECTION_MOVE_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_PAYMENT_METHOD_TYPE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_DEBIT_ACCOUNT_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_DEBIT_CHANNEL_TYPE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_CHARGE_ATTEMPTS_NUMBER.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_INSRNC_CO_RECEIPT_STATUS_TYPE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_RECEIPT_STATUS_TYPE.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_MANAGEMENT_BRANCH_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_VARIABLE_PREMIUM_AMOUNT.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_FIX_PREMIUM_AMOUNT.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_SETTLEMENT_VAR_PREMIUM_AMOUNT.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_SETTLEMENT_FIX_PREMIUM_AMOUNT.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_LAST_CHANGE_BRANCH_ID.getValue()));
        assertNotNull(validation[0].get(RBVDProperties.FIELD_GL_BRANCH_ID.getValue()));

        assertEquals(receiptDao.getEntityId(), validation[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()));
        assertEquals(receiptDao.getBranchId(), validation[0].get(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()));
        assertEquals(receiptDao.getIntAccountId(), validation[0].get(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()));
        assertEquals(receiptDao.getPolicyReceiptId(), validation[0].get(RBVDProperties.FIELD_POLICY_RECEIPT_ID.getValue()));
        assertEquals(receiptDao.getInsuranceCompanyId(), validation[0].get(RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue()));
        assertEquals(receiptDao.getPremiumPaymentReceiptAmount(), validation[0].get(RBVDProperties.FIELD_PREMIUM_PAYMENT_RECEIPT_AMOUNT.getValue()));
        assertEquals(receiptDao.getFixingExchangeRateAmount(), validation[0].get(RBVDProperties.FIELD_FIXING_EXCHANGE_RATE_AMOUNT.getValue()));
        assertEquals(receiptDao.getPremiumCurrencyExchAmount(), validation[0].get(RBVDProperties.FIELD_PREMIUM_CURRENCY_EXCH_AMOUNT.getValue()));
        assertEquals(receiptDao.getPremiumChargeOperationId(), validation[0].get(RBVDProperties.FIELD_PREMIUM_CHARGE_OPERATION_ID.getValue()));
        assertEquals(receiptDao.getCurrencyId(), validation[0].get(RBVDProperties.FIELD_CURRENCY_ID.getValue()));
        assertEquals(receiptDao.getReceiptIssueDate(), validation[0].get(RBVDProperties.FIELD_RECEIPT_ISSUE_DATE.getValue()));
        assertEquals(receiptDao.getReceiptStartDate(), validation[0].get(RBVDProperties.FIELD_RECEIPT_START_DATE.getValue()));
        assertEquals(receiptDao.getReceiptEndDate(), validation[0].get(RBVDProperties.FIELD_RECEIPT_END_DATE.getValue()));
        assertEquals(receiptDao.getReceiptCollectionDate(), validation[0].get(RBVDProperties.FIELD_RECEIPT_COLLECTION_DATE.getValue()));
        assertEquals(receiptDao.getReceiptExpirationDate(), validation[0].get(RBVDProperties.FIELD_RECEIPT_EXPIRATION_DATE.getValue()));
        assertEquals(receiptDao.getReceiptsTransmissionDate(), validation[0].get(RBVDProperties.FIELD_RECEIPTS_TRANSMISSION_DATE.getValue()));
        assertEquals(receiptDao.getReceiptCollectionStatusType(), validation[0].get(RBVDProperties.FIELD_RECEIPT_COLLECTION_STATUS_TYPE.getValue()));
        assertEquals(receiptDao.getInsuranceCollectionMoveId(), validation[0].get(RBVDProperties.FIELD_INSURANCE_COLLECTION_MOVE_ID.getValue()));
        assertEquals(receiptDao.getPaymentMethodType(), validation[0].get(RBVDProperties.FIELD_PAYMENT_METHOD_TYPE.getValue()));
        assertEquals(receiptDao.getDebitAccountId(), validation[0].get(RBVDProperties.FIELD_DEBIT_ACCOUNT_ID.getValue()));
        assertEquals(receiptDao.getDebitChannelType(), validation[0].get(RBVDProperties.FIELD_DEBIT_CHANNEL_TYPE.getValue()));
        assertEquals(receiptDao.getChargeAttemptsNumber(), validation[0].get(RBVDProperties.FIELD_CHARGE_ATTEMPTS_NUMBER.getValue()));
        assertEquals(receiptDao.getInsrncCoReceiptStatusType(), validation[0].get(RBVDProperties.FIELD_INSRNC_CO_RECEIPT_STATUS_TYPE.getValue()));
        assertEquals(receiptDao.getReceiptStatusType(), validation[0].get(RBVDProperties.FIELD_RECEIPT_STATUS_TYPE.getValue()));
        assertEquals(receiptDao.getCreationUserId(), validation[0].get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertEquals(receiptDao.getUserAuditId(), validation[0].get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
        assertEquals(receiptDao.getManagementBranchId(), validation[0].get(RBVDProperties.FIELD_MANAGEMENT_BRANCH_ID.getValue()));
        assertEquals(receiptDao.getVariablePremiumAmount(), validation[0].get(RBVDProperties.FIELD_VARIABLE_PREMIUM_AMOUNT.getValue()));
        assertEquals(receiptDao.getFixPremiumAmount(), validation[0].get(RBVDProperties.FIELD_FIX_PREMIUM_AMOUNT.getValue()));
        assertEquals(receiptDao.getSettlementVarPremiumAmount(), validation[0].get(RBVDProperties.FIELD_SETTLEMENT_VAR_PREMIUM_AMOUNT.getValue()));
        assertEquals(receiptDao.getSettlementFixPremiumAmount(), validation[0].get(RBVDProperties.FIELD_SETTLEMENT_FIX_PREMIUM_AMOUNT.getValue()));
        assertEquals(receiptDao.getLastChangeBranchId(), validation[0].get(RBVDProperties.FIELD_LAST_CHANGE_BRANCH_ID.getValue()));
        assertEquals(receiptDao.getGlBranchId(), validation[0].get(RBVDProperties.FIELD_GL_BRANCH_ID.getValue()));
    }

    @Test
    public void mappingOutputFields_OK() {
        apxRequest.getFirstInstallment().setIsPaymentRequired(true);

        when(this.applicationConfigurationService.getProperty("FORMALIZADO")).thenReturn("FOR");

        mapperHelper.mappingOutputFields(apxRequest, asoResponse, rimacResponse, requiredFieldsEmissionDao);

        assertNotNull(apxRequest.getId());
        assertNotNull(apxRequest.getProductDescription());
        assertNotNull(apxRequest.getProductPlan().getDescription());
        assertNotNull(apxRequest.getOperationDate());
        assertNotNull(apxRequest.getValidityPeriod().getEndDate());
        assertNotNull(apxRequest.getTotalAmount().getExchangeRate());
        assertNotNull(apxRequest.getInstallmentPlan().getPeriod().getName());
        assertNotNull(apxRequest.getInstallmentPlan().getExchangeRate());
        apxRequest.getHolder().getContactDetails().forEach(contactDetail -> assertNotNull(contactDetail.getId()));
        apxRequest.getInspection().getContactDetails().forEach(contactDetail -> assertNotNull(contactDetail.getId()));
        assertNotNull(apxRequest.getFirstInstallment().getFirstPaymentDate());
        assertNotNull(apxRequest.getFirstInstallment().getAccountingDate());
        assertNotNull(apxRequest.getFirstInstallment().getOperationDate());
        assertNotNull(apxRequest.getFirstInstallment().getOperationNumber());
        assertNotNull(apxRequest.getFirstInstallment().getTransactionNumber());
        assertNotNull(apxRequest.getFirstInstallment().getExchangeRate());
        apxRequest.getParticipants().forEach(participant -> assertTrue(Objects.nonNull(participant.getId()) && Objects.nonNull(participant.getCustomerId())));
        assertNotNull(apxRequest.getInsuranceCompany().getName());
        assertNotNull(apxRequest.getInsuranceCompany().getProductId());
        assertNotNull(apxRequest.getExternalQuotationId());
        assertNotNull(apxRequest.getExternalPolicyNumber());
        assertNotNull(apxRequest.getStatus().getId());
        assertNotNull(apxRequest.getStatus().getDescription());
        assertNotNull(apxRequest.getHolder().getIdentityDocument().getDocumentNumber());

        assertEquals(asoResponse.getData().getId(), apxRequest.getId());
        assertEquals(requiredFieldsEmissionDao.getInsuranceProductDesc(), apxRequest.getProductDescription());
        assertEquals(requiredFieldsEmissionDao.getInsuranceModalityName(), apxRequest.getProductPlan().getDescription());
        assertEquals(asoResponse.getData().getOperationDate(), apxRequest.getOperationDate());
        assertEquals(requiredFieldsEmissionDao.getPaymentFrequencyName(),
                apxRequest.getInstallmentPlan().getPeriod().getName());
        assertEquals(asoResponse.getData().getFirstInstallment().getOperationNumber(),
                apxRequest.getFirstInstallment().getOperationNumber());
        assertEquals(asoResponse.getData().getFirstInstallment().getTransactionNumber(),
                apxRequest.getFirstInstallment().getTransactionNumber());
        assertEquals(asoResponse.getData().getInsuranceCompany().getName(), apxRequest.getInsuranceCompany().getName());
        assertEquals(rimacResponse.getPayload().getCodProducto(), apxRequest.getInsuranceCompany().getProductId());
        assertEquals(requiredFieldsEmissionDao.getInsuranceCompanyQuotaId(), apxRequest.getExternalQuotationId());
        assertEquals(rimacResponse.getPayload().getNumeroPoliza(), apxRequest.getExternalPolicyNumber());
        assertEquals("FOR", apxRequest.getStatus().getId());
        assertEquals(asoResponse.getData().getStatus().getDescription(), apxRequest.getStatus().getDescription());
        assertEquals("04040005", apxRequest.getHolder().getIdentityDocument().getDocumentNumber());


        asoResponse.getData().getTotalAmount().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        asoResponse.getData().getInstallmentPlan().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        asoResponse.getData().getFirstInstallment().getExchangeRate().getDetail().getFactor().setRatio(0.0);

        mapperHelper.mappingOutputFields(apxRequest, asoResponse, null, requiredFieldsEmissionDao);

        assertNull(apxRequest.getTotalAmount().getExchangeRate());
        assertNull(apxRequest.getInstallmentPlan().getExchangeRate());
        assertNull(apxRequest.getFirstInstallment().getExchangeRate());
        assertEquals(apxRequest.getInstallmentPlan().getMaturityDate(), apxRequest.getValidityPeriod().getEndDate());
        assertEquals("", apxRequest.getInsuranceCompany().getProductId());

    }

    @Test
    public void mappingOutputFieldsEndorsee_OK() {
        apxRequest.getFirstInstallment().setIsPaymentRequired(true);

        when(this.applicationConfigurationService.getProperty("FORMALIZADO")).thenReturn("FOR");
        ParticipantDTO participanteEndorsee = new ParticipantDTO();
        ParticipantTypeDTO tipoParticipante = new ParticipantTypeDTO();
        tipoParticipante.setId("ENDORSEE");
        participanteEndorsee.setBenefitPercentage(0.0d);
        participanteEndorsee.setParticipantType(tipoParticipante);

        IdentityDocumentDTO document = new IdentityDocumentDTO();
        DocumentTypeDTO tipoDocumento = new DocumentTypeDTO();
        tipoDocumento.setId("RUC");
        document.setDocumentType(tipoDocumento);
        document.setNumber("12345678");
        participanteEndorsee.setIdentityDocument(document);

        apxRequest.getParticipants().add(participanteEndorsee);

        mapperHelper.mappingOutputFields(apxRequest, asoResponse, rimacResponse, requiredFieldsEmissionDao);

        assertNotNull(apxRequest.getId());
        assertNotNull(apxRequest.getProductDescription());
        assertNotNull(apxRequest.getProductPlan().getDescription());
        assertNotNull(apxRequest.getOperationDate());
        assertNotNull(apxRequest.getValidityPeriod().getEndDate());
        assertNotNull(apxRequest.getTotalAmount().getExchangeRate());
        assertNotNull(apxRequest.getInstallmentPlan().getPeriod().getName());
        assertNotNull(apxRequest.getInstallmentPlan().getExchangeRate());
        apxRequest.getHolder().getContactDetails().forEach(contactDetail -> assertNotNull(contactDetail.getId()));
        apxRequest.getInspection().getContactDetails().forEach(contactDetail -> assertNotNull(contactDetail.getId()));
        assertNotNull(apxRequest.getFirstInstallment().getFirstPaymentDate());
        assertNotNull(apxRequest.getFirstInstallment().getAccountingDate());
        assertNotNull(apxRequest.getFirstInstallment().getOperationDate());
        assertNotNull(apxRequest.getFirstInstallment().getOperationNumber());
        assertNotNull(apxRequest.getFirstInstallment().getTransactionNumber());
        assertNotNull(apxRequest.getFirstInstallment().getExchangeRate());
        apxRequest.getParticipants().forEach(participant -> assertTrue(Objects.nonNull(participant.getId()) && Objects.nonNull(participant.getCustomerId())));
        assertNotNull(apxRequest.getInsuranceCompany().getName());
        assertNotNull(apxRequest.getInsuranceCompany().getProductId());
        assertNotNull(apxRequest.getExternalQuotationId());
        assertNotNull(apxRequest.getExternalPolicyNumber());
        assertNotNull(apxRequest.getStatus().getId());
        assertNotNull(apxRequest.getStatus().getDescription());
        assertNotNull(apxRequest.getHolder().getIdentityDocument().getDocumentNumber());

        assertEquals(asoResponse.getData().getId(), apxRequest.getId());
        assertEquals(requiredFieldsEmissionDao.getInsuranceProductDesc(), apxRequest.getProductDescription());
        assertEquals(requiredFieldsEmissionDao.getInsuranceModalityName(), apxRequest.getProductPlan().getDescription());
        assertEquals(asoResponse.getData().getOperationDate(), apxRequest.getOperationDate());
        assertEquals(requiredFieldsEmissionDao.getPaymentFrequencyName(),
                apxRequest.getInstallmentPlan().getPeriod().getName());
        assertEquals(asoResponse.getData().getFirstInstallment().getOperationNumber(),
                apxRequest.getFirstInstallment().getOperationNumber());
        assertEquals(asoResponse.getData().getFirstInstallment().getTransactionNumber(),
                apxRequest.getFirstInstallment().getTransactionNumber());
        assertEquals(asoResponse.getData().getInsuranceCompany().getName(), apxRequest.getInsuranceCompany().getName());
        assertEquals(rimacResponse.getPayload().getCodProducto(), apxRequest.getInsuranceCompany().getProductId());
        assertEquals(requiredFieldsEmissionDao.getInsuranceCompanyQuotaId(), apxRequest.getExternalQuotationId());
        assertEquals(rimacResponse.getPayload().getNumeroPoliza(), apxRequest.getExternalPolicyNumber());
        assertEquals("FOR", apxRequest.getStatus().getId());
        assertEquals(asoResponse.getData().getStatus().getDescription(), apxRequest.getStatus().getDescription());
        assertEquals("04040005", apxRequest.getHolder().getIdentityDocument().getDocumentNumber());


        asoResponse.getData().getTotalAmount().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        asoResponse.getData().getInstallmentPlan().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        asoResponse.getData().getFirstInstallment().getExchangeRate().getDetail().getFactor().setRatio(0.0);

        mapperHelper.mappingOutputFields(apxRequest, asoResponse, rimacResponse, requiredFieldsEmissionDao);

        assertNull(apxRequest.getTotalAmount().getExchangeRate());
        assertNull(apxRequest.getInstallmentPlan().getExchangeRate());
        assertNull(apxRequest.getFirstInstallment().getExchangeRate());
        assertEquals("", apxRequest.getParticipants().get(1).getId());
        assertEquals("", apxRequest.getParticipants().get(1).getCustomerId());



    }




    @Test
    public void buildCreateEmailRequest_OK() {
        apxRequest.setId("00110057794000023694");
        apxRequest.getProductPlan().setDescription("PLAN BASICO");

        CreateEmailASO email = mapperHelper.buildCreateEmailRequestVeh(requiredFieldsEmissionDao, apxRequest, rimacResponse.getPayload().getNumeroPoliza());

        assertNotNull(email.getApplicationId());
        assertNotNull(email.getRecipient());
        assertNotNull(email.getSubject());
        assertNotNull(email.getBody());
        assertNotNull(email.getSender());

        when(requiredFieldsEmissionDao.getVehicleLicenseId()).thenReturn(null);
        when(requiredFieldsEmissionDao.getGasConversionType()).thenReturn("N");
        when(requiredFieldsEmissionDao.getVehicleCirculationType()).thenReturn("P");
        email = mapperHelper.buildCreateEmailRequestVeh(requiredFieldsEmissionDao, apxRequest, rimacResponse.getPayload().getNumeroPoliza());
        assertNotNull(email.getBody());

        SimltInsuredHousingDAO emissionDAO = new SimltInsuredHousingDAO();
        emissionDAO.setDepartmentName("LIMA");
        emissionDAO.setProvinceName("LIMA");
        emissionDAO.setDistrictName("LINCE");
        emissionDAO.setHousingType("P");
        emissionDAO.setAreaPropertyNumber(new BigDecimal(100));
        emissionDAO.setPropSeniorityYearsNumber(new BigDecimal(10));
        emissionDAO.setFloorNumber(new BigDecimal(2));
        apxRequest.getFirstInstallment().getPaymentAmount().setCurrency("PEN");
        apxRequest.getProductPlan().setDescription("PLAN CONTENIDO");
        apxRequest.getProductPlan().setId("04");
        email = mapperHelper.buildCreateEmailRequestHome(requiredFieldsEmissionDao, apxRequest, rimacResponse.getPayload().getNumeroPoliza(), customerList, emissionDAO, "riskDirection");
        assertNotNull(email.getBody());

        emissionDAO.setHousingType("A");
        apxRequest.getProductPlan().setDescription("PLAN EDIFICACION");
        apxRequest.getProductPlan().setId("05");
        emissionDAO.setHousingAssetsLoanAmount(new BigDecimal(20000));
        email = mapperHelper.buildCreateEmailRequestHome(requiredFieldsEmissionDao, apxRequest, rimacResponse.getPayload().getNumeroPoliza(), customerList, emissionDAO, "riskDirection");
        assertNotNull(email.getBody());

        emissionDAO.setHousingType("A");
        apxRequest.getProductPlan().setDescription("PLAN EDIFICACION + CONTENIDO");
        apxRequest.getProductPlan().setId("06");
        emissionDAO.setEdificationLoanAmount(new BigDecimal(150000.00));
        email = mapperHelper.buildCreateEmailRequestHome(requiredFieldsEmissionDao, apxRequest, rimacResponse.getPayload().getNumeroPoliza(), null, emissionDAO, "riskDirection");
        assertNotNull(email.getBody());
    }

    @Test
    public void buildCreateEmailRequestFlexipyme_OK() {
        apxRequest.setId("00110057794000023694");
        apxRequest.getProductPlan().setDescription("PLAN BASICO");

        when(requiredFieldsEmissionDao.getVehicleLicenseId()).thenReturn(null);
        when(requiredFieldsEmissionDao.getGasConversionType()).thenReturn("N");
        when(requiredFieldsEmissionDao.getVehicleCirculationType()).thenReturn("P");

        SimltInsuredHousingDAO emissionDAO = new SimltInsuredHousingDAO();
        emissionDAO.setDepartmentName("LIMA");
        emissionDAO.setProvinceName("LIMA");
        emissionDAO.setDistrictName("LINCE");
        emissionDAO.setHousingType("P");
        emissionDAO.setAreaPropertyNumber(new BigDecimal(100));
        emissionDAO.setPropSeniorityYearsNumber(new BigDecimal(10));
        emissionDAO.setFloorNumber(new BigDecimal(2));
        apxRequest.getFirstInstallment().getPaymentAmount().setCurrency("PEN");
        apxRequest.getProductPlan().setDescription("PLAN CONTENIDO");
        apxRequest.getProductPlan().setId("04");
        apxRequest.setParticipants(null);
        CreateEmailASO email = mapperHelper.buildCreateEmailRequestFlexipyme(requiredFieldsEmissionDao, apxRequest,
                rimacResponse.getPayload().getNumeroPoliza(), customerList,
                emissionDAO, "riskDirection", "Empresa S.A.");
        assertNotNull(email.getBody());

        emissionDAO.setHousingType("I");
        ParticipantDTO legalParticipant = new ParticipantDTO();
        ParticipantTypeDTO participantType = new ParticipantTypeDTO();
        participantType.setId("LEGAL_REPRESENTATIVE");
        legalParticipant.setParticipantType(participantType);
        apxRequest.setParticipants(Collections.singletonList(legalParticipant));
        email = mapperHelper.buildCreateEmailRequestFlexipyme(requiredFieldsEmissionDao, apxRequest,
                rimacResponse.getPayload().getNumeroPoliza(), customerList,
                emissionDAO, "riskDirection", null);
        assertNotNull(email.getBody());

        IdentityDocumentDTO document = new IdentityDocumentDTO();
        document.setDocumentType(new DocumentTypeDTO());
        legalParticipant.setIdentityDocument(document);
        email = mapperHelper.buildCreateEmailRequestFlexipyme(requiredFieldsEmissionDao, apxRequest,
                rimacResponse.getPayload().getNumeroPoliza(), customerList,
                emissionDAO, "riskDirection", null);
        assertNotNull(email.getBody());
    }

    @Test
    public void createGifoleRequest_OK() {
        apxRequest.getValidityPeriod().setEndDate(new Date(2012, 01, 02));
        apxRequest.getInstallmentPlan().getPeriod().setName("MENSUAL");
        apxRequest.setExternalPolicyNumber("501481");
        apxRequest.setId("00110115304000510603");
        GifoleInsuranceRequestASO response1 = this.mapperHelper.createGifoleRequest(apxRequest, customerList, null);
        assertNotNull(response1.getQuotation());
        assertEquals(response1.getQuotation().getId(), apxRequest.getQuotationId());
        assertEquals("13000001", response1.getChannel());
        assertEquals("INSURANCE_CREATION", response1.getOperationType());
        assertNotNull(response1.getValidityPeriod());
        assertNotNull(response1.getInsurance());
        assertEquals("00110115304000510603", response1.getInsurance().getId());
        assertEquals("501481", response1.getPolicyNumber());
        assertNotNull(response1.getProduct());
        assertNotNull(response1.getHolder());

        apxRequest.getPaymentMethod().getRelatedContracts().get(0).getProduct().setId("ACCOUNT");
        GifoleInsuranceRequestASO response2 = this.mapperHelper.createGifoleRequest(apxRequest, null, null);
        assertNotNull(response2.getQuotation());
        assertEquals(response2.getQuotation().getId(), apxRequest.getQuotationId());
        assertEquals("13000001", response2.getChannel());
        assertEquals("INSURANCE_CREATION", response2.getOperationType());
        assertNotNull(response2.getValidityPeriod());
        assertNotNull(response2.getInsurance());
        assertEquals("00110115304000510603", response2.getInsurance().getId());
        assertEquals("501481", response2.getPolicyNumber());
        assertNotNull(response2.getProduct());
        assertNotNull(response2.getHolder());

        customerList.getData().get(0).setLastName(null);
        customerList.getData().get(0).setSecondLastName(null);

        GifoleInsuranceRequestASO response3 = this.mapperHelper.createGifoleRequest(apxRequest, null, null);
        assertEquals("", response3.getHolder().getLastName());
    }

    @Test
    public void createGifoleRequestWithRUC20_OK() {
        apxRequest.getValidityPeriod().setEndDate(new Date(2012, 01, 02));
        apxRequest.getInstallmentPlan().getPeriod().setName("MENSUAL");
        apxRequest.setExternalPolicyNumber("501481");
        apxRequest.setId("00110115304000510603");

        customerList.getData().get(0).getIdentityDocuments().get(0).setDocumentNumber("20999999991");
        customerList.getData().get(0).getIdentityDocuments().get(0).getDocumentType().setId("R");
        customerList.getData().get(0).setLastName(null);

        GifoleInsuranceRequestASO response1 = this.mapperHelper.createGifoleRequest(apxRequest, customerList, null);
        assertNotNull(response1.getQuotation());
        assertEquals(response1.getQuotation().getId(), apxRequest.getQuotationId());
        assertEquals("13000001", response1.getChannel());
        assertEquals("INSURANCE_CREATION", response1.getOperationType());
        assertNotNull(response1.getValidityPeriod());
        assertNotNull(response1.getInsurance());
        assertEquals("00110115304000510603", response1.getInsurance().getId());
        assertEquals("501481", response1.getPolicyNumber());
        assertNotNull(response1.getProduct());
        assertNotNull(response1.getHolder());
    }

    @Test
    public void getPersonTypeTest_OK() {
        EntidadBO person = new EntidadBO();
        PersonTypeEnum response = this.mapperHelper.getPersonType(person);
        assertEquals(PersonTypeEnum.NATURAL, response);

        person.setTipoDocumento("R");
        person.setNroDocumento("20999999991");
        response = this.mapperHelper.getPersonType(person);
        assertEquals(PersonTypeEnum.JURIDIC, response);

        person.setNroDocumento("10999999991");
        response = this.mapperHelper.getPersonType(person);
        assertEquals(PersonTypeEnum.NATURAL_WITH_BUSINESS, response);
    }
}
