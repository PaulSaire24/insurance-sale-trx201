package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ContactASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ContactDetailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.gifole.InsuranceASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PeriodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PlanASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ProductASO;
import com.bbva.pisd.dto.insurance.aso.gifole.QuotationASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractProductASO;
import com.bbva.rbvd.dto.insrncsale.aso.HolderASO;
import com.bbva.rbvd.dto.insrncsale.aso.IdentityDocumentASO;
import com.bbva.rbvd.dto.homeinsrc.dao.SimltInsuredHousingDAO;
import com.bbva.rbvd.dto.insrncsale.aso.DocumentTypeASO;
import com.bbva.rbvd.dto.insrncsale.aso.PaymentAmountASO;
import com.bbva.rbvd.dto.insrncsale.aso.ExchangeRateASO;

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
import com.bbva.rbvd.dto.insrncsale.bo.emision.FinanciamientoBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PayloadEmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarPersonaBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.ContactoInspeccionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.CrearCronogramaBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.DatoParticularBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.CuotaFinancimientoBO;

import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.commons.PaymentAmountDTO;
import com.bbva.rbvd.dto.insrncsale.commons.PolicyInspectionDTO;
import com.bbva.rbvd.dto.insrncsale.commons.QuotationStatusDTO;

import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;

import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.RelatedContractDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ExchangeRateDTO;
import com.bbva.rbvd.dto.insrncsale.policy.DetailDTO;
import com.bbva.rbvd.dto.insrncsale.policy.FactorDTO;

import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Objects;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

