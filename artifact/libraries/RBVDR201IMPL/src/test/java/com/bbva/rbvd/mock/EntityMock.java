package com.bbva.rbvd.mock;


import com.bbva.rbvd.dto.cicsconnection.icr2.ICMRYS2;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;


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

        // Crear datos para la política
        DataASO policyData = new DataASO();
        policyData.setId("POLICY123"); // ID de ejemplo

        // Crear método de pago
        PaymentMethodASO paymentMethod = new PaymentMethodASO();
        paymentMethod.setPaymentType("CREDIT_CARD"); // Tipo de pago de ejemplo
        paymentMethod.setRelatedContracts(Collections.singletonList(new RelatedContractASO()));
        paymentMethod.getRelatedContracts().get(0).setNumber("01230192830129830128");// Contratos relacionados

        // Crear primera cuota
        FirstInstallmentASO firstInstallment = new FirstInstallmentASO();
        firstInstallment.setIsPaymentRequired(true); // Ejemplo de si se requiere pago

        // Crear banco
        BankASO bank = new BankASO();
        BranchASO branch = new BranchASO();
        branch.setId("BRANCH456"); // ID de la sucursal de ejemplo
        bank.setBranch(branch);

        // Establecer los datos en la política ASO
        policyASO.setData(policyData);
        policyData.setPaymentMethod(paymentMethod);
        policyData.setFirstInstallment(firstInstallment);
        policyData.setBank(bank);

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
