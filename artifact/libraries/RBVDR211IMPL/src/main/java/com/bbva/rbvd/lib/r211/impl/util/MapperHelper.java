package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.*;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.commons.HolderDTO;
import com.bbva.rbvd.dto.insrncsale.commons.PolicyInspectionDTO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.policy.*;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class MapperHelper {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static final String EMAIL_VALUE = "EMAIL";
    private static final String PHONE_NUMBER_VALUE = "PHONE";
    private static final String PARTICULAR_DATA_THIRD_CHANNEL = "CANAL_TERCERO";
    private static final String PARTICULAR_DATA_ACCOUNT_DATA = "DATOS_DE_CUENTA";
    private static final String S_VALUE = "S";
    private static final String N_VALUE = "N";
    private static final Long INDICATOR_INSPECTION_VALUE = 1L;
    private static final String PAYMENT_METHOD_VALUE = "DIRECT_DEBIT";
    private static final String COLLECTION_STATUS_FIRST_RECEIPT_VALUE = "00";
    private static final String CARD_PRODUCT_ID = "CARD";
    private static final String CARD_METHOD_TYPE = "T";
    private static final String ACCOUNT_METHOD_TYPE = "C";
    private static final String FIRST_RECEIPT_STATUS_TYPE_VALUE = "COB";

    private final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);

    public Map<String, Object> insuranceProductFilterCreation(String productId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(PISDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue(), productId);
        return filter;
    }

    public Map<String, Object> productModalityFiltersCreation(BigDecimal insuranceProductId, String insuranceModalityType) {
        Map<String, Object> filters = new HashMap<>();
        filters.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(), insuranceProductId);
        filters.put(RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(), Collections.singletonList(insuranceModalityType));
        return filters;
    }

    public PolicyDTO buildAsoRequest(PolicyDTO apxRequest) {
        PolicyDTO requestAso = new PolicyDTO();
        requestAso.setQuotationId(apxRequest.getQuotationId());
        requestAso.setProductId(apxRequest.getProductId());
        requestAso.setProductPlan(apxRequest.getProductPlan());

        PolicyPaymentMethodDTO paymentMethod = new PolicyPaymentMethodDTO();
        paymentMethod.setPaymentType(apxRequest.getPaymentMethod().getPaymentType());
        paymentMethod.setInstallmentFrequency(apxRequest.getPaymentMethod().getInstallmentFrequency());
        RelatedContractDTO paymentRelatedContract = new RelatedContractDTO();
        paymentRelatedContract.setNumber(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getNumber());
        paymentRelatedContract.setProduct(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getProduct());
        paymentMethod.setRelatedContracts(Collections.singletonList(paymentRelatedContract));

        requestAso.setPaymentMethod(paymentMethod);
        requestAso.setValidityPeriod(apxRequest.getValidityPeriod());
        requestAso.setTotalAmount(apxRequest.getTotalAmount());
        requestAso.setInsuredAmount(apxRequest.getInsuredAmount());

        HolderDTO holder = new HolderDTO();
        holder.setId(apxRequest.getHolder().getId());
        holder.setIdentityDocument(apxRequest.getHolder().getIdentityDocument());

        requestAso.setHolder(holder);
        requestAso.setInstallmentPlan(apxRequest.getInstallmentPlan());

        FirstInstallmentDTO firstInstallment = new FirstInstallmentDTO();
        firstInstallment.setIsPaymentRequired(apxRequest.getFirstInstallment().getIsPaymentRequired());

        requestAso.setFirstInstallment(firstInstallment);

        ParticipantDTO participant = new ParticipantDTO();
        participant.setParticipantType(apxRequest.getParticipants().get(0).getParticipantType());
        participant.setCustomerId(apxRequest.getParticipants().get(0).getCustomerId());
        participant.setIdentityDocument(apxRequest.getParticipants().get(0).getIdentityDocument());

        requestAso.setParticipants(Collections.singletonList(participant));
        requestAso.setBusinessAgent(apxRequest.getBusinessAgent());
        requestAso.setPromoter(apxRequest.getPromoter());
        requestAso.setBank(apxRequest.getBank());
        requestAso.setInsuranceCompany(apxRequest.getInsuranceCompany());
        return requestAso;
    }

    public EmisionBO buildRequestBodyRimac(PolicyInspectionDTO inspection, String secondParticularDataValue, String channelCode) {
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

        payload.setDatosParticulares(datosParticulares);
        payload.setEnvioElectronico(S_VALUE);
        payload.setIndCobro(N_VALUE);
        payload.setIndInspeccion(INDICATOR_INSPECTION_VALUE);

        rimacRequest.setPayload(payload);
        return rimacRequest;
    }

    public InsuranceContractDAO buildInsuranceContract(EmisionBO rimacResponse, PolicyDTO apxRequest, BigDecimal productId, String asoId) {
        InsuranceContractDAO contractDao = new InsuranceContractDAO();

        contractDao.setEntityId(asoId.substring(0, 4));
        contractDao.setBranchId(asoId.substring(4, 8));
        contractDao.setIntAccountId(asoId.substring(10));
        contractDao.setFirstVerfnDigitId(asoId.substring(8,9));
        contractDao.setSecondVerfnDigitId(asoId.substring(9,10));
        contractDao.setPolicyQuotaInternalId(apxRequest.getQuotationId());
        contractDao.setInsuranceProductId(productId);
        contractDao.setInsuranceModalityType(apxRequest.getProductPlan().getId());
        contractDao.setInsuranceCompanyId(new BigDecimal(apxRequest.getInsuranceCompany().getId()));
        if(Objects.nonNull(rimacResponse)) {
            contractDao.setPolicyId(rimacResponse.getPayload().getNumeroPoliza());
            contractDao.setInsuranceContractEndDate(format.format(rimacResponse.getPayload().getFechaFinal()));

            int numeroCuotas = rimacResponse.getPayload().getCuotasFinanciamiento().size();
            Date fechaUltimaCuota = rimacResponse.getPayload().getCuotasFinanciamiento().
                    stream().filter(cuota -> cuota.getCuota() == numeroCuotas).findFirst().get().getFechaVencimiento();

            contractDao.setLastInstallmentDate(format.format(fechaUltimaCuota));

            if(numeroCuotas == 1) {
                contractDao.setPeriodNextPaymentDate(format.format(fechaUltimaCuota));
            } else {
                Date fechaSegundaCuota = rimacResponse.getPayload().getCuotasFinanciamiento().
                        stream().filter(cuota -> cuota.getCuota() == 2).findFirst().get().getFechaVencimiento();
                contractDao.setPeriodNextPaymentDate(format.format(fechaSegundaCuota));
            }

            contractDao.setInsuranceCompanyProductId(rimacResponse.getPayload().getCodProducto());
        } else {
            contractDao.setInsuranceContractEndDate(format.format(new Date()));
            contractDao.setLastInstallmentDate(format.format(new Date()));
            contractDao.setPeriodNextPaymentDate(format.format(new Date()));
        }
        contractDao.setInsuranceManagerId(apxRequest.getBusinessAgent().getId());
        contractDao.setInsurancePromoterId(apxRequest.getPromoter().getId());
        contractDao.setContractManagerBranchId(asoId.substring(4, 8));
        contractDao.setContractInceptionDate(format.format(new Date()));
        contractDao.setInsuranceContractStartDate(format.format(apxRequest.getValidityPeriod().getStartDate()));
        contractDao.setCustomerId(apxRequest.getHolder().getId());
        contractDao.setDomicileContractId(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getContractId());
        contractDao.setIssuedReceiptNumber(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()));

        contractDao.setPaymentFrequencyId(BigDecimal.valueOf(1));

        contractDao.setPremiumAmount(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()));
        contractDao.setSettlePendingPremiumAmount(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getPaymentAmount().getAmount()));
        contractDao.setCurrencyId(apxRequest.getInstallmentPlan().getPaymentAmount().getCurrency());
        contractDao.setInstallmentPeriodFinalDate(format.format(new Date()));
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
        arguments.put(RBVDProperties.FIELD_POLICY_PYMT_PEND_DUE_DEBT_TYPE.getValue(), contractDao.getPolicyPymtPendDueDebtType());
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

    public InsuranceCtrReceiptsDAO buildInsuranceCtrReceipt(PolicyASO asoResponse, EmisionBO rimacResponse, PolicyDTO requestBody) {
        InsuranceCtrReceiptsDAO receiptDao = new InsuranceCtrReceiptsDAO();
        receiptDao.setEntityId(asoResponse.getData().getId().substring(0, 4));
        receiptDao.setBranchId(asoResponse.getData().getId().substring(4, 8));
        receiptDao.setIntAccountId(asoResponse.getData().getId().substring(10));
        receiptDao.setPolicyReceiptId(new BigDecimal(asoResponse.getData().getId().substring(8,9)));
        receiptDao.setInsuranceCompanyId(new BigDecimal(asoResponse.getData().getId().substring(9,10)));
        receiptDao.setPremiumPaymentReceiptAmount(BigDecimal.valueOf(requestBody.getFirstInstallment().getPaymentAmount().getAmount()));

        if(Objects.nonNull(asoResponse.getData().getFirstInstallment().getExchangeRate())) {
            receiptDao.setFixingExchangeRateAmount(BigDecimal.valueOf(asoResponse.getData()
                    .getFirstInstallment().getExchangeRate().getDetail().getFactor().getRatio()));
            receiptDao.setPremiumCurrencyExchAmount(BigDecimal.valueOf(asoResponse.getData()
                    .getFirstInstallment().getExchangeRate().getDetail().getFactor().getValue()));
        } else {
            receiptDao.setFixingExchangeRateAmount(BigDecimal.valueOf(0));
            receiptDao.setPremiumCurrencyExchAmount(BigDecimal.valueOf(0));
        }

        String currentDate = format.format(new Date());

        receiptDao.setPremiumChargeOperationId(asoResponse.getData().getFirstInstallment().getOperationNumber());
        receiptDao.setCurrencyId(requestBody.getFirstInstallment().getPaymentAmount().getCurrency());
        receiptDao.setReceiptStartDate(currentDate);

        if(Objects.nonNull(asoResponse.getData().getFirstInstallment().getOperationDate())) {
            receiptDao.setReceiptIssueDate(format.format(asoResponse.getData().getFirstInstallment().getOperationDate()));
            receiptDao.setReceiptCollectionDate(format.format(asoResponse.getData().getFirstInstallment().getOperationDate()));
            receiptDao.setReceiptsTransmissionDate(format.format(asoResponse.getData().getFirstInstallment().getOperationDate()));
        } else {
            receiptDao.setReceiptIssueDate(currentDate);
            receiptDao.setReceiptCollectionDate(currentDate);
            receiptDao.setReceiptsTransmissionDate(currentDate);
        }

        receiptDao.setReceiptCollectionStatusType(COLLECTION_STATUS_FIRST_RECEIPT_VALUE);
        receiptDao.setInsuranceCollectionMoveId(asoResponse.getData().getFirstInstallment().getTransactionNumber());
        receiptDao.setPaymentMethodType(requestBody.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId().
                equals(CARD_PRODUCT_ID) ? CARD_METHOD_TYPE : ACCOUNT_METHOD_TYPE);
        receiptDao.setDebitAccountId(requestBody.getPaymentMethod().getRelatedContracts().get(0).getContractId());
        receiptDao.setDebitChannelType(requestBody.getSaleChannelId());
        receiptDao.setReceiptStatusType(FIRST_RECEIPT_STATUS_TYPE_VALUE);
        receiptDao.setCreationUserId(requestBody.getCreationUser());
        receiptDao.setUserAuditId(requestBody.getUserAudit());
        receiptDao.setManagementBranchId(asoResponse.getData().getId().substring(4, 8));
        receiptDao.setFixPremiumAmount(BigDecimal.valueOf(requestBody.getFirstInstallment().getPaymentAmount().getAmount()));
        receiptDao.setSettlementFixPremiumAmount(BigDecimal.valueOf(requestBody.getInstallmentPlan().getPaymentAmount().getAmount()));
        receiptDao.setLastChangeBranchId(requestBody.getBank().getBranch().getId());
        receiptDao.setGlBranchId(asoResponse.getData().getId().substring(4, 8));

        if(Objects.nonNull(rimacResponse)) {
            Date endDate = rimacResponse.getPayload().getCuotasFinanciamiento().stream().
                    filter(cuota -> cuota.getCuota() == 1).findFirst().get().getFechaVencimiento();
            receiptDao.setReceiptEndDate(format.format(endDate));
            receiptDao.setReceiptExpirationDate(format.format(endDate));
        } else {
            receiptDao.setReceiptEndDate(currentDate);
            receiptDao.setReceiptExpirationDate(currentDate);
        }
        return receiptDao;
    }

    public Map<String, Object> createSaveReceiptsArguments(InsuranceCtrReceiptsDAO receiptDao) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), receiptDao.getEntityId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(), receiptDao.getBranchId());
        arguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), receiptDao.getIntAccountId());
        arguments.put(RBVDProperties.FIELD_POLICY_RECEIPT_ID.getValue(), receiptDao.getPolicyReceiptId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue(), receiptDao.getInsuranceCompanyId());
        arguments.put(RBVDProperties.FIELD_PREMIUM_PAYMENT_RECEIPT_AMOUNT.getValue(), receiptDao.getPremiumPaymentReceiptAmount());
        arguments.put(RBVDProperties.FIELD_FIXING_EXCHANGE_RATE_AMOUNT.getValue(), receiptDao.getFixingExchangeRateAmount());
        arguments.put(RBVDProperties.FIELD_PREMIUM_CURRENCY_EXCH_AMOUNT.getValue(), receiptDao.getPremiumCurrencyExchAmount());
        arguments.put(RBVDProperties.FIELD_PREMIUM_CHARGE_OPERATION_ID.getValue(), receiptDao.getPremiumChargeOperationId());
        arguments.put(RBVDProperties.FIELD_CURRENCY_ID.getValue(), receiptDao.getCurrencyId());
        arguments.put(RBVDProperties.FIELD_RECEIPT_ISSUE_DATE.getValue(), receiptDao.getReceiptIssueDate());
        arguments.put(RBVDProperties.FIELD_RECEIPT_START_DATE.getValue(), receiptDao.getReceiptStartDate());
        arguments.put(RBVDProperties.FIELD_RECEIPT_END_DATE.getValue(), receiptDao.getReceiptEndDate());
        arguments.put(RBVDProperties.FIELD_RECEIPT_COLLECTION_DATE.getValue(), receiptDao.getReceiptCollectionDate());
        arguments.put(RBVDProperties.FIELD_RECEIPT_EXPIRATION_DATE.getValue(), receiptDao.getReceiptExpirationDate());
        arguments.put(RBVDProperties.FIELD_RECEIPTS_TRANSMISSION_DATE.getValue(), receiptDao.getReceiptsTransmissionDate());
        arguments.put(RBVDProperties.FIELD_RECEIPT_COLLECTION_STATUS_TYPE.getValue(), receiptDao.getReceiptCollectionStatusType());
        arguments.put(RBVDProperties.FIELD_INSURANCE_COLLECTION_MOVE_ID.getValue(), receiptDao.getInsuranceCollectionMoveId());
        arguments.put(RBVDProperties.FIELD_PAYMENT_METHOD_TYPE.getValue(), receiptDao.getPaymentMethodType());
        arguments.put(RBVDProperties.FIELD_DEBIT_ACCOUNT_ID.getValue(), receiptDao.getDebitAccountId());
        arguments.put(RBVDProperties.FIELD_DEBIT_CHANNEL_TYPE.getValue(), receiptDao.getDebitChannelType());
        arguments.put(RBVDProperties.FIELD_CHARGE_ATTEMPTS_NUMBER.getValue(), receiptDao.getChargeAttemptsNumber());
        arguments.put(RBVDProperties.FIELD_INSRNC_CO_RECEIPT_STATUS_TYPE.getValue(), receiptDao.getInsrncCoReceiptStatusType());
        arguments.put(RBVDProperties.FIELD_RECEIPT_STATUS_TYPE.getValue(), receiptDao.getReceiptStatusType());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), receiptDao.getCreationUserId());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), receiptDao.getUserAuditId());
        arguments.put(RBVDProperties.FIELD_MANAGEMENT_BRANCH_ID.getValue(), receiptDao.getManagementBranchId());
        arguments.put(RBVDProperties.FIELD_VARIABLE_PREMIUM_AMOUNT.getValue(), receiptDao.getVariablePremiumAmount());
        arguments.put(RBVDProperties.FIELD_FIX_PREMIUM_AMOUNT.getValue(), receiptDao.getFixPremiumAmount());
        arguments.put(RBVDProperties.FIELD_SETTLEMENT_VAR_PREMIUM_AMOUNT.getValue(), receiptDao.getSettlementVarPremiumAmount());
        arguments.put(RBVDProperties.FIELD_SETTLEMENT_FIX_PREMIUM_AMOUNT.getValue(), receiptDao.getSettlementFixPremiumAmount());
        arguments.put(RBVDProperties.FIELD_LAST_CHANGE_BRANCH_ID.getValue(), receiptDao.getLastChangeBranchId());
        arguments.put(RBVDProperties.FIELD_GL_BRANCH_ID.getValue(), receiptDao.getGlBranchId());
        return arguments;
    }

    public IsrcContractMovDAO buildIsrcContractMov(PolicyASO asoResponse, String creationUser, String userAudit) {
        IsrcContractMovDAO isrcContractMovDao = new IsrcContractMovDAO();
        isrcContractMovDao.setEntityId(asoResponse.getData().getId().substring(0, 4));
        isrcContractMovDao.setBranchId(asoResponse.getData().getId().substring(4, 8));
        isrcContractMovDao.setIntAccountId(asoResponse.getData().getId().substring(10));
        isrcContractMovDao.setPolicyMovementNumber(BigDecimal.valueOf(1));
        isrcContractMovDao.setGlAccountDate(format.format(new Date()));
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
        List<IsrcContractParticipantDAO> participantsList = new ArrayList<>();

        ParticipantDTO participant = requestBody.getParticipants().get(0);

        List<Map<String, Object>> roles = (List<Map<String, Object>>) responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue());

        for(Map<String, Object> rol : roles) {
            IsrcContractParticipantDAO participantDao = new IsrcContractParticipantDAO();
            participantDao.setEntityId(id.substring(0,4));
            participantDao.setBranchId(id.substring(4, 8));
            participantDao.setIntAccountId(id.substring(10));
            participantDao.setParticipantRoleId((BigDecimal) rol.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue()));
            participantDao.setPersonalDocType(participant.getIdentityDocument().getDocumentType().getId());
            participantDao.setParticipantPersonalId(participant.getIdentityDocument().getNumber());
            participantDao.setCustomerId(participant.getCustomerId());
            participantDao.setCreationUserId(requestBody.getCreationUser());
            participantDao.setUserAuditId(requestBody.getUserAudit());
            participantsList.add(participantDao);
        }
        return participantsList;
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

}