public class MapperHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperHelper.class);

    private static final String EMAIL_VALUE = "EMAIL";
    private static final String PHONE_NUMBER_VALUE = "PHONE";
    private static final String PARTICULAR_DATA_THIRD_CHANNEL = "CANAL_TERCERO";
    private static final String PARTICULAR_DATA_ACCOUNT_DATA = "DATOS_DE_CUENTA";
    private static final String PARTICULAR_DATA_CERT_BANCO = "NRO_CERT_BANCO";
    private static final String PARTICULAR_DATA_SALE_OFFICE = "OFICINA_VENTA";
    private static final String S_VALUE = "S";
    private static final String N_VALUE = "N";
    private static final Long INDICATOR_INSPECTION_NOT_REQUIRED_VALUE = 0L;
    private static final Long INDICATOR_INSPECTION_REQUIRED_VALUE = 1L;
    private static final String PAYMENT_METHOD_VALUE = "DIRECT_DEBIT";
    private static final String COLLECTION_STATUS_FIRST_RECEIPT_VALUE = "00";
    private static final String COLLECTION_STATUS_NEXT_VALUES = "02";
    private static final String CARD_PRODUCT_ID = "CARD";
    private static final String CARD_PRODUCT_NAME = "TARJETA";
    private static final String ACCOUNT_PRODUCT_NAME = "CUENTA";
    private static final String CARD_METHOD_TYPE = "T";
    private static final String ACCOUNT_METHOD_TYPE = "C";
    private static final String FIRST_RECEIPT_STATUS_TYPE_VALUE = "COB";
    private static final String NEXT_RECEIPTS_STATUS_TYPE_VALUE = "INC";
    private static final String RECEIPT_DEFAULT_DATE_VALUE = "01/01/0001";
    private static final String PRICE_TYPE_VALUE = "PURCHASE";
    private static final String TAG_ENDORSEE = "ENDORSEE";


    private static final String TEMPLATE_EMAIL_CODE_VEH = "PLT00945";
    private static final String SUBJECT_EMAIL_VEH = "!Genial! Acabas de comprar tu seguro vehicular con éxito";
    private static final String MAIL_SENDER = "procesos@bbva.com.pe";

    private static final String GMT_TIME_ZONE = "GMT";

    private static final String TEMPLATE_EMAIL_CODE_HOME = "PLT00968";
    private static final String SUBJECT_EMAIL_HOME = "!Genial! Acabas de comprar tu Seguro Hogar Total con éxito";
    private static final String NONE = "none";
    private static final String PEN_CURRENCY = "S/";
    private static final String USD_CURRENCY = "US$";

    private static final String RUC_ID = "RUC";

    private static final String INSURANCE_GIFOLE_VAL = "INSURANCE_CREATION";
    private static final String MASK_VALUE = "****";
    private static final DateTimeZone DATE_TIME_ZONE = DateTimeZone.forID("America/Lima");

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

    private ApplicationConfigurationService applicationConfigurationService;

    private String currentDate;

    public MapperHelper() {
        this.currentDate = generateCorrectDateFormat(new LocalDate());
    }

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
        validityPeriod.setStartDate(convertDateToLocalDate(apxRequest.getValidityPeriod().getStartDate()));

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
        installmentPlan.setStartDate(convertDateToLocalDate(apxRequest.getInstallmentPlan().getStartDate()));
        installmentPlan.setMaturityDate(convertDateToLocalDate(apxRequest.getInstallmentPlan().getMaturityDate()));
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

        if(Objects.nonNull(apxRequest.getBusinessAgent())) {
            BusinessAgentASO businessAgent = new BusinessAgentASO();
            businessAgent.setId(apxRequest.getBusinessAgent().getId());
            requestAso.setBusinessAgent(businessAgent);
        }

        if(Objects.nonNull(apxRequest.getPromoter())) {
            PromoterASO promoter = new PromoterASO();
            promoter.setId(apxRequest.getPromoter().getId());
            requestAso.setPromoter(promoter);
        }

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

    public EmisionBO buildRequestBodyRimac(PolicyInspectionDTO inspection, String secondParticularDataValue, String channelCode,
                                           String dataId, String saleOffice) {
        EmisionBO rimacRequest = new EmisionBO();

        PayloadEmisionBO payload = new PayloadEmisionBO();

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

        DatoParticularBO cuartoDatoParticular = new DatoParticularBO();
        cuartoDatoParticular.setEtiqueta(PARTICULAR_DATA_SALE_OFFICE);
        cuartoDatoParticular.setCodigo("");
        cuartoDatoParticular.setValor(saleOffice);
        datosParticulares.add(cuartoDatoParticular);

        payload.setDatosParticulares(datosParticulares);
        payload.setEnvioElectronico(N_VALUE);
        payload.setIndCobro(N_VALUE);
        payload.setIndValidaciones(N_VALUE);

        if(inspection.getIsRequired()) {
            ContactoInspeccionBO contactoInspeccion = new ContactoInspeccionBO();
            contactoInspeccion.setNombre(inspection.getFullName());

            ContactDetailDTO contactEmail = inspection.getContactDetails().stream().
                    filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(EMAIL_VALUE)).findFirst().orElse(null);

            ContactDetailDTO contactPhone = inspection.getContactDetails().stream().
                    filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(PHONE_NUMBER_VALUE)).findFirst().orElse(null);

            contactoInspeccion.setCorreo( Objects.nonNull(contactEmail) ? contactEmail.getContact().getAddress() : null);
            contactoInspeccion.setTelefono( Objects.nonNull(contactPhone) ? contactPhone.getContact().getPhoneNumber() : null);

            payload.setContactoInspeccion(contactoInspeccion);
            payload.setIndInspeccion(INDICATOR_INSPECTION_REQUIRED_VALUE);
        } else {
            payload.setIndInspeccion(INDICATOR_INSPECTION_NOT_REQUIRED_VALUE);
        }

        rimacRequest.setPayload(payload);
        return rimacRequest;
    }

    public InsuranceContractDAO buildInsuranceContract(EmisionBO rimacResponse, PolicyDTO apxRequest, RequiredFieldsEmissionDAO emissionDao, String asoId, Boolean isEndorsement) {
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

        contractDao.setPolicyId(rimacResponse.getPayload().getNumeroPoliza());

        String policyExpiration = generateCorrectDateFormat(rimacResponse.getPayload().getFechaFinal());

        contractDao.setInsuranceContractEndDate(policyExpiration);

        int numeroCuotas = rimacResponse.getPayload().getCuotasFinanciamiento().size();
        CuotaFinancimientoBO ultimaCuota = rimacResponse.getPayload().getCuotasFinanciamiento().
                stream().filter(cuota -> cuota.getCuota() == numeroCuotas).findFirst().orElse(null);

        if(Objects.nonNull(ultimaCuota)) {

            contractDao.setLastInstallmentDate(generateCorrectDateFormat(ultimaCuota.getFechaVencimiento()));
            contractDao.setPeriodNextPaymentDate((numeroCuotas == 1) ?
                    policyExpiration : getNextPaymentDate(rimacResponse));
        }

        contractDao.setInsuranceCompanyProductId(rimacResponse.getPayload().getCodProducto());

        if(Objects.nonNull(apxRequest.getBusinessAgent())) {
            contractDao.setInsuranceManagerId(apxRequest.getBusinessAgent().getId());
        }

        if(Objects.nonNull(apxRequest.getPromoter())) {
            contractDao.setInsurancePromoterId(apxRequest.getPromoter().getId());
        }

        contractDao.setContractManagerBranchId(apxRequest.getBank().getBranch().getId());
        contractDao.setContractInceptionDate(currentDate);

        contractDao.setInsuranceContractStartDate(generateCorrectDateFormat(
                        convertDateToLocalDate(apxRequest.getValidityPeriod().getStartDate())));

        contractDao.setValidityMonthsNumber(emissionDao.getContractDurationType().equals("A")
                ? emissionDao.getContractDurationNumber().multiply(BigDecimal.valueOf(12))
                : emissionDao.getContractDurationNumber());

        contractDao.setInsurancePolicyEndDate(policyExpiration);

        contractDao.setCustomerId(apxRequest.getHolder().getId());
        contractDao.setDomicileContractId(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getContractId());
        contractDao.setIssuedReceiptNumber(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()));

        contractDao.setPaymentFrequencyId(emissionDao.getPaymentFrequencyId());

        contractDao.setPremiumAmount(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()));
        contractDao.setSettlePendingPremiumAmount(BigDecimal.valueOf(apxRequest.getTotalAmount().getAmount()));
        contractDao.setCurrencyId(apxRequest.getInstallmentPlan().getPaymentAmount().getCurrency());
        contractDao.setInstallmentPeriodFinalDate(currentDate);
        contractDao.setInsuredAmount(BigDecimal.valueOf(apxRequest.getInsuredAmount().getAmount()));
        contractDao.setCtrctDisputeStatusType(apxRequest.getSaleChannelId());
        contractDao.setCreationUserId(apxRequest.getCreationUser());
        contractDao.setUserAuditId(apxRequest.getUserAudit());
        contractDao.setInsurPendingDebtIndType((apxRequest.getFirstInstallment().getIsPaymentRequired()) ? N_VALUE : S_VALUE);

        contractDao.setTotalDebtAmount((apxRequest.getFirstInstallment().getIsPaymentRequired())
                ? BigDecimal.valueOf(0) : BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()));

        contractDao.setPrevPendBillRcptsNumber((apxRequest.getFirstInstallment().getIsPaymentRequired())
                ? BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments() - 1)
                : BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()));

                contractDao.setSettlementFixPremiumAmount(BigDecimal.valueOf(apxRequest.getTotalAmount().getAmount()));
        contractDao.setAutomaticDebitIndicatorType((apxRequest.getPaymentMethod().getPaymentType().equals(PAYMENT_METHOD_VALUE))
                ? S_VALUE : N_VALUE);
        contractDao.setBiometryTransactionId(apxRequest.getIdentityVerificationCode());
        if(isEndorsement)
            contractDao.setEndorsementPolicyIndType("S");

        return contractDao;
    }

    private String getNextPaymentDate(EmisionBO rimacResponse) {
        String nextPaymentDate = null;
        CuotaFinancimientoBO segundaCuota = rimacResponse.getPayload().getCuotasFinanciamiento().
                stream().filter(cuota -> cuota.getCuota() == 2).findFirst().orElse(null);
        if(Objects.nonNull(segundaCuota)) {
            nextPaymentDate = generateCorrectDateFormat(segundaCuota.getFechaVencimiento());
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
        arguments.put(RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue(), contractDao.getInsurancePolicyEndDate());
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

    public Map<String, Object> createSaveEndorsementArguments(InsuranceContractDAO contractDao, String endosatarioRuc, Double endosatarioPorcentaje) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), contractDao.getEntityId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(), contractDao.getBranchId());
        arguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), contractDao.getIntAccountId());
        arguments.put(RBVDProperties.FIELD_DOCUMENT_TYPE_ID.getValue(), "R");
        arguments.put(RBVDProperties.FIELD_DOCUMENT_ID.getValue(), endosatarioRuc);
        arguments.put(RBVDProperties.FIELD_ENDORSEMENT_SEQUENCE_NUMBER.getValue(), 1);
        arguments.put(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue(), contractDao.getPolicyId());
        arguments.put(RBVDProperties.FIELD_ENDORSEMENT_EFF_START_DATE.getValue(), contractDao.getInsuranceContractStartDate());
        arguments.put(RBVDProperties.FIELD_ENDORSEMENT_EFF_END_DATE.getValue(), contractDao.getInsuranceContractEndDate());
        arguments.put(RBVDProperties.FIELD_POLICY_ENDORSEMENT_PER.getValue(), endosatarioPorcentaje);
        arguments.put(RBVDProperties.FIELD_REGISTRY_SITUATION_TYPE.getValue(), "01");
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), "SYSTEM");
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), "SYSTEM");

        return arguments;
    }

    public List<InsuranceCtrReceiptsDAO> buildInsuranceCtrReceipt(PolicyASO asoResponse, EmisionBO rimacResponse, PolicyDTO requestBody) {
        List<InsuranceCtrReceiptsDAO> receiptList = new ArrayList<>();

        InsuranceCtrReceiptsDAO firstReceipt = new InsuranceCtrReceiptsDAO();
        firstReceipt.setEntityId(asoResponse.getData().getId().substring(0, 4));
        firstReceipt.setBranchId(asoResponse.getData().getId().substring(4, 8));
        firstReceipt.setIntAccountId(asoResponse.getData().getId().substring(10));
        firstReceipt.setInsuranceCompanyId(new BigDecimal(requestBody.getInsuranceCompany().getId()));
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

        if(requestBody.getFirstInstallment().getIsPaymentRequired()) {
            String correctFormatDate = generateCorrectDateFormat(
                    convertDateToLocalDate(asoResponse.getData().getFirstInstallment().getOperationDate()));

            firstReceipt.setReceiptIssueDate(correctFormatDate);
            firstReceipt.setReceiptCollectionDate(correctFormatDate);
            firstReceipt.setReceiptsTransmissionDate(correctFormatDate);

            firstReceipt.setReceiptStatusType(FIRST_RECEIPT_STATUS_TYPE_VALUE);
        } else {
            firstReceipt.setReceiptIssueDate(RECEIPT_DEFAULT_DATE_VALUE);
            firstReceipt.setReceiptCollectionDate(RECEIPT_DEFAULT_DATE_VALUE);
            firstReceipt.setReceiptsTransmissionDate(RECEIPT_DEFAULT_DATE_VALUE);

            firstReceipt.setReceiptStatusType(NEXT_RECEIPTS_STATUS_TYPE_VALUE);
        }

        firstReceipt.setReceiptStartDate(RECEIPT_DEFAULT_DATE_VALUE);
        firstReceipt.setReceiptEndDate(RECEIPT_DEFAULT_DATE_VALUE);
        firstReceipt.setReceiptCollectionStatusType(COLLECTION_STATUS_FIRST_RECEIPT_VALUE);
        firstReceipt.setInsuranceCollectionMoveId(asoResponse.getData().getFirstInstallment().getTransactionNumber());
        firstReceipt.setPaymentMethodType(requestBody.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId().
                equals(CARD_PRODUCT_ID) ? CARD_METHOD_TYPE : ACCOUNT_METHOD_TYPE);
        firstReceipt.setDebitAccountId(requestBody.getPaymentMethod().getRelatedContracts().get(0).getContractId());
        firstReceipt.setDebitChannelType(requestBody.getSaleChannelId());
        firstReceipt.setCreationUserId(requestBody.getCreationUser());
        firstReceipt.setUserAuditId(requestBody.getUserAudit());
        firstReceipt.setManagementBranchId(requestBody.getBank().getBranch().getId());
        firstReceipt.setFixPremiumAmount(BigDecimal.valueOf(requestBody.getFirstInstallment().getPaymentAmount().getAmount()));
        firstReceipt.setSettlementFixPremiumAmount(BigDecimal.valueOf(requestBody.getTotalAmount().getAmount()));
        firstReceipt.setLastChangeBranchId(requestBody.getBank().getBranch().getId());
        firstReceipt.setGlBranchId(asoResponse.getData().getId().substring(4, 8));

        CuotaFinancimientoBO primeraCuota = rimacResponse.getPayload().getCuotasFinanciamiento().stream().
                filter(cuota -> cuota.getCuota() == 1).findFirst().orElse(null);
        if(Objects.nonNull(primeraCuota)) {
            firstReceipt.setPolicyReceiptId(BigDecimal.valueOf(primeraCuota.getCuota()));
            firstReceipt.setReceiptExpirationDate(generateCorrectDateFormat(primeraCuota.getFechaVencimiento()));
        }

        receiptList.add(firstReceipt);

        List<CuotaFinancimientoBO> siguientesCuotas = rimacResponse.getPayload().getCuotasFinanciamiento().stream().
                filter(cuota -> cuota.getCuota() != 1).collect(Collectors.toList());

        if(!isEmpty(siguientesCuotas)) {
            siguientesCuotas.forEach(cuota -> receiptList.add(createNextReceipt(firstReceipt, cuota)));
        }

        return receiptList;
    }

    private InsuranceCtrReceiptsDAO createNextReceipt(InsuranceCtrReceiptsDAO firstReceipt, CuotaFinancimientoBO cuota) {
        InsuranceCtrReceiptsDAO nextReceipt = new InsuranceCtrReceiptsDAO();

        nextReceipt.setEntityId(firstReceipt.getEntityId());
        nextReceipt.setBranchId(firstReceipt.getBranchId());
        nextReceipt.setIntAccountId(firstReceipt.getIntAccountId());
        nextReceipt.setInsuranceCompanyId(firstReceipt.getInsuranceCompanyId());
        nextReceipt.setPolicyReceiptId(BigDecimal.valueOf(cuota.getCuota()));
        nextReceipt.setPremiumPaymentReceiptAmount(BigDecimal.valueOf(0));
        nextReceipt.setFixingExchangeRateAmount(BigDecimal.valueOf(0));
        nextReceipt.setPremiumCurrencyExchAmount(BigDecimal.valueOf(0));
        nextReceipt.setCurrencyId(firstReceipt.getCurrencyId());
        nextReceipt.setReceiptIssueDate(RECEIPT_DEFAULT_DATE_VALUE);
        nextReceipt.setReceiptStartDate(firstReceipt.getReceiptStartDate());
        nextReceipt.setReceiptEndDate(firstReceipt.getReceiptEndDate());
        nextReceipt.setReceiptCollectionDate(RECEIPT_DEFAULT_DATE_VALUE);
        nextReceipt.setReceiptExpirationDate(generateCorrectDateFormat(cuota.getFechaVencimiento()));
        nextReceipt.setReceiptsTransmissionDate(RECEIPT_DEFAULT_DATE_VALUE);
        nextReceipt.setReceiptCollectionStatusType(COLLECTION_STATUS_NEXT_VALUES);
        nextReceipt.setPaymentMethodType(firstReceipt.getPaymentMethodType());
        nextReceipt.setDebitAccountId(firstReceipt.getDebitAccountId());
        nextReceipt.setReceiptStatusType(NEXT_RECEIPTS_STATUS_TYPE_VALUE);
        nextReceipt.setCreationUserId(firstReceipt.getCreationUserId());
        nextReceipt.setUserAuditId(firstReceipt.getUserAuditId());
        nextReceipt.setManagementBranchId(firstReceipt.getManagementBranchId());
        nextReceipt.setFixPremiumAmount(firstReceipt.getFixPremiumAmount());
        nextReceipt.setSettlementFixPremiumAmount(BigDecimal.valueOf(0));
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
        isrcContractMovDao.setGlAccountDate(generateCorrectDateFormat(asoResponse.getData().getFirstInstallment().getAccountingDate()));
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

    public Map<String, Object>[] createSaveParticipantArguments(List<IsrcContractParticipantDAO> participants) {
        Map<String, Object>[] participantsArguments = new HashMap[participants.size()];
        for(int i = 0; i < participants.size(); i++) {
            participantsArguments[i] = createParticipant(participants.get(i));
        }
        return participantsArguments;
    }

    private Map<String, Object> createParticipant(IsrcContractParticipantDAO participantDao) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), participantDao.getEntityId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(), participantDao.getBranchId());
        arguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), participantDao.getIntAccountId());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(), participantDao.getParticipantRoleId());
        arguments.put(RBVDProperties.FIELD_PARTY_ORDER_NUMBER.getValue(), participantDao.getPartyOrderNumber());
        arguments.put(RBVDProperties.FIELD_PERSONAL_DOC_TYPE.getValue(), participantDao.getPersonalDocType());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue(), participantDao.getParticipantPersonalId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ID.getValue(), participantDao.getCustomerId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_RELATIONSHIP_TYPE.getValue(), participantDao.getCustomerRelationshipType());
        arguments.put(RBVDProperties.FIELD_REGISTRY_SITUATION_TYPE.getValue(), participantDao.getRegistrySituationType());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), participantDao.getCreationUserId());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), participantDao.getUserAuditId());
        return arguments;
    }

    public void mappingOutputFields(PolicyDTO responseBody, PolicyASO asoResponse, EmisionBO rimacResponse, RequiredFieldsEmissionDAO requiredFields) {
        DataASO data = asoResponse.getData();

        responseBody.setId(data.getId());
        responseBody.setProductDescription(requiredFields.getInsuranceProductDesc());
        responseBody.getProductPlan().setDescription(requiredFields.getInsuranceModalityName());

        responseBody.setOperationDate(data.getOperationDate());

        responseBody.getValidityPeriod().setEndDate(convertLocaldateToDate(rimacResponse.getPayload().getFechaFinal()));

        responseBody.getInstallmentPlan().getPeriod().setName(requiredFields.getPaymentFrequencyName());

        int progressiveId = 0;

        for(ContactDetailDTO contactDetail : responseBody.getHolder().getContactDetails()) {
            contactDetail.setId(String.valueOf(++progressiveId));
        }

        progressiveId = 0;
        for(ContactDetailDTO contactDetail : responseBody.getInspection().getContactDetails()) {
            contactDetail.setId(String.valueOf(++progressiveId));
        }

        responseBody.getFirstInstallment().setFirstPaymentDate(convertLocaldateToDate(data.getFirstInstallment().getFirstPaymentDate()));

        if(responseBody.getFirstInstallment().getIsPaymentRequired()) {
            responseBody.getFirstInstallment().setOperationDate(
                convertLocaldateToDate(convertDateToLocalDate(data.getFirstInstallment().getOperationDate())));
            responseBody.getFirstInstallment().setAccountingDate(convertLocaldateToDate(data.getFirstInstallment().getAccountingDate()));
            responseBody.getFirstInstallment().setOperationNumber(data.getFirstInstallment().getOperationNumber());
            responseBody.getFirstInstallment().setTransactionNumber(data.getFirstInstallment().getTransactionNumber());

            responseBody.getFirstInstallment().setExchangeRate(validateExchangeRate(data.getFirstInstallment().getExchangeRate()));
            responseBody.getTotalAmount().setExchangeRate(validateExchangeRate(data.getTotalAmount().getExchangeRate()));
            responseBody.getInstallmentPlan().setExchangeRate(validateExchangeRate(data.getInstallmentPlan().getExchangeRate()));
        }

        for(int i = 0; i < responseBody.getParticipants().size(); i++) {
            for(ParticipantASO participant : data.getParticipants()) {
                if(responseBody.getParticipants().get(i).getIdentityDocument().getNumber()
                        .equals(participant.getIdentityDocument().getNumber())) {
                    responseBody.getParticipants().get(i).setId(participant.getId());
                    responseBody.getParticipants().get(i).setCustomerId(participant.getCustomerId());
                }
            }
            if(responseBody.getParticipants().get(i).getParticipantType().getId().equals(TAG_ENDORSEE))
                responseBody.getParticipants().get(i).setId("");
                responseBody.getParticipants().get(i).setCustomerId("");
        }

        responseBody.getInsuranceCompany().setName(data.getInsuranceCompany().getName());
        responseBody.getInsuranceCompany().setProductId(rimacResponse.getPayload().getCodProducto());

        responseBody.setExternalQuotationId(requiredFields.getInsuranceCompanyQuotaId());

        responseBody.setExternalPolicyNumber(rimacResponse.getPayload().getNumeroPoliza());

        QuotationStatusDTO status = new QuotationStatusDTO();
        status.setId(this.applicationConfigurationService.getProperty(data.getStatus().getDescription()));
        status.setDescription(data.getStatus().getDescription());

        responseBody.setStatus(status);

        responseBody.getHolder().getIdentityDocument().setDocumentNumber(responseBody.getHolder().getIdentityDocument().getNumber());
        responseBody.getHolder().getIdentityDocument().setNumber(null);

    }

    public CreateEmailASO buildCreateEmailRequestVeh(RequiredFieldsEmissionDAO emissionDao, PolicyDTO responseBody, String policyNumber){
        CreateEmailASO email = new CreateEmailASO();
        email.setApplicationId(TEMPLATE_EMAIL_CODE_VEH.concat(format.format(new Date())));
        email.setRecipient("0,".concat(responseBody.getHolder().getContactDetails().get(0).getContact().getAddress()));
        email.setSubject(SUBJECT_EMAIL_VEH);
        String[] data = getMailBodyDataVeh(emissionDao, responseBody, policyNumber);
        email.setBody(getEmailBodySructure1(data,TEMPLATE_EMAIL_CODE_VEH));
        email.setSender(MAIL_SENDER);
        Gson log = new Gson();
        LOGGER.info("arguments email Veh {}", log.toJson(email));
        return email;
    }

    public CreateEmailASO buildCreateEmailRequestHome(RequiredFieldsEmissionDAO emissionDao, PolicyDTO responseBody, String policyNumber, CustomerListASO customerInfo, SimltInsuredHousingDAO homeInfo, String riskDirection){
        CreateEmailASO email = new CreateEmailASO();
        email.setApplicationId(TEMPLATE_EMAIL_CODE_HOME.concat(format.format(new Date())));
        email.setRecipient("0,".concat(responseBody.getHolder().getContactDetails().get(0).getContact().getAddress()));
        email.setSubject(SUBJECT_EMAIL_HOME);
        String[] data = getMailBodyDataHome(emissionDao, responseBody, policyNumber, customerInfo, homeInfo, riskDirection);
        email.setBody(getEmailBodySructure2(data,TEMPLATE_EMAIL_CODE_HOME));
        email.setSender(MAIL_SENDER);
        Gson log = new Gson();
        LOGGER.info("arguments email Home {}", log.toJson(email));
        return email;
    }

    private ExchangeRateDTO validateExchangeRate(ExchangeRateASO exchangeRateASO) {
        ExchangeRateDTO exchangeRate = null;
        if(exchangeRateASO.getDetail().getFactor().getRatio() != 0) {
            exchangeRate = new ExchangeRateDTO();
            exchangeRate.setDate(convertLocaldateToDate(exchangeRateASO.getDate()));
            exchangeRate.setBaseCurrency(exchangeRateASO.getBaseCurrency());
            exchangeRate.setTargetCurrency(exchangeRateASO.getTargetCurrency());

            DetailDTO detail = new DetailDTO();

            FactorDTO factor = new FactorDTO();
            factor.setValue(exchangeRateASO.getDetail().getFactor().getValue());
            factor.setRatio(exchangeRateASO.getDetail().getFactor().getRatio());

            detail.setFactor(factor);
            detail.setPriceType(PRICE_TYPE_VALUE);

            exchangeRate.setDetail(detail);
        }
        return exchangeRate;
    }

    private String[] getMailBodyDataVeh(RequiredFieldsEmissionDAO emissionDao, PolicyDTO responseBody, String policyNumber) {
        String[] bodyData = new String[13];
        bodyData[0] = "";
        bodyData[1] = Objects.nonNull(emissionDao.getVehicleLicenseId()) ? emissionDao.getVehicleLicenseId() : "EN TRAMITE";
        bodyData[2] = emissionDao.getVehicleBrandName();
        bodyData[3] = emissionDao.getVehicleModelName();
        bodyData[4] = emissionDao.getVehicleYearId();
        bodyData[5] = emissionDao.getGasConversionType().equals("S") ? "Sí" : "No";
        bodyData[6] = emissionDao.getVehicleCirculationType().equals("L") ? "Lima" : "Provincia";
        Locale locale = new Locale ("en", "UK");
        NumberFormat numberFormat = NumberFormat.getInstance (locale);
        bodyData[7] = numberFormat.format(emissionDao.getCommercialVehicleAmount());
        bodyData[8] = getContractNumber(responseBody.getId());
        bodyData[9] = policyNumber;
        bodyData[10] = responseBody.getProductPlan().getDescription();

        PaymentAmountDTO paymentAmount = responseBody.getFirstInstallment().getPaymentAmount();

        bodyData[11] = USD_CURRENCY.concat(" ").concat(numberFormat.format(paymentAmount.getAmount()));
        bodyData[12] = emissionDao.getPaymentFrequencyName();
        return bodyData;
    }

    private String[] getMailBodyDataHome(RequiredFieldsEmissionDAO emissionDao, PolicyDTO responseBody, String policyNumber, CustomerListASO customerInfo, SimltInsuredHousingDAO homeInfo, String riskDirection) {
        String[] bodyData = new String[18];

        if("P".equals(homeInfo.getHousingType())) {
            bodyData[0] = setName(customerInfo);
            bodyData[1] = " de tu inmueble";
            bodyData[3] = "";
        }else{
            bodyData[0] = setName(customerInfo);
            bodyData[1] = " del inmueble que alquilas";
            bodyData[3] = NONE;
        }
        bodyData[2] = homeInfo.getDepartmentName().concat("/").concat(homeInfo.getProvinceName()).concat("/").concat(homeInfo.getDistrictName());
        bodyData[4] = homeInfo.getAreaPropertyNumber().toString();
        bodyData[5] = homeInfo.getPropSeniorityYearsNumber().toString();
        bodyData[6] = homeInfo.getFloorNumber().toString();

        if("05".equals(responseBody.getProductPlan().getId())) {
            bodyData[7] = "";
            bodyData[10] = NONE;
        }else if ("04".equals(responseBody.getProductPlan().getId())){
            bodyData[7] = NONE;
            bodyData[10] = "";
        }else{
            bodyData[7] = "";
            bodyData[10] = "";
        }

        bodyData[8] = PEN_CURRENCY;
        Locale locale = new Locale ("en", "UK");
        NumberFormat numberFormat = NumberFormat.getInstance (locale);
        bodyData[9] = Objects.nonNull(homeInfo.getEdificationLoanAmount()) ? numberFormat.format(homeInfo.getEdificationLoanAmount()) : "";
        bodyData[11] = Objects.nonNull(homeInfo.getHousingAssetsLoanAmount()) ? numberFormat.format(homeInfo.getHousingAssetsLoanAmount()) : "";
        bodyData[12] = getContractNumber(responseBody.getId());
        bodyData[13] = policyNumber;
        bodyData[14] = numberFormat.format(responseBody.getFirstInstallment().getPaymentAmount().getAmount());
        bodyData[15] = emissionDao.getPaymentFrequencyName();
        bodyData[16] = responseBody.getProductPlan().getDescription();
        bodyData[17] = riskDirection;

        return bodyData;
    }

    private String setName(CustomerListASO responseListCustomers){
        StringBuilder name = new StringBuilder();
        if(Objects.nonNull(responseListCustomers)) {
            name.append(responseListCustomers.getData().get(0).getFirstName()).append(" ").append(responseListCustomers.getData().get(0).getLastName()).append(" ")
                    .append(responseListCustomers.getData().get(0).getSecondLastName()).toString();
            return validateSN(name.toString());
        }
        return "";
    }


    private String getEmailBodySructure1(String[] data, String emailCode) {
        StringBuilder body = new StringBuilder();
        int hundredCode = 100;
        for(int i = 0; i < data.length; i++) {
            if(i > 7) {
                body.append(hundredCode).append(data[i]).append("|");
                hundredCode++;
                continue;
            }
            body.append(generateCode(i+1)).append(data[i]).append("|");
        }
        body.append(emailCode);
        return body.toString();
    }

    private String getEmailBodySructure2(String[] data, String emailCode) {
        StringBuilder body = new StringBuilder();
        for(int i = 0; i < data.length; i++) {
            body.append(generateCode(String.valueOf(i+1))).append(data[i]).append("|");
        }
        body.append(emailCode);
        return body.toString();
    }

    private String generateCode(String index) {
        StringBuilder code = new StringBuilder();
        int length = 3 - index.length();
        for (int i = 0; i < length; i++) {
            code.append("0");
        }
        code.append(index);
        return code.toString();
    }

    private String generateCode(Integer index) {
        return "00".concat(index.toString());
    }

    private String getContractNumber(String id) {
        StringBuilder contract = new StringBuilder();
        contract.append(id, 0, 4).append("-")
                .append(id, 4, 8).append("-")
                .append(id, 8, 10).append("-")
                .append(id.substring(10));
        return contract.toString();
    }

    private Date convertLocaldateToDate(LocalDate localDate) {
        return localDate.toDateTimeAtStartOfDay().toDate();
    }

    private LocalDate convertDateToLocalDate(Date date) {
        return new LocalDate(date, DateTimeZone.forID(GMT_TIME_ZONE));
    }

    private String generateCorrectDateFormat(LocalDate localDate) {
        String day = (localDate.getDayOfMonth() < 10) ? "0" + localDate.getDayOfMonth() : String.valueOf(localDate.getDayOfMonth());
        String month = (localDate.getMonthOfYear() < 10) ? "0" + localDate.getMonthOfYear() : String.valueOf(localDate.getMonthOfYear());
        return day + "/" + month + "/" + localDate.getYear();
    }

    public EmisionBO mapRimacEmisionRequest(EmisionBO rimacRequest,PolicyDTO requestBody, Map<String, Object> responseQueryGetRequiredFields, CustomerListASO customerList){
        EmisionBO generalEmisionRimacRequest = new EmisionBO();
        PayloadEmisionBO emisionBO = new PayloadEmisionBO();
        emisionBO.setEmision(rimacRequest.getPayload());
        emisionBO.getEmision().setProducto((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue()));
        generalEmisionRimacRequest.setPayload(emisionBO);

        FinanciamientoBO financiamiento = new FinanciamientoBO();
        financiamiento.setFrecuencia(this.applicationConfigurationService.getProperty(requestBody.getInstallmentPlan().getPeriod().getId()));
        String strDate = requestBody.getValidityPeriod().getStartDate().toInstant()
				.atOffset(ZoneOffset.UTC)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        financiamiento.setFechaInicio(strDate);
        financiamiento.setNumeroCuotas(requestBody.getInstallmentPlan().getTotalNumberInstallments());
        List<FinanciamientoBO> financiamientoBOs = new ArrayList<>();
        financiamientoBOs.add(financiamiento);
        CrearCronogramaBO crearCronogramaBO = new CrearCronogramaBO();
        crearCronogramaBO.setFinanciamiento(financiamientoBOs);

        generalEmisionRimacRequest.getPayload().setCrearCronograma(crearCronogramaBO);

        CustomerBO customer = customerList.getData().get(0);
		PersonaBO persona = new PersonaBO();
        List<PersonaBO> personasList = new ArrayList<>();
	    persona.setTipoDocumento(this.applicationConfigurationService.getProperty(customer.getIdentityDocuments().get(0).getDocumentType().getId()));
		persona.setNroDocumento(RUC_ID.equalsIgnoreCase(persona.getTipoDocumento())?(String)responseQueryGetRequiredFields.get(PISDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue()):customer.getIdentityDocuments().get(0).getDocumentNumber());
		persona.setApePaterno(customer.getLastName());
		persona.setApeMaterno(customer.getSecondLastName());
		persona.setNombres(customer.getFirstName());
		persona.setFechaNacimiento(customer.getBirthData().getBirthDate());
        if(Objects.nonNull(customer.getGender())) persona.setSexo("MALE".equals(customer.getGender().getId()) ? "M" : "F");
		persona.setCorreoElectronico((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue()));
		persona.setCelular((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue()));

        StringBuilder addressExtra  = new StringBuilder();

        fillAddress(customerList, persona, addressExtra);
        
        int[] intArray = new int[]{ 8,9,23 };
        for(int i=0; i<intArray.length; i++){
            PersonaBO personas =  this.getFillFieldsPerson(persona);
            personas.setRol(intArray[i]);
            personasList.add(personas);
        }
        AgregarPersonaBO agregarPersonaBO = new AgregarPersonaBO();
        agregarPersonaBO.setPersona(personasList);

        generalEmisionRimacRequest.getPayload().setAgregarPersona(agregarPersonaBO);
        Gson log = new Gson();
        LOGGER.info("generalEmisionRimacRequest output {}", log.toJson(generalEmisionRimacRequest));
        return generalEmisionRimacRequest;
    }

    private PersonaBO getFillFieldsPerson(PersonaBO persona) {
        PersonaBO persons = new PersonaBO();
        persons.setTipoDocumento(persona.getTipoDocumento());
        persons.setNroDocumento(persona.getNroDocumento());
        persons.setApePaterno(validateSN(persona.getApePaterno()));
        persons.setApeMaterno(validateSN(persona.getApeMaterno()));
        persons.setNombres(validateSN(persona.getNombres()));
        persons.setFechaNacimiento(persona.getFechaNacimiento());
        persons.setSexo(persona.getSexo());
        persons.setCorreoElectronico(persona.getCorreoElectronico());
        persons.setDireccion(validateSN(persona.getDireccion()));
        persons.setDistrito(validateSN(persona.getDistrito()));
        persons.setProvincia(validateSN(persona.getProvincia()));
        persons.setDepartamento(validateSN(persona.getDepartamento()));
        persons.setTipoVia(validateSN(persona.getTipoVia()));
        persons.setNombreVia(validateSN(persona.getNombreVia()));
        persons.setNumeroVia(validateSN(persona.getNumeroVia()));
        persons.setCelular(persona.getCelular());
        return persons;
    }

    private String validateSN(String name) {
        if(Objects.isNull(name) || "null".equals(name) || " ".equals(name)){
            return "N/A";
        }else{
            name = name.replace("#","Ñ");
            return name;
        }
    }

    private String fillAddress(CustomerListASO customerList, PersonaBO persona, StringBuilder addressExtra){
        boolean viaFull = false;
        String viaTipoNombre = null;
        StringBuilder additionalAddress2  = new StringBuilder();
        StringBuilder additionalAddress3  = new StringBuilder();
        CustomerBO customer = customerList.getData().get(0);
            for (int j = 0; j < customer.getAddresses().get(0).getLocation().getGeographicGroups().size(); j++) {
                String id = customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j)
                        .getGeographicGroupType().getId();
                if ("DISTRICT".equals(id)) {
                    persona.setDistrito(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j).getName());
                }
                if ("PROVINCE".equals(id)) {
                    persona.setProvincia(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j).getName());
                }
                if ("DEPARTMENT".equals(id)) {
                    persona.setDepartamento(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j).getName());
                }
                Map<String, String> map = tipeViaList();
                for (String clave:map.keySet()) {
                    String valor = map.get(clave);
                    if (clave.equals(id)&&!viaFull){
                        viaFull = true;
                        persona.setTipoVia(valor);
                        persona.setNombreVia(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j).getName());
                        viaTipoNombre = persona.getTipoVia().concat(" ").concat(persona.getNombreVia());
                    }
                }
                fillAddress2(persona,customer,j,viaFull,id, additionalAddress2, additionalAddress3);
                fillAddressExtra(addressExtra,customer,j);
            }

            persona.setDireccion(getFullDirectionFromCustomer(viaTipoNombre, additionalAddress2,
                    additionalAddress3, addressExtra, persona).trim());

            return viaTipoNombre;
    }

    private String getFullDirectionFromCustomer(String viaTipoNombre,
            StringBuilder additionalAddress2, StringBuilder additionalAddress3, StringBuilder addressExtra,
            PersonaBO persona) {
        String fullDirection = (Objects.nonNull(viaTipoNombre) ? viaTipoNombre.concat(" ") : "")
                .concat(Objects.nonNull(persona.getNumeroVia()) ? persona.getNumeroVia().concat(" ") : "")
                .concat(additionalAddress2.length() != 0 ? additionalAddress2.toString().concat(" ") : "")
                .concat(additionalAddress3.length() != 0 ? additionalAddress3.toString().concat(" ") : "")
                .concat(addressExtra.length() != 0 ? addressExtra.toString() : "");
        return fullDirection;
    }

    private void fillAddress2(PersonaBO persona, CustomerBO customer,int j,boolean viaFull,String id, StringBuilder additionalAddress2, StringBuilder additionalAddress3){
        if(Objects.nonNull(persona.getTipoVia())&&viaFull){
            Map<String, String> map = tipeViaList();
            for (String clave:map.keySet()) {
                String valor = map.get(clave);
                if (clave.equals(id)&&viaFull&&!valor.equals(persona.getTipoVia())){
                    String direction2 = valor+" "+customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j)
                            .getName();
                    additionalAddress2.append(direction2);
                }
            }

        }

        String direction3 = fillAddress3(persona,customer,j,id);
        if(Objects.nonNull(direction3)){
                additionalAddress3.append(direction3);
        }

        if ("EXTERIOR_NUMBER".equals(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j)
                .getGeographicGroupType().getId())) {
            persona.setNumeroVia(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j)
                    .getName());
        }
}

