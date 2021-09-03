package com.bbva.rbvd.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.aso.ExchangeRateASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DetailASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.FactorASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.CuotaFinancimientoBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.commons.PolicyInspectionDTO;
import com.bbva.rbvd.dto.insrncsale.dao.*;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.*;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MapperHelperTest {

    private final MapperHelper mapperHelper = new MapperHelper();
    private ApplicationConfigurationService applicationConfigurationService;
    private final MockData mockData = MockData.getInstance();
    
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
        when(requiredFieldsEmissionDao.getCommercialVehicleAmount()).thenReturn(BigDecimal.valueOf(23.000));

        apxRequest = mockData.getCreateInsuranceRequestBody();
        apxRequest.setCreationUser("creationUser");
        apxRequest.setUserAudit("userAudit");
        apxRequest.setSaleChannelId("BI");
        asoResponse = mockData.getEmisionASOResponse();
        rimacResponse = mockData.getEmisionRimacResponse();
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
    }

    @Test
    public void buildRequestBodyRimac_OK() {
        PolicyInspectionDTO inspection = apxRequest.getInspection();
        EmisionBO validation = mapperHelper.buildRequestBodyRimac(inspection, "secondValue", "channelCode", "dataId");

        assertNotNull(validation.getPayload().getContactoInspeccion());
        assertNotNull(validation.getPayload().getContactoInspeccion().getNombre());
        assertNotNull(validation.getPayload().getContactoInspeccion().getCorreo());
        assertNotNull(validation.getPayload().getContactoInspeccion().getTelefono());

        assertFalse(validation.getPayload().getDatosParticulares().isEmpty());
        assertNotNull(validation.getPayload().getDatosParticulares().get(0).getEtiqueta());
        assertNotNull(validation.getPayload().getDatosParticulares().get(0).getCodigo());
        assertNotNull(validation.getPayload().getDatosParticulares().get(0).getValor());
        assertNotNull(validation.getPayload().getDatosParticulares().get(1).getEtiqueta());
        assertNotNull(validation.getPayload().getDatosParticulares().get(1).getCodigo());
        assertNotNull(validation.getPayload().getDatosParticulares().get(1).getValor());

        assertNotNull(validation.getPayload().getEnvioElectronico());
        assertNotNull(validation.getPayload().getIndCobro());
        assertNotNull(validation.getPayload().getIndInspeccion());
        assertNotNull(validation.getPayload().getIndValidaciones());

        assertEquals(inspection.getFullName(), validation.getPayload().getContactoInspeccion().getNombre());
        assertEquals(inspection.getContactDetails().get(0).getContact().getAddress(), validation.getPayload().getContactoInspeccion().getCorreo());
        assertEquals(inspection.getContactDetails().get(1).getContact().getPhoneNumber(), validation.getPayload().getContactoInspeccion().getTelefono());
        assertEquals("CANAL_TERCERO", validation.getPayload().getDatosParticulares().get(0).getEtiqueta());
        assertEquals("channelCode", validation.getPayload().getDatosParticulares().get(0).getValor());
        assertEquals("DATOS_DE_CUENTA", validation.getPayload().getDatosParticulares().get(1).getEtiqueta());
        assertEquals("secondValue", validation.getPayload().getDatosParticulares().get(1).getValor());
        assertEquals("NRO_CERT_BANCO", validation.getPayload().getDatosParticulares().get(2).getEtiqueta());
        assertEquals("dataId", validation.getPayload().getDatosParticulares().get(2).getValor());
        assertEquals(S_VALUE, validation.getPayload().getEnvioElectronico());
        assertEquals(N_VALUE, validation.getPayload().getIndCobro());
        assertEquals(Optional.of(1L).get(), validation.getPayload().getIndInspeccion());
        assertEquals(N_VALUE, validation.getPayload().getIndValidaciones());
    }

    @Test
    public void buildInsuranceContract_OK() {

        InsuranceContractDAO validation = mapperHelper.buildInsuranceContract(rimacResponse, apxRequest, requiredFieldsEmissionDao, "00110241400000001102");

        assertNotNull(validation.getEntityId());
        assertNotNull(validation.getBranchId());
        assertNotNull(validation.getIntAccountId());
        assertNotNull(validation.getFirstVerfnDigitId());
        assertNotNull(validation.getSecondVerfnDigitId());
        assertNotNull(validation.getPolicyQuotaInternalId());
        assertNotNull(validation.getInsuranceProductId());
        assertNotNull(validation.getInsuranceModalityType());
        assertNotNull(validation.getInsuranceCompanyId());
        assertNotNull(validation.getPolicyId());
        assertNotNull(validation.getInsuranceCompanyProductId());
        assertNotNull(validation.getInsuranceManagerId());
        assertNotNull(validation.getInsurancePromoterId());
        assertNotNull(validation.getContractManagerBranchId());

        assertNotNull(validation.getInsuranceContractStartDate());
        assertNotNull(validation.getInsuranceContractEndDate());
        assertNotNull(validation.getValidityMonthsNumber());
        assertNotNull(validation.getInsurancePolicyEndDate());

        assertNull(validation.getCustomerId());
        assertNotNull(validation.getDomicileContractId());
        assertNotNull(validation.getCardIssuingMarkType());
        assertNotNull(validation.getIssuedReceiptNumber());
        assertNotNull(validation.getPremiumAmount());
        assertNotNull(validation.getSettlePendingPremiumAmount());
        assertNotNull(validation.getCurrencyId());
        assertNotNull(validation.getInsuredAmount());
        assertNotNull(validation.getBeneficiaryType());
        assertNotNull(validation.getRenewalNumber());
        assertNotNull(validation.getPolicyPymtPendDueDebtType());
        assertNotNull(validation.getCtrctDisputeStatusType());
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
        assertEquals(rimacResponse.getPayload().getNumeroPoliza(), validation.getPolicyId());
        assertEquals(apxRequest.getBusinessAgent().getId(), validation.getInsuranceManagerId());
        assertEquals(apxRequest.getPromoter().getId(), validation.getInsurancePromoterId());
        assertEquals(apxRequest.getBank().getBranch().getId(), validation.getContractManagerBranchId());
        assertEquals("02/06/2022", validation.getInsuranceContractEndDate());
        assertEquals(requiredFieldsEmissionDao.getContractDurationNumber(), validation.getValidityMonthsNumber());
        assertEquals("02/06/2022", validation.getInsurancePolicyEndDate());
        assertEquals("02/05/2022", validation.getLastInstallmentDate());
        assertEquals("02/07/2021", validation.getPeriodNextPaymentDate());
        assertEquals(apxRequest.getHolder().getId(), validation.getCustomerId());
        assertEquals(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getContractId(), validation.getDomicileContractId());
        assertEquals(N_VALUE, validation.getCardIssuingMarkType());
        assertEquals(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()), validation.getIssuedReceiptNumber());
        assertEquals(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()), validation.getPremiumAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getPaymentAmount().getAmount()), validation.getSettlePendingPremiumAmount());
        assertEquals(apxRequest.getInstallmentPlan().getPaymentAmount().getCurrency(), validation.getCurrencyId());
        assertEquals(BigDecimal.valueOf(apxRequest.getInsuredAmount().getAmount()), validation.getInsuredAmount());
        assertEquals("08", validation.getBeneficiaryType());
        assertEquals(BigDecimal.valueOf(0), validation.getRenewalNumber());
        assertEquals(N_VALUE, validation.getPolicyPymtPendDueDebtType());
        assertEquals("BI", validation.getCtrctDisputeStatusType());
        assertEquals(N_VALUE, validation.getEndorsementPolicyIndType());
        assertEquals("PEN", validation.getInsrncCoContractStatusType());
        assertEquals("FOR", validation.getContractStatusId());
        assertEquals(apxRequest.getCreationUser(), validation.getCreationUserId());
        assertEquals(apxRequest.getUserAudit(), validation.getUserAuditId());
        assertEquals(S_VALUE, validation.getInsurPendingDebtIndType());
        assertEquals(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()), validation.getTotalDebtAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()), validation.getPrevPendBillRcptsNumber());
        assertEquals(BigDecimal.valueOf(0), validation.getSettlementVarPremiumAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getPaymentAmount().getAmount()), validation.getSettlementFixPremiumAmount());
        assertEquals(rimacResponse.getPayload().getCodProducto(), validation.getInsuranceCompanyProductId());
        assertEquals(S_VALUE, validation.getAutomaticDebitIndicatorType());
        assertEquals(apxRequest.getIdentityVerificationCode(), validation.getBiometryTransactionId());

        CuotaFinancimientoBO cuota = new CuotaFinancimientoBO();
        cuota.setCuota(1L);
        cuota.setMonto(77.03);
        cuota.setFechaVencimiento(LocalDate.now());
        cuota.setMoneda("USD");

        rimacResponse.getPayload().setCuotasFinanciamiento(Collections.singletonList(cuota));

        apxRequest.getFirstInstallment().setIsPaymentRequired(true);
        apxRequest.getPaymentMethod().setPaymentType("somethingElse");

        when(requiredFieldsEmissionDao.getContractDurationType()).thenReturn("A");
        when(requiredFieldsEmissionDao.getContractDurationNumber()).thenReturn(BigDecimal.ONE);

        validation = mapperHelper.buildInsuranceContract(rimacResponse, apxRequest, requiredFieldsEmissionDao, "00110241400000001102");

        assertEquals(BigDecimal.valueOf(0), validation.getTotalDebtAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments() - 1), validation.getPrevPendBillRcptsNumber());
        assertEquals(BigDecimal.valueOf(12), validation.getValidityMonthsNumber());
        assertEquals("02/06/2022", validation.getPeriodNextPaymentDate());
        assertEquals(N_VALUE, validation.getInsurPendingDebtIndType());
        assertEquals(N_VALUE, validation.getAutomaticDebitIndicatorType());
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
    public void buildInsuranceCtrReceipt_OK() {
        asoResponse.getData().getFirstInstallment().setOperationDate(null);
        List<InsuranceCtrReceiptsDAO> validation = mapperHelper.buildInsuranceCtrReceipt(asoResponse, rimacResponse, apxRequest);

        assertNotNull(validation.get(0).getEntityId());
        assertNotNull(validation.get(0).getBranchId());
        assertNotNull(validation.get(0).getIntAccountId());
        assertNotNull(validation.get(0).getPolicyReceiptId());
        assertNotNull(validation.get(0).getInsuranceCompanyId());
        assertNotNull(validation.get(0).getPremiumPaymentReceiptAmount());
        assertNotNull(validation.get(0).getCurrencyId());
        assertNotNull(validation.get(0).getReceiptStartDate());
        assertNotNull(validation.get(0).getReceiptEndDate());
        assertNotNull(validation.get(0).getReceiptExpirationDate());
        assertNotNull(validation.get(0).getReceiptCollectionStatusType());
        assertNotNull(validation.get(0).getPaymentMethodType());
        assertNotNull(validation.get(0).getDebitAccountId());
        assertNotNull(validation.get(0).getDebitChannelType());
        assertNotNull(validation.get(0).getChargeAttemptsNumber());
        assertNotNull(validation.get(0).getInsrncCoReceiptStatusType());
        assertNotNull(validation.get(0).getReceiptStatusType());
        assertNotNull(validation.get(0).getCreationUserId());
        assertNotNull(validation.get(0).getUserAuditId());
        assertNotNull(validation.get(0).getManagementBranchId());
        assertNotNull(validation.get(0).getVariablePremiumAmount());
        assertNotNull(validation.get(0).getFixPremiumAmount());
        assertNotNull(validation.get(0).getSettlementVarPremiumAmount());
        assertNotNull(validation.get(0).getSettlementFixPremiumAmount());
        assertNotNull(validation.get(0).getLastChangeBranchId());
        assertNotNull(validation.get(0).getGlBranchId());

        assertEquals("0011", validation.get(0).getEntityId());
        assertEquals("0241", validation.get(0).getBranchId());
        assertEquals("0000001102", validation.get(0).getIntAccountId());
        assertEquals(BigDecimal.valueOf(1), validation.get(0).getPolicyReceiptId());
        assertEquals(BigDecimal.valueOf(1), validation.get(0).getInsuranceCompanyId());
        assertEquals(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()), validation.get(0).getPremiumPaymentReceiptAmount());
        assertEquals(BigDecimal.valueOf(asoResponse.getData()
                .getFirstInstallment().getExchangeRate().getDetail().getFactor().getRatio()), validation.get(0).getFixingExchangeRateAmount());
        assertEquals(BigDecimal.valueOf(asoResponse.getData()
                .getFirstInstallment().getExchangeRate().getDetail().getFactor().getValue()), validation.get(0).getPremiumCurrencyExchAmount());
        assertEquals(apxRequest.getFirstInstallment().getPaymentAmount().getCurrency(), validation.get(0).getCurrencyId());
        assertEquals("01/01/0001", validation.get(0).getReceiptStartDate());
        assertEquals("01/01/0001", validation.get(0).getReceiptEndDate());
        assertEquals("01/01/0001", validation.get(0).getReceiptIssueDate());
        assertEquals("01/01/0001", validation.get(0).getReceiptCollectionDate());
        assertEquals("01/01/0001", validation.get(0).getReceiptsTransmissionDate());
        assertEquals("02/06/2021", validation.get(0).getReceiptExpirationDate());
        assertEquals("00", validation.get(0).getReceiptCollectionStatusType());
        assertEquals("T", validation.get(0).getPaymentMethodType());
        assertEquals(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getContractId(), validation.get(0).getDebitAccountId());
        assertEquals(apxRequest.getSaleChannelId(), validation.get(0).getDebitChannelType());
        assertEquals(BigDecimal.valueOf(0), validation.get(0).getChargeAttemptsNumber());
        assertEquals("INC", validation.get(0).getInsrncCoReceiptStatusType());
        assertEquals("INC", validation.get(0).getReceiptStatusType());
        assertEquals(apxRequest.getCreationUser(), validation.get(0).getCreationUserId());
        assertEquals(apxRequest.getUserAudit(), validation.get(0).getUserAuditId());
        assertEquals(apxRequest.getBank().getBranch().getId(), validation.get(0).getManagementBranchId());
        assertEquals(BigDecimal.valueOf(0), validation.get(0).getVariablePremiumAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()), validation.get(0).getFixPremiumAmount());
        assertEquals(BigDecimal.valueOf(0), validation.get(0).getSettlementVarPremiumAmount());
        assertEquals(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getPaymentAmount().getAmount()), validation.get(0).getSettlementFixPremiumAmount());
        assertEquals(apxRequest.getBank().getBranch().getId(), validation.get(0).getLastChangeBranchId());
        assertEquals("0241", validation.get(0).getGlBranchId());

        ExchangeRateASO exchangeRate = new ExchangeRateASO();
        DetailASO detail = new DetailASO();
        FactorASO factor = new FactorASO();
        factor.setRatio(3.93);
        factor.setValue(550.2);
        detail.setFactor(factor);
        exchangeRate.setDetail(detail);

        asoResponse.getData().getFirstInstallment().setExchangeRate(exchangeRate);
        asoResponse.getData().getFirstInstallment().setOperationDate(new Date());
        apxRequest.getPaymentMethod().getRelatedContracts().get(0).getProduct().setId("ACCOUNT");
        apxRequest.getFirstInstallment().setIsPaymentRequired(true);

        validation = mapperHelper.buildInsuranceCtrReceipt(asoResponse, rimacResponse, apxRequest);

        assertEquals(BigDecimal.valueOf(asoResponse.getData()
                .getFirstInstallment().getExchangeRate().getDetail().getFactor().getRatio()), validation.get(0).getFixingExchangeRateAmount());
        assertEquals(BigDecimal.valueOf(asoResponse.getData()
                .getFirstInstallment().getExchangeRate().getDetail().getFactor().getValue()), validation.get(0).getPremiumCurrencyExchAmount());
        assertEquals("COB", validation.get(0).getReceiptStatusType());
    }

    @Test
    public void createSaveReceiptsArguments_OK() {
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
        when(receiptDao.getReceiptStartDate()).thenReturn("03/02/2021");
        when(receiptDao.getReceiptEndDate()).thenReturn("03/02/2021");
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

        mapperHelper.mappingOutputFields(apxRequest, asoResponse, rimacResponse, requiredFieldsEmissionDao);

        assertNull(apxRequest.getTotalAmount().getExchangeRate());
        assertNull(apxRequest.getInstallmentPlan().getExchangeRate());
        assertNull(apxRequest.getFirstInstallment().getExchangeRate());

        apxRequest.setBusinessAgent(null);
        apxRequest.setPromoter(null);

        mapperHelper.mappingOutputFields(apxRequest, asoResponse, rimacResponse, requiredFieldsEmissionDao);

        assertEquals(asoResponse.getData().getBusinessAgent().getId(),
                apxRequest.getBusinessAgent().getId());

        assertEquals(asoResponse.getData().getPromoter().getId(),
                apxRequest.getPromoter().getId());
    }

    @Test
    public void buildCreateEmailRequest_OK() {
        apxRequest.setId("00110057794000023694");
        apxRequest.getProductPlan().setDescription("PLAN BASICO");

        CreateEmailASO email = mapperHelper.buildCreateEmailRequest(requiredFieldsEmissionDao, apxRequest, rimacResponse.getPayload().getNumeroPoliza());

        assertNotNull(email.getApplicationId());
        assertNotNull(email.getRecipient());
        assertNotNull(email.getSubject());
        assertNotNull(email.getBody());
        assertNotNull(email.getSender());

        when(requiredFieldsEmissionDao.getVehicleLicenseId()).thenReturn(null);
        when(requiredFieldsEmissionDao.getGasConversionType()).thenReturn("N");
        when(requiredFieldsEmissionDao.getVehicleCirculationType()).thenReturn("P");

        email = mapperHelper.buildCreateEmailRequest(requiredFieldsEmissionDao, apxRequest, rimacResponse.getPayload().getNumeroPoliza());

        assertNotNull(email.getBody());
    }
}
