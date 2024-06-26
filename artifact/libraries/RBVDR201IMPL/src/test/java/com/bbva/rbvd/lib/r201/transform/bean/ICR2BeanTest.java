package com.bbva.rbvd.lib.r201.transform.bean;


import com.bbva.rbvd.dto.cicsconnection.ic.ICContract;
import com.bbva.rbvd.dto.cicsconnection.icr2.ICMRYS2;
import com.bbva.rbvd.dto.insrncsale.aso.*;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BankASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BranchASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.*;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.mock.EntityMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.ASO_VALUES.EXTERNAL_CONTRACT_OUT;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.ASO_VALUES.INTERNAL_CONTRACT_OUT;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ICR2BeanTest {

    @Test
    public void mapOutFirstInstallment_returnsNull_whenAllParametersAreNull() {
        ICMRYS2 formato = mock(ICMRYS2.class);
        FirstInstallmentASO result = ICRBean.mapOutFirstInstallment(formato);
        assertNull(result);
    }


    @Test
    public void mapOutPaymentAmount_returnsNull_whenAmountAndCurrencyAreNull() {
        BigDecimal amount = null;
        String currency = null;
        PaymentAmountASO result = ICRBean.mapOutPaymenAmount(amount, currency);

        assertNull(result);
    }

    @Test
    public void mapOutPaymentAmount_returnsPaymentAmount_withNullAmount_whenCurrencyIsNotNull() {
        BigDecimal amount = null;
        String currency = "USD";
        PaymentAmountASO result = ICRBean.mapOutPaymenAmount(amount, currency);

        assertNotNull(result);
        assertNull(result.getAmount());
        assertEquals(currency, result.getCurrency());
    }

    @Test
    public void mapOutPaymentAmount_returnsPaymentAmount_withAmountAndCurrency() {
        BigDecimal amount = new BigDecimal(100);
        String currency = "USD";
        PaymentAmountASO result = ICRBean.mapOutPaymenAmount(amount, currency);

        assertNotNull(result);
        assertEquals(amount.doubleValue(), result.getAmount(), 0.01);
        assertEquals(currency, result.getCurrency());
    }

    @Test
    public void mapOutPeriod_returnsNull_whenPeriodIdAndPeriodNameAreNull() {
        PaymentPeriodASO result = ICRBean.mapOutPeriod(null, null);
        assertNull(result);
    }

    @Test
    public void mapOutPeriod_returnsPaymentPeriod_withOnlyId_whenPeriodNameIsNull() {
        String periodId = "MONTHLY";
        PaymentPeriodASO result = ICRBean.mapOutPeriod(periodId, null);

        assertNotNull(result);
        assertEquals(periodId, result.getId());
        assertNull(result.getName());
    }

    @Test
    public void mapOutPeriod_returnsPaymentPeriod_withOnlyName_whenPeriodIdIsNull() {
        String periodName = "Monthly Payment";
        PaymentPeriodASO result = ICRBean.mapOutPeriod(null, periodName);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(periodName, result.getName());
    }

    @Test
    public void mapOutPeriod_returnsPaymentPeriod_whenPeriodIdAndPeriodNameAreNotNull() {
        String periodId = "MONTHLY";
        String periodName = "Monthly Payment";
        PaymentPeriodASO result = ICRBean.mapOutPeriod(periodId, periodName);

        assertNotNull(result);
        assertEquals(periodId, result.getId());
        assertEquals(periodName, result.getName());
    }

    @Test
    public void mapOutRelatedContracts_returnsNull_whenContractTypeAndContractNumberIdAreNull() {
        List<RelatedContractASO> result = ICRBean.mapOutRelatedContracts(null, null);
        assertNull(result);
    }

    @Test
    public void mapOutRelatedContracts_returnsRelatedContract_withContractDetails_whenContractTypeAndContractNumberIdAreNotNull() {
        String contractType = INTERNAL_CONTRACT_OUT;
        String contractNumberId = "123";

        List<RelatedContractASO> result = ICRBean.mapOutRelatedContracts(contractType, contractNumberId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getContractDetails());
        assertEquals(contractType, result.get(0).getContractDetails().getContractType());
        assertEquals(contractNumberId, result.get(0).getContractDetails().getContractId());
    }

    @Test
    public void mapOutContractDetails_returnsNull_whenContractTypeAndContractNumberIdAreNull() {
        ContractDetailsASO result = ICRBean.mapOutContractDeatils(null, null);
        assertNull(result);
    }

    @Test
    public void mapOutContractDetails_returnsContractDetails_withInternalContractType_whenContractTypeIsInternal() {
        String contractType = INTERNAL_CONTRACT_OUT;
        String contractNumberId = "123";

        ContractDetailsASO result = ICRBean.mapOutContractDeatils(contractType, contractNumberId);

        assertNotNull(result);
        assertEquals(contractType, result.getContractType());
        assertEquals(contractNumberId, result.getContractId());
    }

    @Test
    public void mapOutContractDetails_returnsContractDetails_withExternalContractType_whenContractTypeIsExternal() {
        String contractType = EXTERNAL_CONTRACT_OUT;
        String contractNumberId = "456";

        ContractDetailsASO result = ICRBean.mapOutContractDeatils(contractType, contractNumberId);

        assertNotNull(result);
        assertEquals(contractType, result.getContractType());
        assertEquals(contractNumberId, result.getNumber());
    }

    @Test
    public void mapOutHolder_returnsNull_whenAllParametersAreNull() {
        HolderASO result = ICRBean.mapOutHolder(null, null, null);
        assertNull(result);
    }

    @Test
    public void mapOutHolder_returnsHolder_withNullIdentityDocument_whenDocumentTypeAndIdentityDocumentNumberAreNull() {
        String id = "123";
        HolderASO result = ICRBean.mapOutHolder(id, null, null);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertNull(result.getIdentityDocument());
    }

    @Test
    public void mapOutHolder_returnsHolder_withIdentityDocument_whenAllParametersAreNotNull() {
        String id = "123";
        String documentType = "DNI";
        String identityDocumentNumber = "456789";

        HolderASO result = ICRBean.mapOutHolder(id, documentType, identityDocumentNumber);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertNotNull(result.getIdentityDocument());
        assertEquals(documentType, result.getIdentityDocument().getDocumentType().getId());
        assertEquals(identityDocumentNumber, result.getIdentityDocument().getNumber());
    }

    @Test
    public void mapOutIdentityDocument_returnsNull_whenDocumentTypeAndIdentityDocumentNumberAreNull() {
        IdentityDocumentASO result = ICRBean.mapOutIdentityDocument(null, null);
        assertNull(result);
    }

    @Test
    public void mapOutIdentityDocument_returnsIdentityDocument_whenDocumentTypeAndIdentityDocumentNumberAreNotNull() {
        String documentType = "DNI";
        String identityDocumentNumber = "12345678";

        IdentityDocumentASO result = ICRBean.mapOutIdentityDocument(documentType, identityDocumentNumber);

        assertNotNull(result);
        assertEquals(documentType, result.getDocumentType().getId());
        assertEquals(identityDocumentNumber, result.getNumber());
    }

    @Test
    public void mapOutIdentityDocument_returnsIdentityDocument_withNullDocumentType_whenDocumentTypeIsNull() {
        String identityDocumentNumber = "12345678";

        IdentityDocumentASO result = ICRBean.mapOutIdentityDocument(null, identityDocumentNumber);

        assertNotNull(result);
        assertNull(result.getDocumentType());
        assertEquals(identityDocumentNumber, result.getNumber());
    }

    @Test
    public void mapOutIdentityDocument_returnsIdentityDocument_withNullNumber_whenIdentityDocumentNumberIsNull() {
        String documentType = "DNI";

        IdentityDocumentASO result = ICRBean.mapOutIdentityDocument(documentType, null);

        assertNotNull(result);
        assertEquals(documentType, result.getDocumentType().getId());
        assertNull(result.getNumber());
    }

    @Test
    public void mapOutDocumentType_returnsNull_whenDocumentTypeIsNull() {
        DocumentTypeASO result = ICRBean.mapOutDocumentType(null);
        assertNull(result);
    }

    @Test
    public void mapOutDocumentType_returnsDocumentType_whenDocumentTypeIsNotNull() {
        String documentType = "DNI";
        DocumentTypeASO result = ICRBean.mapOutDocumentType(documentType);

        assertNotNull(result);
        assertEquals(documentType, result.getId());
    }

    @Test
    public void mapOutInsuredAmount_returnsNull_whenAmountAndCurrencyAreNull() {
        InsuredAmountASO result = ICRBean.mapOutInsuredAmount(null, null);

        assertNull(result);
    }

    @Test
    public void mapOutInsuredAmount_returnsInsuredAmount_withNullAmount_whenAmountIsNull() {
        String currency = "USD";

        InsuredAmountASO result = ICRBean.mapOutInsuredAmount(null, currency);

        assertNotNull(result);
        assertNull(result.getAmount());
        assertEquals(currency, result.getCurrency());
    }

    @Test
    public void mapOutInsuredAmount_returnsInsuredAmount_withNullCurrency_whenCurrencyIsNull() {
        BigDecimal amount = BigDecimal.valueOf(100);

        InsuredAmountASO result = ICRBean.mapOutInsuredAmount(amount, null);

        assertNotNull(result);
        assertEquals(amount.doubleValue(), result.getAmount(), 0.001);
        assertNull(result.getCurrency());
    }

    @Test
    public void mapOutInsuredAmount_returnsInsuredAmount_whenAmountAndCurrencyAreNotNull() {
        BigDecimal amount = BigDecimal.valueOf(100);
        String currency = "USD";

        InsuredAmountASO result = ICRBean.mapOutInsuredAmount(amount, currency);

        assertNotNull(result);
        assertEquals(amount.doubleValue(), result.getAmount(), 0.001);
        assertEquals(currency, result.getCurrency());
    }

    @Test
    public void mapOutTotalAmount_returnsNull_whenAmountAndCurrencyAreNull() {
        TotalAmountASO result = ICRBean.mapOutTotalAmount(null, null, "2022-01-01", "USD", "EUR", BigDecimal.valueOf(1.2), BigDecimal.valueOf(1.1), "priceType");

        assertNull(result);
    }

    @Test
    public void mapOutTotalAmount_returnsTotalAmount_whenAmountAndCurrencyAreNotNull() {
        TotalAmountASO result = ICRBean.mapOutTotalAmount(BigDecimal.valueOf(100), "USD", "2022-01-01", "USD", "EUR", BigDecimal.valueOf(1.2), BigDecimal.valueOf(1.1), "priceType");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100).doubleValue(), result.getAmount(), 0.001);
        assertEquals("USD", result.getCurrency());
        assertNotNull(result.getExchangeRate());
    }

    @Test
    public void mapOutTotalAmount_returnsTotalAmount_withNullExchangeRate_whenExchangeRateParametersAreNull() {
        TotalAmountASO result = ICRBean.mapOutTotalAmount(BigDecimal.valueOf(100), "USD", null, null, null, null, null, null);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100).doubleValue(), result.getAmount(), 0.001);
        assertEquals("USD", result.getCurrency());
        assertNull(result.getExchangeRate());
    }

    @Test
    public void mapOutValidityPeriod_returnsNull_whenStartDateAndEndDateAreNull() {
        ValidityPeriodASO result = ICRBean.mapOutValidityPeriod(null, null);
        assertNull(result);
    }

    @Test
    public void mapOutValidityPeriod_returnsValidityPeriod_whenStartDateAndEndDateAreNotNull() {
        String startDate = "2022-01-01";
        String endDate = "2022-12-31";

        ValidityPeriodASO result = ICRBean.mapOutValidityPeriod(startDate, endDate);

        assertNotNull(result);
        assertEquals(startDate, result.getStartDate().toString());
        assertEquals(endDate, result.getEndDate().toString());
    }


    @Test
    public void mapOutPaymentMethod_returnsNull_whenAllParametersAreNull() {
        PaymentMethodASO result = ICRBean.mapOutPaymentMethod(null, null, null, null);
        assertNull(result);
    }


    @Test
    public void mapOutPaymentMethod_returnsPaymentMethod_withNullRelatedContracts_whenRelatedContractNumberAndProductIdAreNull() {
        PaymentMethodASO result = ICRBean.mapOutPaymentMethod("paymentType", "installmentFrequency", null, null);

        assertNotNull(result);
        assertEquals("paymentType", result.getPaymentType());
        assertEquals("installmentFrequency", result.getInstallmentFrequency());
        assertNull(result.getRelatedContracts());
    }

    @Test
    public void mapOutPaymentMethodRelatedContracts_returnsNull_whenRelatedContractNumberAndProductIdAreNull() {
        List<RelatedContractASO> result = ICRBean.mapOutPaymentMethodRelatedContracts(null, null);
        assertNull(result);
    }

    @Test
    public void mapOutPaymentMethodRelatedContracts_returnsRelatedContract_whenRelatedContractNumberAndProductIdAreNotNull() {
        String relatedContractNumber = "123";
        String relatedContractProductId = "456";

        List<RelatedContractASO> result = ICRBean.mapOutPaymentMethodRelatedContracts(relatedContractNumber, relatedContractProductId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(relatedContractNumber, result.get(0).getNumber());
        assertEquals(relatedContractProductId, result.get(0).getProduct().getId());
    }

    @Test
    public void mapOutPaymentMethodRelatedContracts_returnsRelatedContract_withNullProduct_whenRelatedContractProductIdIsNull() {
        String relatedContractNumber = "123";

        List<RelatedContractASO> result = ICRBean.mapOutPaymentMethodRelatedContracts(relatedContractNumber, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(relatedContractNumber, result.get(0).getNumber());
        assertNull(result.get(0).getProduct());
    }

    @Test
    public void mapOutPaymentMethodRelatedContracts_returnsRelatedContract_withNullNumber_whenRelatedContractNumberIsNull() {
        String relatedContractProductId = "456";

        List<RelatedContractASO> result = ICRBean.mapOutPaymentMethodRelatedContracts(null, relatedContractProductId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getNumber());
        assertEquals(relatedContractProductId, result.get(0).getProduct().getId());
    }

    @Test
    public void mapOutExchangeRate_returnsNull_whenAllParametersAreNull() {
        ExchangeRateASO result = ICRBean.mapOutExchangeRate(null, null, null, null, null, null);
        assertNull(result);
    }

    @Test
    public void mapOutExchangeRate_returnsExchangeRate_whenParametersAreNotNull() {
        ExchangeRateASO result = ICRBean.mapOutExchangeRate("2022-01-01", "USD", "EUR", BigDecimal.ONE, BigDecimal.ONE, "priceType");
        assertNotNull(result);
        assertEquals("2022-01-01", result.getDate().toString());
        assertEquals("USD", result.getBaseCurrency());
        assertEquals("EUR", result.getTargetCurrency());
        assertNotNull(result.getDetail());
    }

    @Test
    public void mapOutDetail_returnsNull_whenAllParametersAreNull() {
        DetailASO result = ICRBean.mapOutDetail(null, null, null);
        assertNull(result);
    }

    @Test
    public void mapOutDetail_returnsDetail_whenParametersAreNotNull() {
        DetailASO result = ICRBean.mapOutDetail(BigDecimal.ONE, BigDecimal.ONE, "priceType");
        assertNotNull(result);
        assertEquals("priceType", result.getPriceType());
        assertNotNull(result.getFactor());
    }

    @Test
    public void mapOutFactor_returnsNull_whenAllParametersAreNull() {
        FactorASO result = ICRBean.mapOutFactor(null, null);
        assertNull(result);
    }

    @Test
    public void mapOutFactor_returnsFactor_whenParametersAreNotNull() {
        FactorASO result = ICRBean.mapOutFactor(BigDecimal.ONE, BigDecimal.ONE);
        assertNotNull(result);
        assertEquals(1.0, result.getValue(), 0.0);
        assertEquals(1.0, result.getRatio(), 0.0);
    }

    @Test
    public void mapOutPaymentAmount_returnsNull_whenAllParametersAreNull() {
        PaymentAmountASO result = ICRBean.mapOutPaymentAmount(null, null);
        assertNull(result);
    }

    @Test
    public void mapOutPaymentAmount_returnsPaymentAmount_whenParametersAreNotNull() {
        PaymentAmountASO result = ICRBean.mapOutPaymentAmount(BigDecimal.ONE, "USD");
        assertNotNull(result);
        assertEquals(1.0, result.getAmount(), 0.0);
        assertEquals("USD", result.getCurrency());
    }

    @Test
    public void mapOutInstallmentPlan_returnsNull_whenFormatoIsNull() {
        InstallmentPlanASO result = ICRBean.mapOutInstallmentPlan(new ICMRYS2());
        assertNull(result);
    }

    @Test
    public void mapOutInstallmentPlan_returnsInstallmentPlan_whenFormatoIsNotNull() {
        ICMRYS2 formato = mock(ICMRYS2.class);
        when(formato.getFECPAG()).thenReturn("2022-01-01");
        when(formato.getNUMCUO()).thenReturn(1);
        when(formato.getTFOPAG()).thenReturn("TFOPAG");
        when(formato.getDSCTPA()).thenReturn("DSCTPA");
        when(formato.getMTOCUO()).thenReturn(BigDecimal.ONE);
        when(formato.getDIVCUO()).thenReturn("USD");
        when(formato.getFECTPC()).thenReturn("2022-01-01");
        when(formato.getDIVTCM2()).thenReturn("EUR");
        when(formato.getMTOCAM()).thenReturn(BigDecimal.ONE);
        when(formato.getTIPCAM()).thenReturn(BigDecimal.ONE);
        when(formato.getCVCAMB()).thenReturn("CVCAMB");

        InstallmentPlanASO result = ICRBean.mapOutInstallmentPlan(formato);
        assertNotNull(result);
        assertEquals("2022-01-01", result.getStartDate().toString());
        assertEquals(1L, result.getTotalNumberInstallments().longValue());
        assertNotNull(result.getPeriod());
        assertNotNull(result.getPaymentAmount());
        assertNotNull(result.getExchangeRate());
    }

    @Test
    public void mapOutPaymentMethodRelatedContractsProduct_returnsNull_whenProductIdIsNull() {
        String productId = null;

        RelatedContractProductASO result = ICRBean.mapOutPaymentMethodRelatedContractsProduct(productId);

        assertNull(result);
    }

    @Test
    public void mapOutPaymentMethodRelatedContractsProduct_returnsProduct_whenProductIdIsNotNull() {
        String productId = "123";

        RelatedContractProductASO result = ICRBean.mapOutPaymentMethodRelatedContractsProduct(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
    }

    @Test
    public void mapOutProductPlan_returnsNull_whenIdAndDescriptionAreNull() {
        String id = null;
        String description = null;

        ProductPlanASO result = ICRBean.mapOutProductPlan(id, description);

        assertNull(result);
    }

    @Test
    public void mapOutProductPlan_returnsProductPlan_whenIdAndDescriptionAreNotNull() {
        String id = "123";
        String description = "Test Description";

        ProductPlanASO result = ICRBean.mapOutProductPlan(id, description);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(description, result.getDescription());
    }

    @Test
    public void mapInPaymentMethod_setsPaymentDetails_whenPaymentMethodIsNotNull() {
        ICContract format = new ICContract();
        PaymentMethodASO paymentMethod = mock(PaymentMethodASO.class);
        RelatedContractASO relatedContract = mock(RelatedContractASO.class);

        when(paymentMethod.getPaymentType()).thenReturn("DIRECT_DEBIT");
        when(paymentMethod.getInstallmentFrequency()).thenReturn("MONTHLY");
        when(paymentMethod.getRelatedContracts()).thenReturn(Collections.singletonList(relatedContract));
        when(relatedContract.getNumber()).thenReturn("123456");

        ICRBean.mapInPaymentMethod(format, paymentMethod);

        assertEquals("DIRECT_DEBIT", format.getMTDPGO());
        assertEquals("MONTHLY", format.getTFOPAG());
        assertEquals("123456", format.getNROCTA());
    }

    @Test
    public void mapInPaymentMethod_doesNotSetPaymentDetails_whenPaymentMethodIsNull() {
        ICContract format = new ICContract();

        ICRBean.mapInPaymentMethod(format, null);

        assertNull(format.getMTDPGO());
        assertNull(format.getTFOPAG());
        assertNull(format.getNROCTA());
    }

    @Test
    public void mapInPaymentMethod_doesNotSetRelatedContracts_whenRelatedContractsListIsEmpty() {
        ICContract format = new ICContract();
        PaymentMethodASO paymentMethod = mock(PaymentMethodASO.class);

        when(paymentMethod.getPaymentType()).thenReturn("DIRECT_DEBIT");
        when(paymentMethod.getInstallmentFrequency()).thenReturn("MONTHLY");
        when(paymentMethod.getRelatedContracts()).thenReturn(Collections.emptyList());

        ICRBean.mapInPaymentMethod(format, paymentMethod);

        assertEquals("DIRECT_DEBIT", format.getMTDPGO());
        assertEquals("MONTHLY", format.getTFOPAG());
        assertNull(format.getNROCTA());
    }

    @Test
    public void mapInBank_shouldSetBankDetails_whenBankIsNotNull() {
        ICContract format = new ICContract();
        BankASO bank = mock(BankASO.class);
        BranchASO branch = mock(BranchASO.class);

        when(bank.getId()).thenReturn("123");
        when(bank.getBranch()).thenReturn(branch);
        when(branch.getId()).thenReturn("456");

        ICRBean.mapInBank(format, bank);

        assertEquals("123", format.getCODBAN());
        assertEquals("456", format.getOFICON());
    }

    @Test
    public void mapInBank_shouldNotSetBankDetails_whenBankIsNull() {
        ICContract format = new ICContract();

        ICRBean.mapInBank(format, null);

        assertNull(format.getCODBAN());
        assertNull(format.getOFICON());
    }

    @Test
    public void mapInBank_shouldNotSetBranchId_whenBranchIsNull() {
        ICContract format = new ICContract();
        BankASO bank = mock(BankASO.class);

        when(bank.getId()).thenReturn("123");
        when(bank.getBranch()).thenReturn(null);

        ICRBean.mapInBank(format, bank);

        assertEquals("123", format.getCODBAN());
        assertNull(format.getOFICON());
    }


    @Test
    public void mapInParticipants_shouldSetPaymentManagerDetails_whenParticipantTypeIsPaymentManager() {
        ICContract format = new ICContract();
        ParticipantASO participant = new ParticipantASO();
        IdentityDocumentASO identityDocument = mock(IdentityDocumentASO.class);

        participant.setParticipantType(new ParticipantTypeASO());
        participant.getParticipantType().setId("PAYMENT_MANAGER");
        participant.setCustomerId("123");
        participant.setIdentityDocument(identityDocument);

        ICRBean.mapInParticipants(format, Arrays.asList(participant));

        assertEquals("123", format.getCODRSP());
    }

    @Test
    public void mapInParticipants_shouldSetLegalRepresentativeDetails_whenParticipantTypeIsLegalRepresentative() {
        ICContract format = new ICContract();
        ParticipantASO participant = new ParticipantASO();
        IdentityDocumentASO identityDocument = mock(IdentityDocumentASO.class);

        participant.setParticipantType(new ParticipantTypeASO());
        participant.getParticipantType().setId("LEGAL_REPRESENTATIVE");
        participant.setCustomerId("456");
        participant.setIdentityDocument(identityDocument);

        ICRBean.mapInParticipants(format, Arrays.asList(participant));

        assertEquals("456", format.getCODRPL());
    }

    @Test
    public void mapInParticipants_shouldNotSetAnyDetails_whenParticipantsListIsEmpty() {
        ICContract format = new ICContract();

        ICRBean.mapInParticipants(format, Collections.emptyList());

        assertNull(format.getCODRSP());
        assertNull(format.getCODRPL());
    }

    @Test
    public void mapInParticipants_shouldNotSetAnyDetails_whenParticipantTypeIsNull() {
        ICContract format = new ICContract();
        ParticipantASO participant = mock(ParticipantASO.class);

        when(participant.getParticipantType()).thenReturn(null);

        ICRBean.mapInParticipants(format, Arrays.asList(participant));

        assertNull(format.getCODRSP());
        assertNull(format.getCODRPL());
    }

    @Test
    public void mapInFullTest() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance();
        ICContract result = ICRBean.mapIn(input, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_COLLECT_ICR2);
        assertNotNull(result);

        // Verificar que todos los campos de result no sean nulos
        assertNotNull(result.getCODPRO());
        assertNotNull(result.getCODMOD());
        assertNotNull(result.getMTDPGO());
        assertNotNull(result.getTFOPAG());
        assertNotNull(result.getNROCTA());
        assertNotNull(result.getMEDPAG());
        assertNotNull(result.getFECINI());
        assertNotNull(result.getPRITOT());
        assertNotNull(result.getDIVPRI());
        assertNotNull(result.getSUMASE());
        assertNotNull(result.getDIVSUM());
        assertNotNull(result.getCODASE());
        assertNotNull(result.getTIPDOC());
        assertNotNull(result.getNUMASE());
        assertNotNull(result.getFECPAG());
        assertNotNull(result.getNUMCUO());
        assertNotNull(result.getMTOCUO());
        assertNotNull(result.getDIVCUO());
        assertNotNull(result.getCOBRO());
        assertNotNull(result.getCODRSP());
        assertNotNull(result.getTIPDO1());
        assertNotNull(result.getNUMRSP());
        assertNotNull(result.getGESTOR());
        assertNotNull(result.getPRESEN());
        assertNotNull(result.getCODBAN());
        assertNotNull(result.getOFICON());
        assertNotNull(result.getCODCIA());
        assertNotNull(result.getSUBCANA());

        // Verificar que los campos de result coincidan con los campos de input
        assertEquals(input.getProductId(), result.getCODPRO());
        assertEquals(input.getProductPlan().getId(), result.getCODMOD());
        assertEquals("DIRECT_DEBIT", result.getMTDPGO());
        assertEquals("MONTHLY", result.getTFOPAG());
        assertEquals(input.getPaymentMethod().getRelatedContracts().get(0).getNumber(), result.getNROCTA());
        assertEquals("CARD", result.getMEDPAG());
        assertEquals(input.getValidityPeriod().getStartDate().toString(), result.getFECINI());
        assertEquals(input.getTotalAmount().getAmount().toString(), result.getPRITOT().toString());
        assertEquals(input.getTotalAmount().getCurrency(), result.getDIVPRI());
        assertEquals(input.getInsuredAmount().getAmount().toString(), result.getSUMASE().toString());
        assertEquals(input.getInsuredAmount().getCurrency(), result.getDIVSUM());
        assertEquals(input.getHolder().getId(), result.getCODASE());
        assertEquals("DNI", result.getTIPDOC());
        assertEquals(input.getHolder().getIdentityDocument().getNumber(), result.getNUMASE());
        assertEquals(input.getInstallmentPlan().getStartDate().toString(), result.getFECPAG());
        assertEquals(input.getInstallmentPlan().getTotalNumberInstallments(), result.getNUMCUO());
        assertEquals("MONTHLY", result.getTFOPAG());
        assertEquals(input.getInstallmentPlan().getPaymentAmount().getAmount().toString(), result.getMTOCUO().toString());
        assertEquals(input.getInstallmentPlan().getPaymentAmount().getCurrency(), result.getDIVCUO());
        assertEquals("S", result.getCOBRO().getValue());
        assertEquals(input.getParticipants().get(0).getCustomerId(), result.getCODRSP());
        assertEquals("DNI", result.getTIPDO1());
        assertEquals(input.getParticipants().get(0).getIdentityDocument().getNumber(), result.getNUMRSP());
        assertEquals(input.getBusinessAgent().getId(), result.getGESTOR());
        assertEquals(input.getPromoter().getId(), result.getPRESEN());
        assertEquals(input.getBank().getId(), result.getCODBAN());
        assertEquals(input.getBank().getBranch().getId(), result.getOFICON());
        assertEquals(input.getInsuranceCompany().getId(), result.getCODCIA());
        assertEquals(input.getSalesSupplier().getId(), result.getSUBCANA());
    }

    @Test
    public void mapOutFullTest() throws IOException {
        ICMRYS2 input = EntityMock.getInstance().buildFormatoICMRYS2();
        DataASO result = ICRBean.mapOut(input);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getPolicyNumber());
        assertNotNull(result.getProductId());
        assertNotNull(result.getProductDescription());
        assertNotNull(result.getProductPlan());
        assertNotNull(result.getProductPlan().getId());
        assertNotNull(result.getProductPlan().getDescription());
        assertNotNull(result.getPaymentMethod());
        assertNotNull(result.getPaymentMethod().getPaymentType());
        assertNotNull(result.getPaymentMethod().getInstallmentFrequency());
        assertNotNull(result.getPaymentMethod().getRelatedContracts());
        assertEquals(1, result.getPaymentMethod().getRelatedContracts().size());
        assertNotNull(result.getPaymentMethod().getRelatedContracts().get(0).getNumber());
        assertNotNull(result.getPaymentMethod().getRelatedContracts().get(0).getProduct());
        assertNotNull(result.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId());
        assertNotNull(result.getOperationDate());
        assertNotNull(result.getValidityPeriod());
        assertNotNull(result.getValidityPeriod().getStartDate());
        assertNotNull(result.getValidityPeriod().getEndDate());
        assertNotNull(result.getTotalAmount());
        assertNotNull(result.getTotalAmount().getAmount());
        assertNotNull(result.getTotalAmount().getCurrency());
        assertNotNull(result.getTotalAmount().getExchangeRate());
        assertNotNull(result.getTotalAmount().getExchangeRate().getDate());
        assertNotNull(result.getTotalAmount().getExchangeRate().getBaseCurrency());
        assertNotNull(result.getTotalAmount().getExchangeRate().getTargetCurrency());
        assertNotNull(result.getTotalAmount().getExchangeRate().getDetail());
        assertNotNull(result.getTotalAmount().getExchangeRate().getDetail().getFactor());
        assertNotNull(result.getTotalAmount().getExchangeRate().getDetail().getFactor().getValue());
        assertNotNull(result.getTotalAmount().getExchangeRate().getDetail().getFactor().getRatio());
        assertNotNull(result.getTotalAmount().getExchangeRate().getDetail().getPriceType());
        assertNotNull(result.getInsuredAmount());
        assertNotNull(result.getInsuredAmount().getAmount());
        assertNotNull(result.getInsuredAmount().getCurrency());
        assertNotNull(result.getHolder());
        assertNotNull(result.getHolder().getId());
        assertNotNull(result.getHolder().getIdentityDocument());
        assertNotNull(result.getHolder().getIdentityDocument().getDocumentType());
        assertNotNull(result.getHolder().getIdentityDocument().getDocumentType().getId());
        assertNotNull(result.getHolder().getIdentityDocument().getNumber());
        assertNotNull(result.getRelatedContracts());
        assertEquals(1, result.getRelatedContracts().size());
        assertNotNull(result.getInstallmentPlan());
        assertNotNull(result.getInstallmentPlan().getStartDate());
        assertNotNull(result.getInstallmentPlan().getTotalNumberInstallments());
        assertNotNull(result.getInstallmentPlan().getPeriod());
        assertNotNull(result.getInstallmentPlan().getPeriod().getId());
        assertNotNull(result.getInstallmentPlan().getPeriod().getName());
        assertNotNull(result.getInstallmentPlan().getPaymentAmount());
        assertNotNull(result.getInstallmentPlan().getPaymentAmount().getAmount());
        assertNotNull(result.getInstallmentPlan().getPaymentAmount().getCurrency());
        assertNotNull(result.getInstallmentPlan().getExchangeRate());
        assertNotNull(result.getInstallmentPlan().getExchangeRate().getDate());
        assertNotNull(result.getInstallmentPlan().getExchangeRate().getBaseCurrency());
        assertNotNull(result.getInstallmentPlan().getExchangeRate().getTargetCurrency());
        assertNotNull(result.getInstallmentPlan().getExchangeRate().getDetail());
        assertNotNull(result.getInstallmentPlan().getExchangeRate().getDetail().getFactor());
        assertNotNull(result.getInstallmentPlan().getExchangeRate().getDetail().getFactor().getValue());
        assertNotNull(result.getInstallmentPlan().getExchangeRate().getDetail().getFactor().getRatio());
        assertNotNull(result.getInstallmentPlan().getExchangeRate().getDetail().getPriceType());
        assertNotNull(result.getFirstInstallment());
        assertNotNull(result.getFirstInstallment().getFirstPaymentDate());
        assertNotNull(result.getFirstInstallment().getIsPaymentRequired());
        assertNotNull(result.getFirstInstallment().getPaymentAmount());
        assertNotNull(result.getFirstInstallment().getPaymentAmount().getAmount());
        assertNotNull(result.getFirstInstallment().getPaymentAmount().getCurrency());
        assertNotNull(result.getFirstInstallment().getExchangeRate());
        assertNotNull(result.getFirstInstallment().getExchangeRate().getDate());
        assertNotNull(result.getFirstInstallment().getExchangeRate().getBaseCurrency());
        assertNotNull(result.getFirstInstallment().getExchangeRate().getTargetCurrency());
        assertNotNull(result.getFirstInstallment().getExchangeRate().getDetail());
        assertNotNull(result.getFirstInstallment().getExchangeRate().getDetail().getPriceType());
        assertNotNull(result.getFirstInstallment().getExchangeRate().getDetail().getFactor());
        assertNotNull(result.getFirstInstallment().getExchangeRate().getDetail().getFactor().getValue());
        assertNotNull(result.getFirstInstallment().getExchangeRate().getDetail().getFactor().getRatio());
        assertNotNull(result.getFirstInstallment().getOperationNumber());
        assertNotNull(result.getFirstInstallment().getTransactionNumber());
        assertNotNull(result.getFirstInstallment().getOperationDate());
        assertNotNull(result.getFirstInstallment().getAccountingDate());
        assertNotNull(result.getParticipants());
        assertEquals(1, result.getParticipants().size());
        assertNotNull(result.getParticipants().get(0).getId());
        assertNotNull(result.getParticipants().get(0).getIdentityDocument());
        assertNotNull(result.getParticipants().get(0).getIdentityDocument().getDocumentType());
        assertNotNull(result.getParticipants().get(0).getIdentityDocument().getDocumentType().getId());
        assertNotNull(result.getParticipants().get(0).getIdentityDocument().getNumber());
        assertNotNull(result.getParticipants().get(0).getCustomerId());
        assertNotNull(result.getBusinessAgent());
        assertNotNull(result.getBusinessAgent().getId());
        assertNotNull(result.getPromoter());
        assertNotNull(result.getPromoter().getId());
        assertNotNull(result.getInsuranceCompany());
        assertNotNull(result.getInsuranceCompany().getId());
        assertNotNull(result.getInsuranceCompany().getName());
        assertNotNull(result.getStatus());
        assertNotNull(result.getStatus().getId());
        assertNotNull(result.getStatus().getDescription());
        assertNotNull(result.getBank());
        assertNotNull(result.getBank().getId());
        assertNotNull(result.getBank().getBranch());
        assertNotNull(result.getBank().getBranch().getId());

        assertEquals(input.getNUMCON(), result.getId());
        assertEquals(input.getNUMPOL(), result.getPolicyNumber());
        assertEquals(input.getCODPRO(), result.getProductId());
        assertEquals(input.getNOMPRO(), result.getProductDescription());
        assertEquals(input.getCODMOD(), result.getProductPlan().getId());
        assertEquals(input.getNOMMOD(), result.getProductPlan().getDescription());
        assertEquals("DIRECT_DEBIT", result.getPaymentMethod().getPaymentType());
        assertEquals("M", result.getPaymentMethod().getInstallmentFrequency());
        assertEquals(input.getNROCTA(), result.getPaymentMethod().getRelatedContracts().get(0).getNumber());
        assertEquals("ACCOUNT", result.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId());
        assertEquals(input.getFECINI(), result.getValidityPeriod().getStartDate().toString());
        assertEquals(input.getFECFIN(), result.getValidityPeriod().getEndDate().toString());
        assertEquals(input.getPRITOT().toString(), result.getTotalAmount().getAmount().toString());
        assertEquals(input.getDIVPRI(), result.getTotalAmount().getCurrency());
        assertEquals(input.getDIVPRI(), result.getTotalAmount().getExchangeRate().getBaseCurrency());
        assertEquals(input.getDIVTCM(), result.getTotalAmount().getExchangeRate().getTargetCurrency());
        assertEquals(input.getMTOTCM().toString(), result.getTotalAmount().getExchangeRate().getDetail().getFactor().getValue().toString());
        assertEquals(input.getTCPRIB().toString(), result.getTotalAmount().getExchangeRate().getDetail().getFactor().getRatio().toString());
        assertEquals(input.getTCMBCV(), result.getTotalAmount().getExchangeRate().getDetail().getPriceType());
        assertEquals(input.getSUMASE().toString(), result.getInsuredAmount().getAmount().toString());
        assertEquals(input.getDIVSUM(), result.getInsuredAmount().getCurrency());
        assertEquals(input.getCODASE(), result.getHolder().getId());
        assertEquals("L", result.getHolder().getIdentityDocument().getDocumentType().getId());
        assertEquals(input.getNUMASE(), result.getHolder().getIdentityDocument().getNumber());
        assertEquals(input.getFECPAG(), result.getInstallmentPlan().getStartDate().toString());
        assertEquals(input.getNUMCUO().toString(), result.getInstallmentPlan().getTotalNumberInstallments().toString());
        assertEquals("M", result.getInstallmentPlan().getPeriod().getId());
        assertEquals(input.getDSCTPA(), result.getInstallmentPlan().getPeriod().getName());
        assertEquals(input.getMTOCUO().toString(), result.getInstallmentPlan().getPaymentAmount().getAmount().toString());
        assertEquals(input.getDIVCUO(), result.getInstallmentPlan().getPaymentAmount().getCurrency());
        assertEquals(input.getFECTPC(), result.getInstallmentPlan().getExchangeRate().getDate().toString());
        assertEquals(input.getMTOCAM().toString(), result.getInstallmentPlan().getExchangeRate().getDetail().getFactor().getValue().toString());
        assertEquals(input.getTIPCAM().toString(), result.getInstallmentPlan().getExchangeRate().getDetail().getFactor().getRatio().toString());
        assertEquals("C", result.getInstallmentPlan().getExchangeRate().getDetail().getPriceType());
        assertEquals(input.getFECPAG(), result.getFirstInstallment().getFirstPaymentDate().toString());
        assertEquals("true", result.getFirstInstallment().getIsPaymentRequired().toString());
        assertEquals(input.getMTOCUO().toString(), result.getFirstInstallment().getPaymentAmount().getAmount().toString());
        assertEquals(input.getDIVCUO(), result.getFirstInstallment().getPaymentAmount().getCurrency());
        assertEquals(input.getFECTPC(), result.getFirstInstallment().getExchangeRate().getDate().toString());
        assertEquals("C", result.getFirstInstallment().getExchangeRate().getDetail().getPriceType());
        assertEquals(input.getMTOCAM().toString(), result.getFirstInstallment().getExchangeRate().getDetail().getFactor().getValue().toString());
        assertEquals(input.getTIPCAM().toString(), result.getFirstInstallment().getExchangeRate().getDetail().getFactor().getRatio().toString());
        assertEquals(input.getIDNOPE(), result.getFirstInstallment().getOperationNumber());
        assertEquals(input.getNUMMOV(), result.getFirstInstallment().getTransactionNumber());
        assertEquals(input.getCODRSP(), result.getParticipants().get(0).getId());
        assertEquals("DNI", result.getParticipants().get(0).getIdentityDocument().getDocumentType().getId());
        assertEquals(input.getNUMRSP(), result.getParticipants().get(0).getIdentityDocument().getNumber());
        assertEquals(input.getCODRSP(), result.getParticipants().get(0).getCustomerId());
        assertEquals(input.getGESTOR(), result.getBusinessAgent().getId());
        assertEquals(input.getPRESEN(), result.getPromoter().getId());
        assertEquals(input.getCODCIA(), result.getInsuranceCompany().getId());
        assertEquals(input.getNOMCIA(), result.getInsuranceCompany().getName());
        assertEquals(input.getCSTCON(), result.getStatus().getId());
        assertEquals(input.getDSTCON(), result.getStatus().getDescription());
        assertEquals(input.getCODBAN(), result.getBank().getId());
        assertEquals(input.getOFICON(), result.getBank().getBranch().getId());

    }

    /*
    @Test
    public void mapInWhenParticipantsParticipantTypeId_Is_PAYMENT_MANAGER_Test() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance_supportArrayList();
        input.getParticipants().remove(1);
        ICR2Bean icr2Bean = new ICR2Bean();
        ICContract result = icr2Bean.mapIn(input);

        assertNotNull(result);
        // WHEN IS PAYMENT_MANAGER
        assertNotNull(result.getPartic());
        assertNotNull(result.getCodrsp());
        assertNotNull(result.getTipdo1());
        assertNotNull(result.getNumrsp());

        assertEquals(input.getParticipants().get(0).getParticipantType().getId() ,result.getPartic());
        assertEquals(input.getParticipants().get(0).getCustomerId() ,result.getCodrsp());
        assertEquals("L" ,result.getTipdo1());
        assertEquals(input.getParticipants().get(0).getIdentityDocument().getNumber() ,result.getNumrsp());

    }
    @Test
    public void mapInWhenParticipantsParticipantTypeId_Is_LEGAL_REPRESENTATIVE_Test() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance_supportArrayList();
        input.getParticipants().remove(0);
        ICR2Request result = mapper.mapIn(input);

        // WHEN IS LEGAL_REPRESENTATIVE
        assertNotNull(result.getPartic());
        assertNotNull(result.getCodrpl());
        assertNotNull(result.getTipdor());
        assertNotNull(result.getNumrpl());

        assertEquals(input.getParticipants().get(0).getParticipantType().getId() ,result.getPartic());
        assertEquals(input.getParticipants().get(0).getCustomerId() ,result.getCodrpl());
        assertEquals("R" ,result.getTipdor());
        assertEquals(input.getParticipants().get(0).getIdentityDocument().getNumber() ,result.getNumrpl());
    }

    @Test
    public void mapInFullTestLega() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsuranceLegal();
        ICR2Request result = mapper.mapIn(input);
        assertNotNull(result);
        assertNotNull(result.getPartic());
        assertNotNull(result.getCodrpl());
        assertNotNull(result.getTipdor());
        assertNotNull(result.getNumrpl());

        assertEquals(input.getParticipants().get(0).getParticipantType().getId(), Constants.LEGAL_REPRESENTATIVE);
        assertEquals(input.getParticipants().get(0).getCustomerId(), result.getCodrpl());
        assertEquals(input.getParticipants().get(0).getIdentityDocument().getNumber(), result.getNumrpl());
        assertEquals("L", result.getTipdor());

    }

    @Test
    public void mapInEmptyTest() {
        ICR2Request result = mapper.mapIn(new DataASO());

        assertNotNull(result);
        assertNull(result.getCodpro());
        assertNull(result.getCodmod());
        assertNull(result.getMtdpgo());
        assertNull(result.getTfopag());
        assertNull(result.getNrocta());
        assertNull(result.getMedpag());
        assertNull(result.getFecini());
        assertNull(result.getPritot());
        assertNull(result.getDivpri());
        assertNull(result.getSumase());
        assertNull(result.getDivsum());
        assertNull(result.getCodase());
        assertNull(result.getTipdoc());
        assertNull(result.getNumase());
        assertNull(result.getTconvin());
        assertNull(result.getConvin());
        assertNull(result.getFecpag());
        assertNull(result.getNumcuo());
        assertNull(result.getMtocuo());
        assertNull(result.getDivcuo());
        assertNull(result.getCobro());
        assertNull(result.getCodrsp());
        assertNull(result.getTipdo1());
        assertNull(result.getNumrsp());
        assertNull(result.getGestor());
        assertNull(result.getPresen());
        assertNull(result.getCodban());
        assertNull(result.getOficon());
        assertNull(result.getCodcia());
        assertNull(result.getSubcana());
    }

    @Test
    public void mapInWithoutParticipantsIdentityDocumentTest() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance();
        input.getParticipants().get(0).setIdentityDocument(null);
        ICR2Request result = mapper.mapIn(input);

        assertNotNull(result);
        assertNotNull(result.getCodpro());
        assertNotNull(result.getCodmod());
        assertNotNull(result.getMtdpgo());
        assertNotNull(result.getTfopag());
        assertNotNull(result.getNrocta());
        assertNotNull(result.getMedpag());
        assertNotNull(result.getFecini());
        assertNotNull(result.getPritot());
        assertNotNull(result.getDivpri());
        assertNotNull(result.getSumase());
        assertNotNull(result.getDivsum());
        assertNotNull(result.getCodase());
        assertNotNull(result.getTipdoc());
        assertNotNull(result.getNumase());
        assertNotNull(result.getTconvin());
        assertNotNull(result.getConvin());
        assertNotNull(result.getFecpag());
        assertNotNull(result.getNumcuo());
        assertNotNull(result.getMtocuo());
        assertNotNull(result.getDivcuo());
        assertNotNull(result.getCobro());
        assertNotNull(result.getCodrsp());
        assertNull(result.getTipdo1());
        assertNull(result.getNumrsp());
        assertNotNull(result.getGestor());
        assertNotNull(result.getPresen());
        assertNotNull(result.getCodban());
        assertNotNull(result.getOficon());
        assertNotNull(result.getCodcia());
    }

    @Test
    public void mapInWithoutInstallmentPlanPaymentAmountTest() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance();
        input.getInstallmentPlan().setPaymentAmount(null);
        ICR2Request result = mapper.mapIn(input);

        assertNotNull(result);
        assertNotNull(result.getCodpro());
        assertNotNull(result.getCodmod());
        assertNotNull(result.getMtdpgo());
        assertNotNull(result.getTfopag());
        assertNotNull(result.getNrocta());
        assertNotNull(result.getMedpag());
        assertNotNull(result.getFecini());
        assertNotNull(result.getPritot());
        assertNotNull(result.getDivpri());
        assertNotNull(result.getSumase());
        assertNotNull(result.getDivsum());
        assertNotNull(result.getCodase());
        assertNotNull(result.getTipdoc());
        assertNotNull(result.getNumase());
        assertNotNull(result.getTconvin());
        assertNotNull(result.getConvin());
        assertNotNull(result.getFecpag());
        assertNotNull(result.getNumcuo());
        assertNull(result.getMtocuo());
        assertNull(result.getDivcuo());
        assertNotNull(result.getCobro());
        assertNotNull(result.getCodrsp());
        assertNotNull(result.getTipdo1());
        assertNotNull(result.getNumrsp());
        assertNotNull(result.getGestor());
        assertNotNull(result.getPresen());
        assertNotNull(result.getCodban());
        assertNotNull(result.getOficon());
        assertNotNull(result.getCodcia());
    }

    @Test
    public void mapInWithoutInstallmentPlanPeriodAndPaymentMethodInstallmentFrecuencyTest() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance();
        input.getInstallmentPlan().setPeriod(null);
        input.getPaymentMethod().setInstallmentFrequency(null);
        ICR2Request result = mapper.mapIn(input);

        assertNotNull(result);
        assertNotNull(result.getCodpro());
        assertNotNull(result.getCodmod());
        assertNotNull(result.getMtdpgo());
        assertNull(result.getTfopag());
        assertNotNull(result.getNrocta());
        assertNotNull(result.getMedpag());
        assertNotNull(result.getFecini());
        assertNotNull(result.getPritot());
        assertNotNull(result.getDivpri());
        assertNotNull(result.getSumase());
        assertNotNull(result.getDivsum());
        assertNotNull(result.getCodase());
        assertNotNull(result.getTipdoc());
        assertNotNull(result.getNumase());
        assertNotNull(result.getTconvin());
        assertNotNull(result.getConvin());
        assertNotNull(result.getFecpag());
        assertNotNull(result.getNumcuo());
        assertNotNull(result.getMtocuo());
        assertNotNull(result.getDivcuo());
        assertNotNull(result.getCobro());
        assertNotNull(result.getCodrsp());
        assertNotNull(result.getTipdo1());
        assertNotNull(result.getNumrsp());
        assertNotNull(result.getGestor());
        assertNotNull(result.getPresen());
        assertNotNull(result.getCodban());
        assertNotNull(result.getOficon());
        assertNotNull(result.getCodcia());
    }

    @Test
    public void mapInHolderIdentityDocumentTest() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance();
        input.getHolder().setIdentityDocument(null);
        ICR2Request result = mapper.mapIn(input);

        assertNotNull(result);
        assertNotNull(result.getCodpro());
        assertNotNull(result.getCodmod());
        assertNotNull(result.getMtdpgo());
        assertNotNull(result.getTfopag());
        assertNotNull(result.getNrocta());
        assertNotNull(result.getMedpag());
        assertNotNull(result.getFecini());
        assertNotNull(result.getPritot());
        assertNotNull(result.getDivpri());
        assertNotNull(result.getSumase());
        assertNotNull(result.getDivsum());
        assertNotNull(result.getCodase());
        assertNull(result.getTipdoc());
        assertNull(result.getNumase());
        assertNotNull(result.getTconvin());
        assertNotNull(result.getConvin());
        assertNotNull(result.getFecpag());
        assertNotNull(result.getNumcuo());
        assertNotNull(result.getMtocuo());
        assertNotNull(result.getDivcuo());
        assertNotNull(result.getCobro());
        assertNotNull(result.getCodrsp());
        assertNotNull(result.getTipdo1());
        assertNotNull(result.getNumrsp());
        assertNotNull(result.getGestor());
        assertNotNull(result.getPresen());
        assertNotNull(result.getCodban());
        assertNotNull(result.getOficon());
        assertNotNull(result.getCodcia());
    }

    @Test
    public void mapInWithoutPaymentMethodRelatedContractsTest() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance();
        input.getPaymentMethod().setRelatedContracts(null);
        ICR2Request result = mapper.mapIn(input);

        assertNotNull(result);
        assertNotNull(result.getCodpro());
        assertNotNull(result.getCodmod());
        assertNotNull(result.getMtdpgo());
        assertNotNull(result.getTfopag());
        assertNull(result.getNrocta());
        assertNull(result.getMedpag());
        assertNotNull(result.getFecini());
        assertNotNull(result.getPritot());
        assertNotNull(result.getDivpri());
        assertNotNull(result.getSumase());
        assertNotNull(result.getDivsum());
        assertNotNull(result.getCodase());
        assertNotNull(result.getTipdoc());
        assertNotNull(result.getNumase());
        assertNotNull(result.getTconvin());
        assertNotNull(result.getConvin());
        assertNotNull(result.getFecpag());
        assertNotNull(result.getNumcuo());
        assertNotNull(result.getMtocuo());
        assertNotNull(result.getDivcuo());
        assertNotNull(result.getCobro());
        assertNotNull(result.getCodrsp());
        assertNotNull(result.getTipdo1());
        assertNotNull(result.getNumrsp());
        assertNotNull(result.getGestor());
        assertNotNull(result.getPresen());
        assertNotNull(result.getCodban());
        assertNotNull(result.getOficon());
        assertNotNull(result.getCodcia());
    }

    @Test
    public void mapInWithRelatedContractContractDetailsContractTypeExternalTest() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance();

        ICR2Request result = mapper.mapIn(input);

        assertNotNull(result);
        assertNotNull(result.getCodpro());
        assertNotNull(result.getCodmod());
        assertNotNull(result.getMtdpgo());
        assertNotNull(result.getTfopag());
        assertNotNull(result.getNrocta());
        assertNotNull(result.getMedpag());
        assertNotNull(result.getFecini());
        assertNotNull(result.getPritot());
        assertNotNull(result.getDivpri());
        assertNotNull(result.getSumase());
        assertNotNull(result.getDivsum());
        assertNotNull(result.getCodase());
        assertNotNull(result.getTipdoc());
        assertNotNull(result.getNumase());
        assertNotNull(result.getTconvin());
        assertNotNull(result.getConvin());
        assertNotNull(result.getFecpag());
        assertNotNull(result.getNumcuo());
        assertNotNull(result.getMtocuo());
        assertNotNull(result.getDivcuo());
        assertNotNull(result.getCobro());
        assertNotNull(result.getCodrsp());
        assertNotNull(result.getTipdo1());
        assertNotNull(result.getNumrsp());
        assertNotNull(result.getGestor());
        assertNotNull(result.getPresen());
        assertNotNull(result.getCodban());
        assertNotNull(result.getOficon());
        assertNotNull(result.getCodcia());

    }

    @Test
    public void mapInWithoutPaymentMethodRelatedContractProductTest() throws IOException {
        DataASO input = EntityMock.getInstance().buildInputCreateInsurance();
        input.getPaymentMethod().getRelatedContracts().get(0).setProduct(null);
        ICR2Request result = mapper.mapIn(input);

        assertNotNull(result);
        assertNotNull(result.getCodpro());
        assertNotNull(result.getCodmod());
        assertNotNull(result.getMtdpgo());
        assertNotNull(result.getTfopag());
        assertNotNull(result.getNrocta());
        assertNull(result.getMedpag());
        assertNotNull(result.getFecini());
        assertNotNull(result.getPritot());
        assertNotNull(result.getDivpri());
        assertNotNull(result.getSumase());
        assertNotNull(result.getDivsum());
        assertNotNull(result.getCodase());
        assertNotNull(result.getTipdoc());
        assertNotNull(result.getNumase());
        assertNotNull(result.getTconvin());
        assertNotNull(result.getConvin());
        assertNotNull(result.getFecpag());
        assertNotNull(result.getNumcuo());
        assertNotNull(result.getMtocuo());
        assertNotNull(result.getDivcuo());
        assertNotNull(result.getCobro());
        assertNotNull(result.getCodrsp());
        assertNotNull(result.getTipdo1());
        assertNotNull(result.getNumrsp());
        assertNotNull(result.getGestor());
        assertNotNull(result.getPresen());
        assertNotNull(result.getCodban());
        assertNotNull(result.getOficon());
        assertNotNull(result.getCodcia());
    }

     */

}