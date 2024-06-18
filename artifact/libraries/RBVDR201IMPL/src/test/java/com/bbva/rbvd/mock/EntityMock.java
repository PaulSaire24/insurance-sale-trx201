package com.bbva.rbvd.mock;


import com.bbva.rbvd.dto.cicsconnection.icr2.ICMRYS2;
import com.bbva.rbvd.dto.insrncsale.aso.*;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BankASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BranchASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BusinessAgentASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;


public final class EntityMock {

    public static final String INSURANCE_ID = "123";
    public static final String HEADER_BCS_OPERATION_TRACER = "12343-2123123--asd12j3oi";
    public static final String PRODUCT_ID_BACKEND = "C";

    private static final EntityMock INSTANCE = new EntityMock();
    private ObjectMapper objectMapper;

    private EntityMock() {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }


    public static EntityMock getInstance() {
        return INSTANCE;
    }


    public PolicyASO createMockPolicyASO() {
        PolicyASO policyASO = new PolicyASO();

        DataASO policyData = new DataASO();
        policyData.setId("00110135104001261609");
        policyData.setProductId("841");
        policyData.setProductDescription("SEGURO OPTATIVO RESPALDO TOTAL");
        policyData.setProductPlan(new ProductPlanASO());
        policyData.setDescription("PLAN SIN DEVOLUCIÓN RESPALDO TOTAL");

        PaymentMethodASO paymentMethod = new PaymentMethodASO();
        paymentMethod.setPaymentType("DIRECT_DEBIT");
        paymentMethod.setInstallmentFrequency("ANNUAL");
        paymentMethod.setRelatedContracts(Collections.singletonList(new RelatedContractASO()));
        paymentMethod.getRelatedContracts().get(0).setNumber("00110130270299963079");
        paymentMethod.getRelatedContracts().get(0).setProduct(new RelatedContractProductASO());
        paymentMethod.getRelatedContracts().get(0).getProduct().setId("ACCOUNT");

        policyData.setOperationDate(new Date());

        policyData.setValidityPeriod(new ValidityPeriodASO());
        policyData.getValidityPeriod().setStartDate(new LocalDate());
        policyData.getValidityPeriod().setEndDate(new LocalDate().plusYears(1));

        policyData.setTotalAmount(new TotalAmountASO());
        policyData.getTotalAmount().setCurrency("PEN");
        policyData.getTotalAmount().setAmount(205.2);
        policyData.getTotalAmount().setExchangeRate(new ExchangeRateASO());
        policyData.getTotalAmount().getExchangeRate().setDate(new LocalDate());
        policyData.getTotalAmount().getExchangeRate().setBaseCurrency("PEN");
        policyData.getTotalAmount().getExchangeRate().setTargetCurrency("PEN");
        policyData.getTotalAmount().getExchangeRate().setDetail(new DetailASO());
        policyData.getTotalAmount().getExchangeRate().getDetail().setFactor(new FactorASO());
        policyData.getTotalAmount().getExchangeRate().getDetail().getFactor().setValue(0.0);
        policyData.getTotalAmount().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        policyData.getTotalAmount().getExchangeRate().getDetail().setPriceType("PURCHASE");

        policyData.setInsuredAmount(new InsuredAmountASO());
        policyData.getInsuredAmount().setCurrency("PEN");
        policyData.getInsuredAmount().setAmount(30000.0);
        policyData.setHolder(new HolderASO());
        policyData.getHolder().setId("97789740");
        policyData.getHolder().setIdentityDocument(new IdentityDocumentASO());
        policyData.getHolder().getIdentityDocument().setDocumentType(new DocumentTypeASO());
        policyData.getHolder().getIdentityDocument().getDocumentType().setId("DNI");
        policyData.getHolder().getIdentityDocument().setNumber("04040005");

        policyData.setInstallmentPlan(new InstallmentPlanASO());
        policyData.getInstallmentPlan().setStartDate(new LocalDate());
        policyData.getInstallmentPlan().setTotalNumberInstallments(1L);
        policyData.getInstallmentPlan().setPeriod(new PaymentPeriodASO());
        policyData.getInstallmentPlan().getPeriod().setId("ANNUAL");
        policyData.getInstallmentPlan().getPeriod().setName("ANNUAL");
        policyData.getInstallmentPlan().setPaymentAmount(new PaymentAmountASO());
        policyData.getInstallmentPlan().setExchangeRate(new ExchangeRateASO());
        policyData.getInstallmentPlan().getExchangeRate().setDate(new LocalDate());
        policyData.getInstallmentPlan().getExchangeRate().setBaseCurrency("PEN");
        policyData.getInstallmentPlan().getExchangeRate().setTargetCurrency("PEN");
        policyData.getInstallmentPlan().getExchangeRate().setDetail(new DetailASO());
        policyData.getInstallmentPlan().getExchangeRate().getDetail().setFactor(new FactorASO());
        policyData.getInstallmentPlan().getExchangeRate().getDetail().getFactor().setValue(0.0);
        policyData.getInstallmentPlan().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        policyData.getInstallmentPlan().getExchangeRate().getDetail().setPriceType("PURCHASE");


        policyData.setFirstInstallment(new FirstInstallmentASO());
        policyData.getFirstInstallment().setFirstPaymentDate(new LocalDate());
        policyData.getFirstInstallment().setIsPaymentRequired(true);
        policyData.getFirstInstallment().setExchangeRate(new ExchangeRateASO());
        policyData.getFirstInstallment().getExchangeRate().setDate(new LocalDate());
        policyData.getFirstInstallment().getExchangeRate().setBaseCurrency("PEN");
        policyData.getFirstInstallment().getExchangeRate().setTargetCurrency("PEN");
        policyData.getFirstInstallment().getExchangeRate().setDetail(new DetailASO());
        policyData.getFirstInstallment().getExchangeRate().getDetail().setFactor(new FactorASO());
        policyData.getFirstInstallment().getExchangeRate().getDetail().getFactor().setValue(0.0);
        policyData.getFirstInstallment().getExchangeRate().getDetail().getFactor().setRatio(0.0);
        policyData.getFirstInstallment().getExchangeRate().getDetail().setPriceType("PURCHASE");


        policyData.setParticipants(Collections.singletonList(new ParticipantASO()));
        policyData.getParticipants().get(0).setId("97789740");
        policyData.getParticipants().get(0).setIdentityDocument(new IdentityDocumentASO());
        policyData.getParticipants().get(0).getIdentityDocument().setDocumentType(new DocumentTypeASO());
        policyData.getParticipants().get(0).getIdentityDocument().getDocumentType().setId("DNI");
        policyData.getParticipants().get(0).getIdentityDocument().setNumber("04040005");
        policyData.getParticipants().get(0).setCustomerId("97789740");

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

    public DataASO buildInsurance() throws IOException {
        return objectMapper.readValue(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("mock/createInsurance/insurance.json"), DataASO.class);
    }

    public DataASO buildInputCreateInsurance() throws IOException {
        return objectMapper.readValue(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("mock/createInsurance/InputCreateInsurance.json"), DataASO.class);
    }

    public DataASO buildInputCreateInsuranceLegal() throws IOException {
        return objectMapper.readValue(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("mock/createInsurance/insuranceLegal.json"), DataASO.class);
    }

    public DataASO buildInputCreateInsurance_supportArrayList() throws IOException {
        return objectMapper.readValue(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("mock/createInsurance/insurance_supportArray.json"), DataASO.class);
    }

    public ICMRYS2 buildFormatoICMRYS2() throws IOException {
        return objectMapper.readValue(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("mock/icr2/formatoICMRYS2.json"), ICMRYS2.class);
    }

}
