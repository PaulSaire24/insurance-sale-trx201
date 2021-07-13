package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractProductASO;
import com.bbva.rbvd.dto.insrncsale.aso.HolderASO;
import com.bbva.rbvd.dto.insrncsale.aso.IdentityDocumentASO;
import com.bbva.rbvd.dto.insrncsale.aso.DocumentTypeASO;
import com.bbva.rbvd.dto.insrncsale.aso.PaymentAmountASO;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.ProductPlanASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PaymentMethodASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.ValidityPeriodASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.TotalAmountASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.InsuredAmountASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.InstallmentPlanASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PaymentPeriodASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.FirstInstallmentASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.ParticipantASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.ParticipantTypeASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BusinessAgentASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PromoterASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BankASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BranchASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.InsuranceCompanyASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PayloadEmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.ContactoInspeccionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.DatoParticularBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.CuotaFinancimientoBO;

import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.commons.PolicyInspectionDTO;

import com.bbva.rbvd.dto.insrncsale.dao.*;

import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;

import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class MapperHelper {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static final String EMAIL_VALUE = "EMAIL";
    private static final String PHONE_NUMBER_VALUE = "PHONE";
    private static final String PARTICULAR_DATA_THIRD_CHANNEL = "CANAL_TERCERO";
    private static final String PARTICULAR_DATA_ACCOUNT_DATA = "DATOS_DE_CUENTA";
    private static final String PARTICULAR_DATA_CERT_BANCO = "NRO_CERT_BANCO";
    private static final String S_VALUE = "S";
    private static final String N_VALUE = "N";
    private static final Long INDICATOR_INSPECTION_VALUE = 1L;
    private static final String PAYMENT_METHOD_VALUE = "DIRECT_DEBIT";
    private static final String COLLECTION_STATUS_FIRST_RECEIPT_VALUE = "00";
    private static final String COLLECTION_STATUS_NEXT_VALUES = "02";
    private static final String CARD_PRODUCT_ID = "CARD";
    private static final String CARD_METHOD_TYPE = "T";
    private static final String ACCOUNT_METHOD_TYPE = "C";
    private static final String FIRST_RECEIPT_STATUS_TYPE_VALUE = "COB";
    private static final String NEXT_RECEIPTS_STATUS_TYPE_VALUE = "INC";
    private static final String NEXT_RECEIPTS_START_DATE_VALUE = "01/01/2021";

    private final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
    private final String currentDate = format.format(new Date());

    private ApplicationConfigurationService applicationConfigurationService;

    public DataASO buildAsoRequest(PolicyDTO apxRequest) {
        DataASO requestAso = new DataASO();

        requestAso.setQuotationId(apxRequest.getQuotationId());
        requestAso.setProductId(apxRequest.getProductId());

        ProductPlanASO productPlan = new ProductPlanASO();
        productPlan.setId(apxRequest.getProductPlan().getId());
        requestAso.setProductPlan(productPlan);

        PaymentMethodASO paymentMethod = new PaymentMethodASO();

        RelatedContractASO paymentRelatedContract = new RelatedContractASO();

        RelatedContractProductASO product = new RelatedContractProductASO();
        product.setId(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId());
        paymentRelatedContract.setProduct(product);
        paymentRelatedContract.setNumber(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getNumber());

        paymentMethod.setRelatedContracts(Collections.singletonList(paymentRelatedContract));
        paymentMethod.setPaymentType(apxRequest.getPaymentMethod().getPaymentType());
        paymentMethod.setInstallmentFrequency(apxRequest.getPaymentMethod().getInstallmentFrequency());
        requestAso.setPaymentMethod(paymentMethod);

        ValidityPeriodASO validityPeriod = new ValidityPeriodASO();
        validityPeriod.setStartDate(apxRequest.getValidityPeriod().getStartDate());

        requestAso.setValidityPeriod(validityPeriod);

        TotalAmountASO totalAmount = new TotalAmountASO();
        totalAmount.setAmount(apxRequest.getTotalAmount().getAmount());
        totalAmount.setCurrency(apxRequest.getTotalAmount().getCurrency());

        requestAso.setTotalAmount(totalAmount);

        InsuredAmountASO insuredAmount = new InsuredAmountASO();
        insuredAmount.setAmount(apxRequest.getInsuredAmount().getAmount());
        insuredAmount.setCurrency(apxRequest.getInsuredAmount().getCurrency());

        requestAso.setInsuredAmount(insuredAmount);

        HolderASO holder = new HolderASO();

        IdentityDocumentASO identityDocument = new IdentityDocumentASO();
        DocumentTypeASO documentType = new DocumentTypeASO();
        documentType.setId(apxRequest.getHolder().getIdentityDocument().getDocumentType().getId());
        identityDocument.setDocumentType(documentType);
        identityDocument.setNumber(apxRequest.getHolder().getIdentityDocument().getNumber());

        holder.setIdentityDocument(identityDocument);
        holder.setId(apxRequest.getHolder().getId());

        requestAso.setHolder(holder);

        InstallmentPlanASO installmentPlan = new InstallmentPlanASO();
        installmentPlan.setStartDate(apxRequest.getInstallmentPlan().getStartDate());
        installmentPlan.setMaturityDate(apxRequest.getInstallmentPlan().getMaturityDate());
        installmentPlan.setTotalNumberInstallments(apxRequest.getInstallmentPlan().getTotalNumberInstallments());

        PaymentPeriodASO period = new PaymentPeriodASO();
        period.setId(apxRequest.getInstallmentPlan().getPeriod().getId());

        installmentPlan.setPeriod(period);

        PaymentAmountASO paymentAmount = new PaymentAmountASO();
        paymentAmount.setAmount(apxRequest.getInstallmentPlan().getPaymentAmount().getAmount());
        paymentAmount.setCurrency(apxRequest.getInstallmentPlan().getPaymentAmount().getCurrency());

        installmentPlan.setPaymentAmount(paymentAmount);

        requestAso.setInstallmentPlan(installmentPlan);

        FirstInstallmentASO firstInstallment = new FirstInstallmentASO();
        firstInstallment.setIsPaymentRequired(apxRequest.getFirstInstallment().getIsPaymentRequired());

        requestAso.setFirstInstallment(firstInstallment);

        ParticipantASO participant = new ParticipantASO();

        ParticipantTypeASO participantType = new ParticipantTypeASO();
        participantType.setId(apxRequest.getParticipants().get(0).getParticipantType().getId());

        participant.setParticipantType(participantType);
        participant.setCustomerId(apxRequest.getParticipants().get(0).getCustomerId());

        IdentityDocumentASO participantIdentityDocument = new IdentityDocumentASO();

        DocumentTypeASO participantDocumentType = new DocumentTypeASO();
        participantDocumentType.setId(apxRequest.getParticipants().get(0).getIdentityDocument().getDocumentType().getId());
        participantIdentityDocument.setDocumentType(participantDocumentType);
        participantIdentityDocument.setNumber(apxRequest.getParticipants().get(0).getIdentityDocument().getNumber());

        participant.setIdentityDocument(participantIdentityDocument);

        requestAso.setParticipants(Collections.singletonList(participant));

        BusinessAgentASO businessAgent = new BusinessAgentASO();
        businessAgent.setId(apxRequest.getBusinessAgent().getId());

        requestAso.setBusinessAgent(businessAgent);

        PromoterASO promoter = new PromoterASO();
        promoter.setId(apxRequest.getPromoter().getId());

        requestAso.setPromoter(promoter);

        BankASO bank = new BankASO();

        BranchASO branch = new BranchASO();
        branch.setId(apxRequest.getBank().getBranch().getId());
        bank.setBranch(branch);
        bank.setId(apxRequest.getBank().getId());

        requestAso.setBank(bank);

        InsuranceCompanyASO insuranceCompany = new InsuranceCompanyASO();
        insuranceCompany.setId(apxRequest.getInsuranceCompany().getId());

        requestAso.setInsuranceCompany(insuranceCompany);

        return requestAso;
    }

    public EmisionBO buildRequestBodyRimac(PolicyInspectionDTO inspection, String secondParticularDataValue, String channelCode, String dataId) {
        EmisionBO rimacRequest = new EmisionBO();

        PayloadEmisionBO payload = new PayloadEmisionBO();

        ContactoInspeccionBO contactoInspeccion = new ContactoInspeccionBO();
        contactoInspeccion.setNombre(inspection.getFullName());

        ContactDetailDTO contactEmail = inspection.getContactDetails().stream().
                filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(EMAIL_VALUE)).findFirst().orElse(null);

        ContactDetailDTO contactPhone = inspection.getContactDetails().stream().
                filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(PHONE_NUMBER_VALUE)).findFirst().orElse(null);

        contactoInspeccion.setCorreo( Objects.nonNull(contactEmail) ? contactEmail.getContact().getAddress() : null);
        contactoInspeccion.setTelefono( Objects.nonNull(contactPhone) ? contactPhone.getContact().getPhoneNumber() : null);

        payload.setContactoInspeccion(contactoInspeccion);

        List<DatoParticularBO> datosParticulares = new ArrayList<>();

        DatoParticularBO primerDatoParticular = new DatoParticularBO();
        primerDatoParticular.setEtiqueta(PARTICULAR_DATA_THIRD_CHANNEL);
        primerDatoParticular.setCodigo("");
        primerDatoParticular.setValor(channelCode);
        datosParticulares.add(primerDatoParticular);

        DatoParticularBO segundoDatoParticular = new DatoParticularBO();
        segundoDatoParticular.setEtiqueta(PARTICULAR_DATA_ACCOUNT_DATA);
        segundoDatoParticular.setCodigo("");
        segundoDatoParticular.setValor(secondParticularDataValue);
        datosParticulares.add(segundoDatoParticular);

        DatoParticularBO tercerDatoParticular = new DatoParticularBO();
        tercerDatoParticular.setEtiqueta(PARTICULAR_DATA_CERT_BANCO);
        tercerDatoParticular.setCodigo("");
        tercerDatoParticular.setValor(dataId);
        datosParticulares.add(tercerDatoParticular);

        payload.setDatosParticulares(datosParticulares);
        payload.setEnvioElectronico(S_VALUE);
        payload.setIndCobro(N_VALUE);
        payload.setIndInspeccion(INDICATOR_INSPECTION_VALUE);
        payload.setIndValidaciones(N_VALUE);

        rimacRequest.setPayload(payload);
        return rimacRequest;
    }

    public InsuranceContractDAO buildInsuranceContract(EmisionBO rimacResponse, PolicyDTO apxRequest, RequiredFieldsEmissionDAO emissionDao, String asoId) {
        InsuranceContractDAO contractDao = new InsuranceContractDAO();

        contractDao.setEntityId(asoId.substring(0, 4));
        contractDao.setBranchId(asoId.substring(4, 8));
        contractDao.setIntAccountId(asoId.substring(10));
        contractDao.setFirstVerfnDigitId(asoId.substring(8,9));
        contractDao.setSecondVerfnDigitId(asoId.substring(9,10));
        contractDao.setPolicyQuotaInternalId(apxRequest.getQuotationId());
        contractDao.setInsuranceProductId(emissionDao.getInsuranceProductId());
        contractDao.setInsuranceModalityType(apxRequest.getProductPlan().getId());
        contractDao.setInsuranceCompanyId(new BigDecimal(apxRequest.getInsuranceCompany().getId()));
        if(Objects.nonNull(rimacResponse)) {
            contractDao.setPolicyId(rimacResponse.getPayload().getNumeroPoliza());
            contractDao.setInsuranceContractEndDate(rimacResponse.getPayload().getFechaFinal());

            int numeroCuotas = rimacResponse.getPayload().getCuotasFinanciamiento().size();
            CuotaFinancimientoBO ultimaCuota = rimacResponse.getPayload().getCuotasFinanciamiento().
                    stream().filter(cuota -> cuota.getCuota() == numeroCuotas).findFirst().orElse(null);

            if(Objects.nonNull(ultimaCuota)) {
                contractDao.setLastInstallmentDate(ultimaCuota.getFechaVencimiento());
                contractDao.setPeriodNextPaymentDate((numeroCuotas == 1) ?
                        ultimaCuota.getFechaVencimiento() : getNextPaymentDate(rimacResponse));
            }

            contractDao.setInsuranceCompanyProductId(rimacResponse.getPayload().getCodProducto());
        } else {
            contractDao.setInsuranceContractEndDate(currentDate);
            contractDao.setLastInstallmentDate(currentDate);
            contractDao.setPeriodNextPaymentDate(currentDate);
        }
        contractDao.setInsuranceManagerId(apxRequest.getBusinessAgent().getId());
        contractDao.setInsurancePromoterId(apxRequest.getPromoter().getId());
        contractDao.setContractManagerBranchId(asoId.substring(4, 8));
        contractDao.setContractInceptionDate(currentDate);
        contractDao.setInsuranceContractStartDate(format.format(apxRequest.getValidityPeriod().getStartDate()));

        contractDao.setValidityMonthsNumber(emissionDao.getContractDurationNumber());

        contractDao.setCustomerId(apxRequest.getHolder().getId());
        contractDao.setDomicileContractId(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getContractId());
        contractDao.setIssuedReceiptNumber(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()));

        contractDao.setPaymentFrequencyId(emissionDao.getPaymentFrequencyId());

        contractDao.setPremiumAmount(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()));
        contractDao.setSettlePendingPremiumAmount(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getPaymentAmount().getAmount()));
        contractDao.setCurrencyId(apxRequest.getInstallmentPlan().getPaymentAmount().getCurrency());
        contractDao.setInstallmentPeriodFinalDate(currentDate);
        contractDao.setInsuredAmount(BigDecimal.valueOf(apxRequest.getInsuredAmount().getAmount()));
        contractDao.setContractPreviousBranchId(asoId.substring(4, 8));
        contractDao.setCreationUserId(apxRequest.getCreationUser());
        contractDao.setUserAuditId(apxRequest.getUserAudit());

        contractDao.setTotalDebtAmount((apxRequest.getFirstInstallment().getIsPaymentRequired())
                ? BigDecimal.valueOf(0) : BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()));

        contractDao.setPrevPendBillRcptsNumber((apxRequest.getFirstInstallment().getIsPaymentRequired())
                ? BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments() - 1)
                : BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()));

        contractDao.setSettlementFixPremiumAmount(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getPaymentAmount().getAmount()));
        contractDao.setAutomaticDebitIndicatorType((apxRequest.getPaymentMethod().getPaymentType().equals(PAYMENT_METHOD_VALUE))
                ? S_VALUE : N_VALUE);
        contractDao.setBiometryTransactionId(apxRequest.getIdentityVerificationCode());
        return contractDao;
    }

    private String getNextPaymentDate(EmisionBO rimacResponse) {
        String nextPaymentDate = null;
        CuotaFinancimientoBO segundaCuota = rimacResponse.getPayload().getCuotasFinanciamiento().
                stream().filter(cuota -> cuota.getCuota() == 2).findFirst().orElse(null);
        if(Objects.nonNull(segundaCuota)) {
            nextPaymentDate = segundaCuota.getFechaVencimiento();
        }
        return nextPaymentDate;
    }

    public Map<String, Object> createSaveContractArguments(InsuranceContractDAO contractDao) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), contractDao.getEntityId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(), contractDao.getBranchId());
        arguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), contractDao.getIntAccountId());
        arguments.put(RBVDProperties.FIELD_CONTRACT_FIRST_VERFN_DIGIT_ID.getValue(), contractDao.getFirstVerfnDigitId());
        arguments.put(RBVDProperties.FIELD_CONTRACT_SECOND_VERFN_DIGIT_ID.getValue(), contractDao.getSecondVerfnDigitId());
        arguments.put(RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue(), contractDao.getPolicyQuotaInternalId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(), contractDao.getInsuranceProductId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(), contractDao.getInsuranceModalityType());
        arguments.put(RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue(), contractDao.getInsuranceCompanyId());
        arguments.put(RBVDProperties.FIELD_POLICY_ID.getValue(), contractDao.getPolicyId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_MANAGER_ID.getValue(), contractDao.getInsuranceManagerId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_PROMOTER_ID.getValue(), contractDao.getInsurancePromoterId());
        arguments.put(RBVDProperties.FIELD_CONTRACT_MANAGER_BRANCH_ID.getValue(), contractDao.getContractManagerBranchId());
        arguments.put(RBVDProperties.FIELD_CONTRACT_INCEPTION_DATE.getValue(), contractDao.getContractInceptionDate());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue(), contractDao.getInsuranceContractStartDate());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue(), contractDao.getInsuranceContractEndDate());
        arguments.put(RBVDProperties.FIELD_INSRNC_VALIDITY_MONTHS_NUMBER.getValue(), contractDao.getValidityMonthsNumber());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ID.getValue(), contractDao.getCustomerId());
        arguments.put(RBVDProperties.FIELD_DOMICILE_CONTRACT_ID.getValue(), contractDao.getDomicileContractId());
        arguments.put(RBVDProperties.FIELD_CARD_ISSUING_MARK_TYPE.getValue(), contractDao.getCardIssuingMarkType());
        arguments.put(RBVDProperties.FIELD_ISSUED_RECEIPT_NUMBER.getValue(), contractDao.getIssuedReceiptNumber());
        arguments.put(RBVDProperties.FIELD_PAYMENT_FREQUENCY_ID.getValue(), contractDao.getPaymentFrequencyId());
        arguments.put(RBVDProperties.FIELD_PREMIUM_AMOUNT.getValue(), contractDao.getPremiumAmount());
        arguments.put(RBVDProperties.FIELD_SETTLE_PENDING_PREMIUM_AMOUNT.getValue(), contractDao.getSettlePendingPremiumAmount());
        arguments.put(RBVDProperties.FIELD_CURRENCY_ID.getValue(), contractDao.getCurrencyId());
        arguments.put(RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue(), contractDao.getLastInstallmentDate());
        arguments.put(RBVDProperties.FIELD_INSTALLMENT_PERIOD_FINAL_DATE.getValue(), contractDao.getInstallmentPeriodFinalDate());
        arguments.put(RBVDProperties.FIELD_INSURED_AMOUNT.getValue(), contractDao.getInsuredAmount());
        arguments.put(RBVDProperties.FIELD_BENEFICIARY_TYPE.getValue(), contractDao.getBeneficiaryType());
        arguments.put(RBVDProperties.FIELD_RENEWAL_NUMBER.getValue(), contractDao.getRenewalNumber());
        arguments.put(RBVDProperties.FIELD_CTRCT_DISPUTE_STATUS_TYPE.getValue(), contractDao.getCtrctDisputeStatusType());
        arguments.put(RBVDProperties.FIELD_CONTRACT_PREVIOUS_BRANCH_ID.getValue(), contractDao.getContractPreviousBranchId());
        arguments.put(RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue(), contractDao.getPeriodNextPaymentDate());
        arguments.put(RBVDProperties.FIELD_ENDORSEMENT_POLICY_IND_TYPE.getValue(), contractDao.getEndorsementPolicyIndType());
        arguments.put(RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue(), contractDao.getInsrncCoContractStatusType());
        arguments.put(RBVDProperties.FIELD_CONTRACT_STATUS_ID.getValue(), contractDao.getContractStatusId());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), contractDao.getCreationUserId());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), contractDao.getUserAuditId());
        arguments.put(RBVDProperties.FIELD_INSUR_PENDING_DEBT_IND_TYPE.getValue(), contractDao.getInsurPendingDebtIndType());
        arguments.put(RBVDProperties.FIELD_TOTAL_DEBT_AMOUNT.getValue(), contractDao.getTotalDebtAmount());
        arguments.put(RBVDProperties.FIELD_PREV_PEND_BILL_RCPTS_NUMBER.getValue(), contractDao.getPrevPendBillRcptsNumber());
        arguments.put(RBVDProperties.FIELD_SETTLEMENT_VAR_PREMIUM_AMOUNT.getValue(), contractDao.getSettlementVarPremiumAmount());
        arguments.put(RBVDProperties.FIELD_SETTLEMENT_FIX_PREMIUM_AMOUNT.getValue(), contractDao.getSettlementFixPremiumAmount());
        arguments.put(RBVDProperties.FIELD_INSURANCE_COMPANY_PRODUCT_ID.getValue(), contractDao.getInsuranceCompanyProductId());
        arguments.put(RBVDProperties.FIELD_AUTOMATIC_DEBIT_INDICATOR_TYPE.getValue(), contractDao.getAutomaticDebitIndicatorType());
        arguments.put(RBVDProperties.FIELD_BIOMETRY_TRANSACTION_ID.getValue(), contractDao.getBiometryTransactionId());
        arguments.put(RBVDProperties.FIELD_TELEMARKETING_TRANSACTION_ID.getValue(), contractDao.getTelemarketingTransactionId());
        return arguments;
    }

    public List<InsuranceCtrReceiptsDAO> buildInsuranceCtrReceipt(PolicyASO asoResponse, EmisionBO rimacResponse, PolicyDTO requestBody) {
        List<InsuranceCtrReceiptsDAO> receiptList = new ArrayList<>();

        InsuranceCtrReceiptsDAO firstReceipt = new InsuranceCtrReceiptsDAO();
        firstReceipt.setEntityId(asoResponse.getData().getId().substring(0, 4));
        firstReceipt.setBranchId(asoResponse.getData().getId().substring(4, 8));
        firstReceipt.setIntAccountId(asoResponse.getData().getId().substring(10));
        firstReceipt.setInsuranceCompanyId(new BigDecimal(asoResponse.getData().getId().substring(9,10)));
        firstReceipt.setPremiumPaymentReceiptAmount(BigDecimal.valueOf(requestBody.getFirstInstallment().getPaymentAmount().getAmount()));

        if(Objects.nonNull(asoResponse.getData().getFirstInstallment().getExchangeRate())) {
            firstReceipt.setFixingExchangeRateAmount(BigDecimal.valueOf(asoResponse.getData()
                    .getFirstInstallment().getExchangeRate().getDetail().getFactor().getRatio()));
            firstReceipt.setPremiumCurrencyExchAmount(BigDecimal.valueOf(asoResponse.getData()
                    .getFirstInstallment().getExchangeRate().getDetail().getFactor().getValue()));
        } else {
            firstReceipt.setFixingExchangeRateAmount(BigDecimal.valueOf(0));
            firstReceipt.setPremiumCurrencyExchAmount(BigDecimal.valueOf(0));
        }

        if(asoResponse.getData().getFirstInstallment().getOperationNumber().length() >= 11) {
            firstReceipt.setPremiumChargeOperationId(asoResponse.getData().getFirstInstallment().getOperationNumber().substring(1));
        }
        firstReceipt.setCurrencyId(requestBody.getFirstInstallment().getPaymentAmount().getCurrency());
        firstReceipt.setReceiptStartDate(format.format(requestBody.getValidityPeriod().getStartDate()));

        if(Objects.nonNull(asoResponse.getData().getFirstInstallment().getOperationDate())) {
            firstReceipt.setReceiptIssueDate(format.format(asoResponse.getData().getFirstInstallment().getOperationDate()));
            firstReceipt.setReceiptCollectionDate(format.format(asoResponse.getData().getFirstInstallment().getOperationDate()));
            firstReceipt.setReceiptsTransmissionDate(format.format(asoResponse.getData().getFirstInstallment().getOperationDate()));
        } else {
            firstReceipt.setReceiptIssueDate(currentDate);
            firstReceipt.setReceiptCollectionDate(currentDate);
            firstReceipt.setReceiptsTransmissionDate(currentDate);
        }

        firstReceipt.setReceiptCollectionStatusType(COLLECTION_STATUS_FIRST_RECEIPT_VALUE);
        firstReceipt.setInsuranceCollectionMoveId(asoResponse.getData().getFirstInstallment().getTransactionNumber());
        firstReceipt.setPaymentMethodType(requestBody.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId().
                equals(CARD_PRODUCT_ID) ? CARD_METHOD_TYPE : ACCOUNT_METHOD_TYPE);
        firstReceipt.setDebitAccountId(requestBody.getPaymentMethod().getRelatedContracts().get(0).getContractId());
        firstReceipt.setDebitChannelType(requestBody.getSaleChannelId());
        firstReceipt.setReceiptStatusType(FIRST_RECEIPT_STATUS_TYPE_VALUE);
        firstReceipt.setCreationUserId(requestBody.getCreationUser());
        firstReceipt.setUserAuditId(requestBody.getUserAudit());
        firstReceipt.setManagementBranchId(asoResponse.getData().getId().substring(4, 8));
        firstReceipt.setFixPremiumAmount(BigDecimal.valueOf(requestBody.getFirstInstallment().getPaymentAmount().getAmount()));
        firstReceipt.setSettlementFixPremiumAmount(BigDecimal.valueOf(requestBody.getInstallmentPlan().getPaymentAmount().getAmount()));
        firstReceipt.setLastChangeBranchId(requestBody.getBank().getBranch().getId());
        firstReceipt.setGlBranchId(asoResponse.getData().getId().substring(4, 8));

        CuotaFinancimientoBO primeraCuota = rimacResponse.getPayload().getCuotasFinanciamiento().stream().
                filter(cuota -> cuota.getCuota() == 1).findFirst().orElse(null);
        if(Objects.nonNull(primeraCuota)) {
            firstReceipt.setPolicyReceiptId(BigDecimal.valueOf(primeraCuota.getCuota()));
            firstReceipt.setReceiptEndDate(primeraCuota.getFechaVencimiento());
            firstReceipt.setReceiptExpirationDate(primeraCuota.getFechaVencimiento());
        }

        receiptList.add(firstReceipt);

        List<CuotaFinancimientoBO> siguientesCuotas = rimacResponse.getPayload().getCuotasFinanciamiento().stream().
                filter(cuota -> cuota.getCuota() != 1).collect(Collectors.toList());

        if(siguientesCuotas.size() >= 1) {
            siguientesCuotas.forEach(cuota -> receiptList.add(createNextReceipt(firstReceipt, cuota)));
        }

        return receiptList;
    }

    private InsuranceCtrReceiptsDAO createNextReceipt(InsuranceCtrReceiptsDAO firstReceipt, CuotaFinancimientoBO cuota) {
        InsuranceCtrReceiptsDAO nextReceipt = new InsuranceCtrReceiptsDAO();

        String dueDate = cuota.getFechaVencimiento();

        nextReceipt.setEntityId(firstReceipt.getEntityId());
        nextReceipt.setBranchId(firstReceipt.getBranchId());
        nextReceipt.setIntAccountId(firstReceipt.getIntAccountId());
        nextReceipt.setPolicyReceiptId(BigDecimal.valueOf(cuota.getCuota()));
        nextReceipt.setPremiumPaymentReceiptAmount(BigDecimal.valueOf(0));
        nextReceipt.setFixingExchangeRateAmount(BigDecimal.valueOf(0));
        nextReceipt.setPremiumCurrencyExchAmount(BigDecimal.valueOf(0));
        nextReceipt.setReceiptIssueDate(currentDate);
        nextReceipt.setReceiptStartDate(NEXT_RECEIPTS_START_DATE_VALUE);
        nextReceipt.setReceiptEndDate(dueDate);
        nextReceipt.setReceiptCollectionDate(currentDate);
        nextReceipt.setReceiptExpirationDate(dueDate);
        nextReceipt.setReceiptsTransmissionDate(currentDate);
        nextReceipt.setReceiptCollectionStatusType(COLLECTION_STATUS_NEXT_VALUES);
        nextReceipt.setPaymentMethodType(firstReceipt.getPaymentMethodType());
        nextReceipt.setDebitAccountId(firstReceipt.getDebitAccountId());
        nextReceipt.setReceiptStatusType(NEXT_RECEIPTS_STATUS_TYPE_VALUE);
        nextReceipt.setCreationUserId(firstReceipt.getCreationUserId());
        nextReceipt.setUserAuditId(firstReceipt.getUserAuditId());
        nextReceipt.setManagementBranchId(firstReceipt.getManagementBranchId());
        nextReceipt.setFixPremiumAmount(firstReceipt.getFixPremiumAmount());
        nextReceipt.setSettlementFixPremiumAmount(firstReceipt.getSettlementFixPremiumAmount());
        nextReceipt.setGlBranchId(firstReceipt.getGlBranchId());

        return nextReceipt;
    }

    public Map<String, Object>[] createSaveReceiptsArguments(List<InsuranceCtrReceiptsDAO> receipts) {
        Map<String, Object>[] receiptsArguments = new HashMap[receipts.size()];
        for(int i = 0; i < receipts.size(); i++) {
            receiptsArguments[i] = createReceipt(receipts.get(i));
        }
        return receiptsArguments;
    }

    private Map<String, Object> createReceipt(InsuranceCtrReceiptsDAO receiptDao) {
        Map<String, Object> receiptArguments = new HashMap<>();

        receiptArguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), receiptDao.getEntityId());
        receiptArguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(), receiptDao.getBranchId());
        receiptArguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), receiptDao.getIntAccountId());
        receiptArguments.put(RBVDProperties.FIELD_POLICY_RECEIPT_ID.getValue(), receiptDao.getPolicyReceiptId());
        receiptArguments.put(RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue(), receiptDao.getInsuranceCompanyId());
        receiptArguments.put(RBVDProperties.FIELD_PREMIUM_PAYMENT_RECEIPT_AMOUNT.getValue(), receiptDao.getPremiumPaymentReceiptAmount());
        receiptArguments.put(RBVDProperties.FIELD_FIXING_EXCHANGE_RATE_AMOUNT.getValue(), receiptDao.getFixingExchangeRateAmount());
        receiptArguments.put(RBVDProperties.FIELD_PREMIUM_CURRENCY_EXCH_AMOUNT.getValue(), receiptDao.getPremiumCurrencyExchAmount());
        receiptArguments.put(RBVDProperties.FIELD_PREMIUM_CHARGE_OPERATION_ID.getValue(), receiptDao.getPremiumChargeOperationId());
        receiptArguments.put(RBVDProperties.FIELD_CURRENCY_ID.getValue(), receiptDao.getCurrencyId());
        receiptArguments.put(RBVDProperties.FIELD_RECEIPT_ISSUE_DATE.getValue(), receiptDao.getReceiptIssueDate());
        receiptArguments.put(RBVDProperties.FIELD_RECEIPT_START_DATE.getValue(), receiptDao.getReceiptStartDate());
        receiptArguments.put(RBVDProperties.FIELD_RECEIPT_END_DATE.getValue(), receiptDao.getReceiptEndDate());
        receiptArguments.put(RBVDProperties.FIELD_RECEIPT_COLLECTION_DATE.getValue(), receiptDao.getReceiptCollectionDate());
        receiptArguments.put(RBVDProperties.FIELD_RECEIPT_EXPIRATION_DATE.getValue(), receiptDao.getReceiptExpirationDate());
        receiptArguments.put(RBVDProperties.FIELD_RECEIPTS_TRANSMISSION_DATE.getValue(), receiptDao.getReceiptsTransmissionDate());
        receiptArguments.put(RBVDProperties.FIELD_RECEIPT_COLLECTION_STATUS_TYPE.getValue(), receiptDao.getReceiptCollectionStatusType());
        receiptArguments.put(RBVDProperties.FIELD_INSURANCE_COLLECTION_MOVE_ID.getValue(), receiptDao.getInsuranceCollectionMoveId());
        receiptArguments.put(RBVDProperties.FIELD_PAYMENT_METHOD_TYPE.getValue(), receiptDao.getPaymentMethodType());
        receiptArguments.put(RBVDProperties.FIELD_DEBIT_ACCOUNT_ID.getValue(), receiptDao.getDebitAccountId());
        receiptArguments.put(RBVDProperties.FIELD_DEBIT_CHANNEL_TYPE.getValue(), receiptDao.getDebitChannelType());
        receiptArguments.put(RBVDProperties.FIELD_CHARGE_ATTEMPTS_NUMBER.getValue(), receiptDao.getChargeAttemptsNumber());
        receiptArguments.put(RBVDProperties.FIELD_INSRNC_CO_RECEIPT_STATUS_TYPE.getValue(), receiptDao.getInsrncCoReceiptStatusType());
        receiptArguments.put(RBVDProperties.FIELD_RECEIPT_STATUS_TYPE.getValue(), receiptDao.getReceiptStatusType());
        receiptArguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), receiptDao.getCreationUserId());
        receiptArguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), receiptDao.getUserAuditId());
        receiptArguments.put(RBVDProperties.FIELD_MANAGEMENT_BRANCH_ID.getValue(), receiptDao.getManagementBranchId());
        receiptArguments.put(RBVDProperties.FIELD_VARIABLE_PREMIUM_AMOUNT.getValue(), receiptDao.getVariablePremiumAmount());
        receiptArguments.put(RBVDProperties.FIELD_FIX_PREMIUM_AMOUNT.getValue(), receiptDao.getFixPremiumAmount());
        receiptArguments.put(RBVDProperties.FIELD_SETTLEMENT_VAR_PREMIUM_AMOUNT.getValue(), receiptDao.getSettlementVarPremiumAmount());
        receiptArguments.put(RBVDProperties.FIELD_SETTLEMENT_FIX_PREMIUM_AMOUNT.getValue(), receiptDao.getSettlementFixPremiumAmount());
        receiptArguments.put(RBVDProperties.FIELD_LAST_CHANGE_BRANCH_ID.getValue(), receiptDao.getLastChangeBranchId());
        receiptArguments.put(RBVDProperties.FIELD_GL_BRANCH_ID.getValue(), receiptDao.getGlBranchId());

        return receiptArguments;
    }

    public IsrcContractMovDAO buildIsrcContractMov(PolicyASO asoResponse, String creationUser, String userAudit) {
        IsrcContractMovDAO isrcContractMovDao = new IsrcContractMovDAO();
        isrcContractMovDao.setEntityId(asoResponse.getData().getId().substring(0, 4));
        isrcContractMovDao.setBranchId(asoResponse.getData().getId().substring(4, 8));
        isrcContractMovDao.setIntAccountId(asoResponse.getData().getId().substring(10));
        isrcContractMovDao.setPolicyMovementNumber(BigDecimal.valueOf(1));
        isrcContractMovDao.setGlAccountDate(currentDate);
        isrcContractMovDao.setGlBranchId(asoResponse.getData().getId().substring(4, 8));
        isrcContractMovDao.setCreationUserId(creationUser);
        isrcContractMovDao.setUserAuditId(userAudit);
        return isrcContractMovDao;
    }

    public Map<String, Object> createSaveContractMovArguments(IsrcContractMovDAO isrcContractMovDao) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), isrcContractMovDao.getEntityId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(), isrcContractMovDao.getBranchId());
        arguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), isrcContractMovDao.getIntAccountId());
        arguments.put(RBVDProperties.FIELD_POLICY_MOVEMENT_NUMBER.getValue(), isrcContractMovDao.getPolicyMovementNumber());
        arguments.put(RBVDProperties.FIELD_GL_ACCOUNT_DATE.getValue(), isrcContractMovDao.getGlAccountDate());
        arguments.put(RBVDProperties.FIELD_GL_BRANCH_ID.getValue(), isrcContractMovDao.getGlBranchId());
        arguments.put(RBVDProperties.FIELD_MOVEMENT_TYPE.getValue(), isrcContractMovDao.getMovementType());
        arguments.put(RBVDProperties.FIELD_ADDITIONAL_DATA_DESC.getValue(), isrcContractMovDao.getAdditionalDataDesc());
        arguments.put(RBVDProperties.FIELD_CONTRACT_STATUS_ID.getValue(), isrcContractMovDao.getContractStatusId());
        arguments.put(RBVDProperties.FIELD_MOVEMENT_STATUS_TYPE.getValue(), isrcContractMovDao.getMovementStatusType());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), isrcContractMovDao.getCreationUserId());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), isrcContractMovDao.getUserAuditId());
        return arguments;
    }


    public List<IsrcContractParticipantDAO> buildIsrcContractParticipants(PolicyDTO requestBody, Map<String, Object> responseQueryRoles, String id) {
        ParticipantDTO participant = requestBody.getParticipants().get(0);

        List<Map<String, Object>> roles = (List<Map<String, Object>>) responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue());
        return roles.stream().map(rol -> createParticipantDao(id, rol, participant, requestBody)).collect(Collectors.toList());
    }

    private IsrcContractParticipantDAO createParticipantDao(String id, Map<String, Object> rol, ParticipantDTO participant, PolicyDTO requestBody) {
        IsrcContractParticipantDAO participantDao = new IsrcContractParticipantDAO();
        participantDao.setEntityId(id.substring(0,4));
        participantDao.setBranchId(id.substring(4, 8));
        participantDao.setIntAccountId(id.substring(10));
        participantDao.setParticipantRoleId((BigDecimal) rol.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue()));
        participantDao.setPersonalDocType(this.applicationConfigurationService.getProperty(participant.getIdentityDocument().getDocumentType().getId()));
        participantDao.setParticipantPersonalId(participant.getIdentityDocument().getNumber());
        participantDao.setCustomerId(participant.getCustomerId());
        participantDao.setCreationUserId(requestBody.getCreationUser());
        participantDao.setUserAuditId(requestBody.getUserAudit());
        return participantDao;
    }

    public Map<String, Object> createSaveParticipantArguments(IsrcContractParticipantDAO participant) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), participant.getEntityId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(), participant.getBranchId());
        arguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), participant.getIntAccountId());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(), participant.getParticipantRoleId());
        arguments.put(RBVDProperties.FIELD_PARTY_ORDER_NUMBER.getValue(), participant.getPartyOrderNumber());
        arguments.put(RBVDProperties.FIELD_PERSONAL_DOC_TYPE.getValue(), participant.getPersonalDocType());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue(), participant.getParticipantPersonalId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ID.getValue(), participant.getCustomerId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_RELATIONSHIP_TYPE.getValue(), participant.getCustomerRelationshipType());
        arguments.put(RBVDProperties.FIELD_REGISTRY_SITUATION_TYPE.getValue(), participant.getRegistrySituationType());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), participant.getCreationUserId());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), participant.getUserAuditId());
        return arguments;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

}
