package com.bbva.rbvd.lib.r201.util;

import com.bbva.rbvd.dto.insrncsale.aso.*;
import com.bbva.rbvd.dto.insrncsale.aso.emision.*;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BankASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BranchASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BusinessAgentASO;
import com.google.gson.*;
import org.joda.time.LocalDate;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class JsonHelper {

    private static final String DATE = "yyyy-MM-dd";
    private static final String PURCHASE = "PURCHASE";
    private static final String MONTHLY = "MONTHLY";
    private static final String HOLDER_ID = "97789740";
    private static final String USD = "USD";
    private static final JsonHelper INSTANCE = new JsonHelper();

    private final Gson gson;

    private JsonHelper() {
        gson = new GsonBuilder()
                .setDateFormat(DATE)
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeHierarchyAdapter(Calendar.class, new CalendarAdapter())
                .create();
    }

    public PolicyASO createMockPolicyASO() {
        PolicyASO policyASO = new PolicyASO();

        DataASO policyData = new DataASO();
        policyData.setId("00110135104001261609");
        policyData.setProductId("830");
        policyData.setProductDescription("SEG.VEHICULAR ROYAL OPTATIVO");
        policyData.setProductPlan(new ProductPlanASO());
        policyData.setDescription("PLAN 3 FULL COBERTURAS ROYAL");

        PaymentMethodASO paymentMethod = new PaymentMethodASO();
        paymentMethod.setPaymentType("DIRECT_DEBIT");
        paymentMethod.setInstallmentFrequency(MONTHLY);
        paymentMethod.setRelatedContracts(Collections.singletonList(new RelatedContractASO()));
        paymentMethod.getRelatedContracts().get(0).setNumber("00110130270299963079");
        paymentMethod.getRelatedContracts().get(0).setProduct(new RelatedContractProductASO());
        paymentMethod.getRelatedContracts().get(0).getProduct().setId("ACCOUNT");

        policyData.setOperationDate(new Date());

        policyData.setValidityPeriod(new ValidityPeriodASO());
        policyData.getValidityPeriod().setStartDate(new LocalDate());
        policyData.getValidityPeriod().setEndDate(new LocalDate().plusYears(1));

        policyData.setTotalAmount(new TotalAmountASO());
        policyData.getTotalAmount().setCurrency(USD);
        policyData.getTotalAmount().setAmount(205.2);
        policyData.getTotalAmount().setExchangeRate(new ExchangeRateASO());
        policyData.getTotalAmount().getExchangeRate().setDate(new LocalDate());
        policyData.getTotalAmount().getExchangeRate().setBaseCurrency(USD);
        policyData.getTotalAmount().getExchangeRate().setTargetCurrency(USD);
        policyData.getTotalAmount().getExchangeRate().setDetail(new DetailASO());
        policyData.getTotalAmount().getExchangeRate().getDetail().setFactor(new FactorASO());
        policyData.getTotalAmount().getExchangeRate().getDetail().getFactor().setValue(0.0);
        policyData.getTotalAmount().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        policyData.getTotalAmount().getExchangeRate().getDetail().setPriceType(PURCHASE);

        policyData.setInsuredAmount(new InsuredAmountASO());
        policyData.getInsuredAmount().setCurrency(USD);
        policyData.getInsuredAmount().setAmount(30000.0);
        policyData.setHolder(new HolderASO());
        policyData.getHolder().setId(HOLDER_ID);
        policyData.getHolder().setIdentityDocument(new IdentityDocumentASO());
        policyData.getHolder().getIdentityDocument().setDocumentType(new DocumentTypeASO());
        policyData.getHolder().getIdentityDocument().getDocumentType().setId("DNI");
        policyData.getHolder().getIdentityDocument().setNumber("04040005");

        policyData.setInstallmentPlan(new InstallmentPlanASO());
        policyData.getInstallmentPlan().setStartDate(new LocalDate());
        policyData.getInstallmentPlan().setTotalNumberInstallments(1L);
        policyData.getInstallmentPlan().setPeriod(new PaymentPeriodASO());
        policyData.getInstallmentPlan().getPeriod().setId(MONTHLY);
        policyData.getInstallmentPlan().getPeriod().setName(MONTHLY);
        policyData.getInstallmentPlan().setPaymentAmount(new PaymentAmountASO());
        policyData.getInstallmentPlan().setExchangeRate(new ExchangeRateASO());
        policyData.getInstallmentPlan().getExchangeRate().setDate(new LocalDate());
        policyData.getInstallmentPlan().getExchangeRate().setBaseCurrency(USD);
        policyData.getInstallmentPlan().getExchangeRate().setTargetCurrency(USD);
        policyData.getInstallmentPlan().getExchangeRate().setDetail(new DetailASO());
        policyData.getInstallmentPlan().getExchangeRate().getDetail().setFactor(new FactorASO());
        policyData.getInstallmentPlan().getExchangeRate().getDetail().getFactor().setValue(0.0);
        policyData.getInstallmentPlan().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        policyData.getInstallmentPlan().getExchangeRate().getDetail().setPriceType(PURCHASE);


        policyData.setFirstInstallment(new FirstInstallmentASO());
        policyData.getFirstInstallment().setFirstPaymentDate(new LocalDate());
        policyData.getFirstInstallment().setIsPaymentRequired(true);
        policyData.getFirstInstallment().setExchangeRate(new ExchangeRateASO());
        policyData.getFirstInstallment().getExchangeRate().setDate(new LocalDate());
        policyData.getFirstInstallment().getExchangeRate().setBaseCurrency(USD);
        policyData.getFirstInstallment().getExchangeRate().setTargetCurrency(USD);
        policyData.getFirstInstallment().getExchangeRate().setDetail(new DetailASO());
        policyData.getFirstInstallment().getExchangeRate().getDetail().setFactor(new FactorASO());
        policyData.getFirstInstallment().getExchangeRate().getDetail().getFactor().setValue(0.0);
        policyData.getFirstInstallment().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        policyData.getFirstInstallment().getExchangeRate().getDetail().setPriceType(PURCHASE);


        policyData.setParticipants(Collections.singletonList(new ParticipantASO()));
        policyData.getParticipants().get(0).setId(HOLDER_ID);
        policyData.getParticipants().get(0).setIdentityDocument(new IdentityDocumentASO());
        policyData.getParticipants().get(0).getIdentityDocument().setDocumentType(new DocumentTypeASO());
        policyData.getParticipants().get(0).getIdentityDocument().getDocumentType().setId("DNI");
        policyData.getParticipants().get(0).getIdentityDocument().setNumber("04040005");
        policyData.getParticipants().get(0).setCustomerId(HOLDER_ID);

        policyData.setBusinessAgent(new BusinessAgentASO());
        policyData.getBusinessAgent().setId("121329");

        policyData.setPromoter(new PromoterASO());
        policyData.getPromoter().setId("121329");

        policyData.setInsuranceCompany(new InsuranceCompanyASO());
        policyData.getInsuranceCompany().setId("01");
        policyData.getInsuranceCompany().setName("RÍMAC");

        policyData.setStatus(new StatusASO());
        policyData.getStatus().setId("01");
        policyData.getStatus().setDescription("FORMALIZADO");


        policyData.setBank(new BankASO());
        policyData.getBank().setId("0011");
        policyData.getBank().setBranch(new BranchASO());
        policyData.getBank().getBranch().setId("0135");


        // Establecer los datos en la política ASO
        policyASO.setData(policyData);
        policyData.setPaymentMethod(paymentMethod);

        return policyASO;
    }

    public static JsonHelper getInstance() { return INSTANCE; }

    public <T> T fromString(String src, Class<T> clazz) { return this.gson.fromJson(src, clazz); }

    public String toJsonString(Object o) { return this.gson.toJson(o); }

}

class LocalDateAdapter implements JsonSerializer<LocalDate> {

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

}

class CalendarAdapter implements JsonSerializer<Calendar> {

    @Override
    public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return new JsonPrimitive(dateFormat.format(src.getTime()));
    }

}
