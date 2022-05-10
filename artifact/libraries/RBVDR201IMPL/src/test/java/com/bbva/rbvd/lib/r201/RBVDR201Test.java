package com.bbva.rbvd.lib.r201;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.lib.r201.factory.ApiConnectorFactoryMock;
import com.bbva.rbvd.lib.r201.impl.RBVDR201Impl;
import com.bbva.rbvd.lib.r201.impl.util.RimacUrlForker;
import com.bbva.rbvd.mock.MockBundleContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

	private RBVDR201Impl rbvdR201 = new RBVDR201Impl();

	private PISDR014 pisdr014;

	private MockData mockData;
	private APIConnector internalApiConnector;
	private APIConnector externalApiConnector;
	private RimacUrlForker rimacUrlForker;
	private CustomerListASO customerList;
	private MockDTO mockDTO;
	
	@Before
	public void setUp() throws Exception{
		ThreadContext.set(new Context());

		mockData = MockData.getInstance();

		MockBundleContext mockBundleContext = mock(MockBundleContext.class);

		ApiConnectorFactoryMock apiConnectorFactoryMock = new ApiConnectorFactoryMock();
		internalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext);
		rbvdR201.setInternalApiConnector(internalApiConnector);

		externalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext, false);
		rbvdR201.setExternalApiConnector(externalApiConnector);

		pisdr014 = mock(PISDR014.class);
		rbvdR201.setPisdR014(pisdr014);
		rimacUrlForker = mock(RimacUrlForker.class);
		rbvdR201.setRimacUrlForker(rimacUrlForker);
		when(pisdr014.executeSignatureConstruction(anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(new SignatureAWS("", "", "", ""));
		mockDTO = MockDTO.getInstance();
		customerList = mockDTO.getCustomerDataResponse();
	}

	@Test(expected = BusinessException.class)
	public void executePrePolicyEmissionASOWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionASOWithRestClientException...");

		String responseBody = "{\"messages\":[{\"code\":\"wrongParameters\",\"message\":\"LOS DATOS INGRESADOS SON INVALIDOS\",\"parameters\":[],\"type\":\"FATAL\"}]}";

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8));

		rbvdR201.executePrePolicyEmissionASO(new DataASO());
	}

	@Test
	public void executePrePolicyEmissionASO_OK() throws IOException {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionASO_OK...");

		PolicyASO responseBody = mockData.getEmisionASOResponse();

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).thenReturn(responseBody);

		PolicyASO validation = rbvdR201.executePrePolicyEmissionASO(new DataASO());

		assertNotNull(validation);
		assertNotNull(validation.getData());
		assertNotNull(validation.getData().getId());
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
	}

	@Test(expected = BusinessException.class)
	public void executePrePolicyEmissionServiceWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionServiceWithRestClientException...");

		String responseBody = "{\"error\":{\"code\":\"VEHDAT005\",\"message\":\"Error al Validar Datos.\",\"details\":[\"\\\"contactoInspeccion.telefono\\\" debe contener caracteres\"],\"httpStatus\":400}}";
		when(rimacUrlForker.generatePropertyKeyName(anyString())).thenReturn("value");
		when(rimacUrlForker.generateUriForSignatureAWS(anyString(), anyString())).thenReturn("value");
		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap())).thenThrow(new HttpServerErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8));

		rbvdR201.executePrePolicyEmissionService(new EmisionBO(), "quotationId", "traceId", "830");
	}

	@Test
	public void executePrePolicyEmissionServiceOK() throws IOException {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionServiceOK...");

		EmisionBO rimacResponse = mockData.getEmisionRimacResponse();
		when(rimacUrlForker.generatePropertyKeyName(anyString())).thenReturn("value");
		when(rimacUrlForker.generateUriForSignatureAWS(anyString(), anyString())).thenReturn("value");
		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap())).thenReturn(rimacResponse);

		EmisionBO validation = rbvdR201.executePrePolicyEmissionService(new EmisionBO(), "quotationId", "traceId", "830");

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

	@Test
	public void executeCreateEmailWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executeCreateEmailWithRestClientException...");
		when(this.internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<String>) any()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		Integer validation = rbvdR201.executeCreateEmail(new CreateEmailASO());

		assertNull(validation);
	}

	@Test
	public void executeCreateEmailOK() {
		LOGGER.info("RBVDR201Test - Executing executeCreateEmailOK...");
		when(this.internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<String>) any()))
				.thenReturn(new ResponseEntity<>("", HttpStatus.OK));

		Integer validation = rbvdR201.executeCreateEmail(new CreateEmailASO());

		assertNotNull(validation);
		assertEquals(new Integer(HttpStatus.OK.value()), validation);
	}

	@Test
	public void executeGetCustomerInformationServiceOK() {
		LOGGER.info("RBVDR201Test - Executing executeGetCustomerInformationServiceOK...");

		when(internalApiConnector.getForObject(anyString(), any(), anyMap()))
				.thenReturn(customerList);

		CustomerListASO validation = rbvdR201.executeGetCustomerInformation("90008603");
		assertNotNull(validation);
		assertNotNull(validation.getData().get(0).getFirstName());
	}

	@Test
	public void executeRegisterAdditionalCustomerResponseWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executeGetCustomerInformationServiceOK...");
		when(internalApiConnector.getForObject(anyString(), any(), anyMap()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		CustomerListASO validation = rbvdR201.executeGetCustomerInformation("90008603");
		assertNull(validation);
	}

	@Test
	public void executeNewGifoleServiceOK() {
		LOGGER.info("RBVDR201Test - Executing executeGifoleServiceOK...");

		when(this.internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Void>) any()))
				.thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

		Integer validation = rbvdR201.executeGifoleEmisionService(new GifoleInsuranceRequestASO());

		assertNotNull(validation);
		assertEquals(new Integer(201), validation);
	}

	@Test
	public void executeNewGifoleServiceWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executeGifoleServiceWithRestClientException...");

		when(this.internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Void>) any()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		Integer validation = rbvdR201.executeGifoleEmisionService(new GifoleInsuranceRequestASO());

		assertNull(validation);
		assertEquals(PISDErrors.ERROR_CONNECTION_GIFOLE_ROYAL_INSURANCE_REQUEST_ASO_SERVICE.getAdviceCode(), this.rbvdR201.getAdviceList().get(0).getCode());
	}

	
}