private String fillAddress3(PersonaBO persona, CustomerBO customer,int j,String id){
    String address3 = null;
    Map<String, String> maps = tipeViaList2();
    for (String clave:maps.keySet()) {
        String valor = maps.get(clave);
        if (clave.equals(id)&&!valor.equals(persona.getTipoVia())){
            address3 = valor+" "+customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j)
                    .getName().concat(" ");
        }
    }
    return address3;
}

private void fillAddressExtra(StringBuilder addressExtra, CustomerBO customer,int j){
    if ("BLOCK".equals(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j)
            .getGeographicGroupType().getId())) {
        addressExtra.append(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j)
                .getName()).append(" ");

    }
    if ("LOT".equals(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j)
            .getGeographicGroupType().getId())) {
        addressExtra.append(customer.getAddresses().get(0).getLocation().getGeographicGroups().get(j)
                .getName());
    }
}

private Map<String, String> tipeViaList(){
    Map<String, String> map = new HashMap<>();
    map.put("ALAMEDA", "ALM");
    map.put("AVENUE", "AV.");
    map.put("STREET", "CAL");
    map.put("MALL", "CC.");
    map.put("ROAD", "CRT");
    map.put("SHOPPING_ARCADE", "GAL");
    map.put("JIRON", "JR.");
    map.put("JETTY", "MAL");
    map.put("OVAL", "OVA");
    map.put("PEDESTRIAN_WALK", "PAS");
    map.put("SQUARE", "PLZ");
    map.put("PARK", "PQE");
    map.put("PROLONGATION", "PRL");
    map.put("PASSAGE", "PSJ");
    map.put("BRIDGE", "PTE");
    map.put("DESCENT", "BAJ");
    map.put("PORTAL", "POR");
    map.put("GROUP", "AGR");
    map.put("AAHH", "AHH");
    map.put("HOUSING_COMPLEX", "CHB");
    map.put("HOUSING_COOPERATIVE", "COV");
    map.put("STAGE", "ETP");
    map.put("SHANTYTOWN", "PJJ");
    map.put("NEIGHBORHOOD", "SEC");
    map.put("URBANIZATION", "URB");
    map.put("NEIGHBORHOOD_UNIT", "UV.");
    map.put("ZONE", "ZNA");
    map.put("ASSOCIATION", "ASC");
    map.put("INDIGENOUS_COMMUNITY", "COM");
    map.put("PEASANT_COMMUNITY", "CAM");
    map.put("FUNDO", "FUN");
    map.put("MINING_CAMP", "MIN");
    map.put("RESIDENTIAL", "RES");
    return map;
}

