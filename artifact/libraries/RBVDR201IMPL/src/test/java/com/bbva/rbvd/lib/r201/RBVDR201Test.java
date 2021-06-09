package com.bbva.rbvd.lib.r201;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.lib.r201.factory.ApiConnectorFactoryMock;
import com.bbva.rbvd.lib.r201.impl.RBVDR201Impl;
import com.bbva.rbvd.mock.MockBundleContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR201-app.xml",
		"classpath:/META-INF/spring/RBVDR201-app-test.xml",
		"classpath:/META-INF/spring/RBVDR201-arc.xml",
		"classpath:/META-INF/spring/RBVDR201-arc-test.xml" })
public class RBVDR201Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR201Test.class);

	private static final String MESSAGE_EXCEPTION = "CONNECTION ERROR";

	private RBVDR201Impl rbvdr201 = new RBVDR201Impl();

	private PISDR014 pisdr014;

	private MockData mockData;
	private APIConnector internalApiConnector;
	private APIConnector externalApiConnector;

	@Before
	public void setUp() {
		ThreadContext.set(new Context());

		mockData = MockData.getInstance();

		MockBundleContext mockBundleContext = mock(MockBundleContext.class);

		ApiConnectorFactoryMock apiConnectorFactoryMock = new ApiConnectorFactoryMock();
		internalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext);
		rbvdr201.setInternalApiConnector(internalApiConnector);

		externalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext, false);
		rbvdr201.setExternalApiConnector(externalApiConnector);

		pisdr014 = mock(PISDR014.class);
		rbvdr201.setPisdR014(pisdr014);

		when(pisdr014.executeSignatureConstruction(anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(new SignatureAWS("", "", "", ""));
	}

	@Test
	public void executePrePolicyEmissionASOWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionASOWithRestClientException...");

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		PolicyASO validation = rbvdr201.executePrePolicyEmissionASO(new PolicyDTO());

		assertNull(validation);
	}

	@Test
	public void executePrePolicyEmissionASO_OK() throws IOException {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionASO_OK...");

		PolicyASO responseBody = mockData.getEmisionASOResponse();

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).thenReturn(responseBody);

		PolicyASO validation = rbvdr201.executePrePolicyEmissionASO(new PolicyDTO());

		assertNotNull(validation);
		assertNotNull(validation.getData());
		assertNotNull(validation.getData().getId());
		assertNotNull(validation.getData().getPolicyNumber());
		assertNotNull(validation.getData().getProductId());
		assertNotNull(validation.getData().getProductDescription());
		assertNotNull(validation.getData().getProductPlan());
		assertNotNull(validation.getData().getProductPlan().getId());
		assertNotNull(validation.getData().getProductPlan().getDescription());
		assertNotNull(validation.getData().getPaymentMethod());
		assertNotNull(validation.getData().getPaymentMethod().getPaymentType());
		assertNotNull(validation.getData().getPaymentMethod().getInstallmentFrequency());
		assertNotNull(validation.getData().getPaymentMethod().getRelatedContracts());
		assertFalse(validation.getData().getPaymentMethod().getRelatedContracts().isEmpty());
		assertNotNull(validation.getData().getOperationDate());
		assertNotNull(validation.getData().getValidityPeriod());
		assertNotNull(validation.getData().getValidityPeriod().getStartDate());
		assertNotNull(validation.getData().getValidityPeriod().getEndDate());
		assertNotNull(validation.getData().getTotalAmount());
		assertNotNull(validation.getData().getTotalAmount().getAmount());
		assertNotNull(validation.getData().getTotalAmount().getCurrency());
		assertNotNull(validation.getData().getInsuredAmount());
		assertNotNull(validation.getData().getInsuredAmount().getAmount());
		assertNotNull(validation.getData().getInsuredAmount().getCurrency());
		assertNotNull(validation.getData().getHolder());
		assertNotNull(validation.getData().getHolder().getId());
		assertNotNull(validation.getData().getHolder().getIdentityDocument());
		assertNotNull(validation.getData().getHolder().getIdentityDocument().getDocumentType());
		assertNotNull(validation.getData().getHolder().getIdentityDocument().getDocumentType().getId());
		assertNotNull(validation.getData().getHolder().getIdentityDocument().getDocumentType().getDescription());
		assertNotNull(validation.getData().getHolder().getIdentityDocument().getNumber());
		assertNotNull(validation.getData().getInstallmentPlan());
		assertNotNull(validation.getData().getFirstInstallment());
		assertNotNull(validation.getData().getInsuranceCompany());
		assertNotNull(validation.getData().getInsuranceCompany().getId());
		assertNotNull(validation.getData().getInsuranceCompany().getName());
		assertNotNull(validation.getData().getExternalQuotationId());
	}

	@Test
	public void executePrePolicyEmissionServiceWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionServiceWithRestClientException...");

		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap())).thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		EmisionBO validation = rbvdr201.executePrePolicyEmissionService(new EmisionBO(), "quotationId", "traceId");
		assertNull(validation);
	}

	@Test
	public void executePrePolicyEmissionServiceOK() throws IOException {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionServiceOK...");

		EmisionBO rimacResponse = mockData.getEmisionRimacResponse();

		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap())).thenReturn(rimacResponse);

		EmisionBO validation = rbvdr201.executePrePolicyEmissionService(new EmisionBO(), "quotationId", "traceId");

		assertNotNull(validation);
		assertNotNull(validation.getPayload());
		assertNotNull(validation.getPayload().getCotizacion());
		assertNotNull(validation.getPayload().getIndicadorRequierePago());
		assertNotNull(validation.getPayload().getCodProducto());
		assertNotNull(validation.getPayload().getNumeroPoliza());
		assertNotNull(validation.getPayload().getPrimaNeta());
		assertNotNull(validation.getPayload().getPrimaBruta());
		assertNotNull(validation.getPayload().getIndicadorInspeccion());
		assertNotNull(validation.getPayload().getIndicadorGps());
		assertNotNull(validation.getPayload().getEnvioElectronico());
		assertNotNull(validation.getPayload().getFinanciamiento());
		assertNotNull(validation.getPayload().getNumeroCuotas());
		assertNotNull(validation.getPayload().getFechaInicio());
		assertNotNull(validation.getPayload().getFechaFinal());
		assertNotNull(validation.getPayload().getCuotasFinanciamiento());
		assertFalse(validation.getPayload().getCuotasFinanciamiento().isEmpty());
		assertNotNull(validation.getPayload().getContratante());
		assertNotNull(validation.getPayload().getContratante().getTipoDocumento());
		assertNotNull(validation.getPayload().getContratante().getNumeroDocumento());
		assertNotNull(validation.getPayload().getContratante().getApellidoPaterno());
		assertNotNull(validation.getPayload().getContratante().getApellidoMaterno());
		assertNotNull(validation.getPayload().getContratante().getNombres());
		assertNotNull(validation.getPayload().getContratante().getSexo());
		assertNotNull(validation.getPayload().getContratante().getFechaNacimiento());
		assertNotNull(validation.getPayload().getContratante().getUbigeo());
		assertNotNull(validation.getPayload().getContratante().getNombreDistrito());
		assertNotNull(validation.getPayload().getContratante().getNombreProvincia());
		assertNotNull(validation.getPayload().getContratante().getNombreDepartamento());
		assertNotNull(validation.getPayload().getContratante().getNombreVia());
		assertNotNull(validation.getPayload().getContratante().getNumeroVia());
		assertNotNull(validation.getPayload().getContratante().getCorreo());
		assertNotNull(validation.getPayload().getContratante().getTelefono());
		assertNotNull(validation.getPayload().getResponsablePago());
		assertNotNull(validation.getPayload().getAsegurado());

	}



	
}
