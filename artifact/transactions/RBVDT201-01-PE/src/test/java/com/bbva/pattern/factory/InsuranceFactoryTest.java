package com.bbva.pattern.factory;

import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.RBVDR211;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InsuranceFactoryTest {

    @Mock
    private RBVDR211 rbvdR211;

    @Test
    public void createInsuranceBankAndCompanyReturnsLifeProductFlowForLifeProduct() {
        // Given
        String typeProduct = "lifeProduct";
        List<String> listOfLifeProductCodes = Arrays.asList("lifeProduct");
        PolicyDTO requestBody = new PolicyDTO();
        ResponseLibrary<PolicyDTO> expectedResponse = ResponseLibrary.ResponseServiceBuilder.an().build();
        when(rbvdR211.executeEmissionPrePolicyLifeProductFlowNew(requestBody)).thenReturn(expectedResponse);
        InsuranceFactory insuranceFactory = new InsuranceFactory();
        // When
        ResponseLibrary<PolicyDTO> result = insuranceFactory.createInsuranceBankAndCompany(typeProduct, rbvdR211, listOfLifeProductCodes, requestBody);

        // Then
        assertEquals(expectedResponse, result);
    }

    @Test
    public void createInsuranceBankAndCompanyReturnsNotLifeFlowForNonLifeProduct() {
        // Given
        String typeProduct = "nonLifeProduct";
        List<String> listOfLifeProductCodes = Arrays.asList("lifeProduct");
        PolicyDTO requestBody = new PolicyDTO();
        ResponseLibrary<PolicyDTO> expectedResponse = ResponseLibrary.ResponseServiceBuilder.an().build();
        when(rbvdR211.executeEmissionPolicyNotLifeFlowNew(requestBody)).thenReturn(expectedResponse);
        InsuranceFactory insuranceFactory = new InsuranceFactory();
        // When
        ResponseLibrary<PolicyDTO> result = insuranceFactory.createInsuranceBankAndCompany(typeProduct, rbvdR211, listOfLifeProductCodes, requestBody);

        // Then
        assertEquals(expectedResponse, result);
    }


}