private Map<String, String> tipeViaList2() {
    Map<String, String> map = new HashMap<>();
    map.put("UNCATEGORIZED", "NA");
    map.put("NOT_PROVIDED", "NP");
    return map;
}

    public GifoleInsuranceRequestASO createGifoleRequest(PolicyDTO responseBody, CustomerListASO responseListCustomers){
        GifoleInsuranceRequestASO gifoleResponse = new GifoleInsuranceRequestASO();
        QuotationASO quotationASO = new QuotationASO();
        quotationASO.setId(responseBody.getQuotationId());
        gifoleResponse.setQuotation(quotationASO);
        gifoleResponse.setChannel(responseBody.getAap());
        DateTime currentDate = new DateTime(new Date(), DATE_TIME_ZONE);
        gifoleResponse.setOperationDate(currentDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        gifoleResponse.setOperationType(INSURANCE_GIFOLE_VAL);
        String startDate = responseBody.getValidityPeriod().getStartDate().toInstant()
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDate = responseBody.getValidityPeriod().getEndDate().toInstant()
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        com.bbva.pisd.dto.insurance.aso.gifole.ValidityPeriodASO validityPeriodASO = new com.bbva.pisd.dto.insurance.aso.gifole.ValidityPeriodASO(startDate, endDate);
        gifoleResponse.setValidityPeriod(validityPeriodASO);
        InsuranceASO insuranceASO = new InsuranceASO();
        insuranceASO.setId(responseBody.getId());

        List<com.bbva.pisd.dto.insurance.aso.gifole.RelatedContractASO> relatedContractASOs = new ArrayList<>();
        for(RelatedContractDTO contract : responseBody.getPaymentMethod().getRelatedContracts()){
            com.bbva.pisd.dto.insurance.aso.gifole.RelatedContractASO relatedContractASO = new com.bbva.pisd.dto.insurance.aso.gifole.RelatedContractASO();
            int beginIndex = contract.getNumber().length() - 4;
            relatedContractASO.setNumber(MASK_VALUE.concat(contract.getNumber().substring(beginIndex)));
            relatedContractASOs.add(relatedContractASO);
        }

        com.bbva.pisd.dto.insurance.aso.gifole.PaymentMethodASO paymentMethodASO = new com.bbva.pisd.dto.insurance.aso.gifole.PaymentMethodASO();
        paymentMethodASO.setId(responseBody.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId()
                .equals(CARD_PRODUCT_ID) ? CARD_PRODUCT_NAME : ACCOUNT_PRODUCT_NAME);
        paymentMethodASO.setRelatedContracts(relatedContractASOs);
        insuranceASO.setPaymentMethod(paymentMethodASO);
        gifoleResponse.setInsurance(insuranceASO);
        gifoleResponse.setPolicyNumber(responseBody.getExternalPolicyNumber());

        ProductASO productASO = new ProductASO();
        PlanASO planASO = new PlanASO();
        planASO.setId(responseBody.getProductPlan().getId());
        planASO.setName(responseBody.getProductPlan().getDescription());
        productASO.setPlan(planASO);
        productASO.setId(responseBody.getProductId());
        productASO.setName(responseBody.getProductDescription());
        gifoleResponse.setProduct(productASO);

        com.bbva.pisd.dto.insurance.aso.gifole.HolderASO holderASO = new com.bbva.pisd.dto.insurance.aso.gifole.HolderASO();
        if(Objects.nonNull(responseListCustomers)) {
            CustomerBO customer = responseListCustomers.getData().get(0);
            holderASO.setFirstName(customer.getFirstName());
            holderASO.setLastName(customer.getLastName().concat(" ").concat(customer.getSecondLastName()));
        }else{
            holderASO.setFirstName("");
            holderASO.setLastName("");
        }

        holderASO.setIsBankCustomer(true);
        holderASO.setIsDataTreatment(true);

        if(responseBody.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId().equals(CARD_PRODUCT_ID)){
            holderASO.setHasCreditCard(true);
            holderASO.setHasBankAccount(false);
        }else{
            holderASO.setHasBankAccount(true);
            holderASO.setHasCreditCard(false);
        }

        com.bbva.pisd.dto.insurance.aso.gifole.DocumentTypeASO documentTypeASO = new com.bbva.pisd.dto.insurance.aso.gifole.DocumentTypeASO();
        documentTypeASO.setId(responseBody.getHolder().getIdentityDocument().getDocumentType().getId());
        com.bbva.pisd.dto.insurance.aso.gifole.IdentityDocumentASO identityDocumentASO = new com.bbva.pisd.dto.insurance.aso.gifole.IdentityDocumentASO();
        identityDocumentASO.setDocumentType(documentTypeASO);
        identityDocumentASO.setDocumentNumber(responseBody.getHolder().getIdentityDocument().getDocumentNumber());
        holderASO.setIdentityDocument(identityDocumentASO);
        List<ContactDetailASO> contactDetailASOs = new ArrayList<>();
        ContactDetailASO contactDetailASO1 = new ContactDetailASO();
        ContactDetailASO contactDetailASO2 = new ContactDetailASO();
        ContactASO contactASO2 = new ContactASO();
        contactASO2.setContactType(EMAIL_VALUE);
        contactASO2.setAddress(responseBody.getHolder().getContactDetails().get(0).getContact().getAddress());
        contactDetailASO2.setContact(contactASO2);
        ContactASO contactASO1 = new ContactASO();
        contactASO1.setContactType(PHONE_NUMBER_VALUE);
        contactASO1.setPhoneNumber(responseBody.getHolder().getContactDetails().get(1).getContact().getPhoneNumber());
        contactDetailASO1.setContact(contactASO1);
        contactDetailASOs.add(contactDetailASO1);
        contactDetailASOs.add(contactDetailASO2);
        holderASO.setContactDetails(contactDetailASOs);
        gifoleResponse.setHolder(holderASO);

        com.bbva.pisd.dto.insurance.aso.gifole.InstallmentPlanASO installmentPlanASO = new com.bbva.pisd.dto.insurance.aso.gifole.InstallmentPlanASO();
        PeriodASO periodASO = new PeriodASO();
        periodASO.setId(responseBody.getInstallmentPlan().getPeriod().getId());
        periodASO.setName(responseBody.getInstallmentPlan().getPeriod().getName());
        installmentPlanASO.setTotalInstallmentsNumber(responseBody.getInstallmentPlan().getTotalNumberInstallments());
        installmentPlanASO.setPeriod(periodASO);
        gifoleResponse.setInstallmentPlan(installmentPlanASO);
        
        Gson log = new Gson();
        LOGGER.info("GifoleResponse output {}", log.toJson(gifoleResponse));

        return gifoleResponse;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

}
