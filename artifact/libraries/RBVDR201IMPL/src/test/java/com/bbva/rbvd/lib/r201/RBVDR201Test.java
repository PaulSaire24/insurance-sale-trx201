package com.bbva.rbvd.lib.r201;

import com.bbva.apx.exception.business.BusinessException;

import com.bbva.apx.exception.io.network.TimeoutException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.elara.utility.api.connector.APIConnector;

import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;

import com.bbva.pisd.dto.insurance.mock.MockDTO;

import com.bbva.pisd.lib.r014.PISDR014;

import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;
import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherDataASO;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;

import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

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
	private static final String KEY_CYPHER_CODE = "apx-pe-fpextff1-do";

	private RBVDR201Impl rbvdR201 = new RBVDR201Impl();

	private PISDR014 pisdr014;

	private MockData mockData;
	private APIConnector internalApiConnector;
	private APIConnector externalApiConnector;
	private APIConnector internalApiConnectorImpersonation;
	private RimacUrlForker rimacUrlForker;
	private CustomerListASO customerList;
	private MockDTO mockDTO;
	private ApplicationConfigurationService applicationConfigurationService;

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

		internalApiConnectorImpersonation = apiConnectorFactoryMock.getAPIConnector(mockBundleContext, true, true);
		rbvdR201.setInternalApiConnectorImpersonation(internalApiConnectorImpersonation);

		applicationConfigurationService = mock(ApplicationConfigurationService.class);
		rbvdR201.setApplicationConfigurationService(applicationConfigurationService);

		pisdr014 = mock(PISDR014.class);
		rbvdR201.setPisdR014(pisdr014);
		rimacUrlForker = mock(RimacUrlForker.class);
		rbvdR201.setRimacUrlForker(rimacUrlForker);
		when(pisdr014.executeSignatureConstruction(anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(new SignatureAWS("", "", "", ""));
		mockDTO = MockDTO.getInstance();
		customerList = mockDTO.getCustomerDataResponse();

		when(applicationConfigurationService.getDefaultProperty(eq("error.message.timeout"),anyString())).thenReturn("ERROR_SERVICE_TIMEOUT_ASO.getMessage()");
		when(applicationConfigurationService.getDefaultProperty(eq("error.message.restException"), anyString())).thenReturn("ERROR_SERVICE_ASO.getMessage()");
	}

	@Test
	public void executeGetContactDetailsServiceOK() throws IOException {
		LOGGER.info("RBVDR201Test - Executing executeGetContactDetailsServiceOK...");

		GetContactDetailsASO responseListCustomers = mockDTO.getContactDetailsResponse();

		when(internalApiConnector.getForObject(anyString(), any(), anyMap())).
				thenReturn(responseListCustomers);

		GetContactDetailsASO validation = rbvdR201.executeGetContactDetailsService("customerId");

		assertNotNull(validation);
		assertFalse(validation.getData().isEmpty());
	}

	@Test
	public void executeGetContactDetailsServiceWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executeGetContactDetailsServiceWithRestClientException...");

		when(internalApiConnector.getForObject(anyString(), any(), anyMap())).
				thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		GetContactDetailsASO validation = rbvdR201.executeGetContactDetailsService("customerId");

		assertNull(validation);
	}

	@Test(expected = BusinessException.class)
	public void executePrePolicyEmissionASOWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionASOWithRestClientException...");

		String responseBody = "{\"messages\":[{\"code\":\"wrongParameters\",\"message\":\"LOS DATOS INGRESADOS SON INVALIDOS\",\"parameters\":[],\"type\":\"FATAL\"}]}";

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8));

		rbvdR201.executePrePolicyEmissionASO(new DataASO());
	}

	@Test(expected = BusinessException.class)
	public void executePrePolicyEmissionASOWithTimeoutException() {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionASOWithRestClientException...");

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).thenThrow(new TimeoutException("timeoutExcp"));

		rbvdR201.executePrePolicyEmissionASO(new DataASO());
	}

	@Test(expected = BusinessException.class)
	public void executePrePolicyEmissionASOWithStatusCodeException() {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionASOWithRestClientException...");

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

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

	@Test
	public void executePrePolicyEmissionServiceWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionServiceWithRestClientException...");

		String responseBody = "{\"error\":{\"code\":\"VEHDAT005\",\"message\":\"Error al Validar Datos.\",\"details\":[\"\\\"contactoInspeccion.telefono\\\" debe contener caracteres\"],\"httpStatus\":400}}";
		when(rimacUrlForker.generatePropertyKeyName(anyString())).thenReturn("value");
		when(rimacUrlForker.generateUriForSignatureAWS(anyString(), anyString())).thenReturn("value");
		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap())).thenThrow(new HttpServerErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8));

		EmisionBO rimacResponse = rbvdR201.executePrePolicyEmissionService(new EmisionBO(), "quotationId", "traceId", "830");
		assertNull(rimacResponse);
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
	public void executeGetListBusinessesServiceOK() {
		LOGGER.info("RBVDR201Test - Executing executeGetListBusinessesServiceOK...");
		ListBusinessesASO businesses = new ListBusinessesASO();
		businesses.setData(new ArrayList<>());
		when(internalApiConnector.getForObject(anyString(), any(), anyMap()))
				.thenReturn(businesses);

		ListBusinessesASO validation = rbvdR201.executeGetListBusinesses("90008603", null);
		assertNotNull(validation);

		validation = rbvdR201.executeGetListBusinesses("90008603", "ABC");
		assertNotNull(validation);

		businesses.setData(Collections.singletonList(new BusinessASO()));
		validation = rbvdR201.executeGetListBusinesses("90008603", "ABC");
		assertNotNull(validation);
	}

	@Test
	public void executeGetListBusinessesResponseWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executeGetListBusinessesResponseWithRestClientException...");
		when(internalApiConnector.getForObject(anyString(), any(), anyMap()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		ListBusinessesASO validation = rbvdR201.executeGetListBusinesses("90008603", null);
		assertNull(validation);
	}

	@Test
	public void executeCypherServiceTestNull() {
		LOGGER.info("RBVDR201Test - Executing executeCypherServiceTestOK...");
		when(this.internalApiConnector.postForObject(anyString(), anyObject(), any())).thenReturn(null);
		String validation = rbvdR201.executeCypherService(new CypherASO("ABC", KEY_CYPHER_CODE));
		assertNull(validation);

		CypherASO response = new CypherASO();
		when(this.internalApiConnector.postForObject(anyString(), anyObject(), any())).thenReturn(response);
		validation = rbvdR201.executeCypherService(new CypherASO("ABC", KEY_CYPHER_CODE));
		assertNull(validation);

		CypherDataASO data = new CypherDataASO();
		response.setData(data);
		when(this.internalApiConnector.postForObject(anyString(), anyObject(), any())).thenReturn(response);

		validation = rbvdR201.executeCypherService(new CypherASO("ABC", KEY_CYPHER_CODE));
		assertNull(validation);

		LOGGER.info("RBVDR201Test - Executing executeCypherServiceTestOK - END... validation: {}", validation);
	}

	@Test
	public void executeCypherServiceTestOK() {
		LOGGER.info("RBVDR201Test - Executing executeCypherServiceTestOK...");
		CypherASO response = new CypherASO();
		CypherDataASO data = new CypherDataASO();
		response.setData(data);
		when(this.internalApiConnector.postForObject(anyString(), anyObject(), any())).thenReturn(response);

		String validation = rbvdR201.executeCypherService(new CypherASO("ABC", KEY_CYPHER_CODE));
		assertNull(validation);

		data.setDocument("XYZ");
		validation = rbvdR201.executeCypherService(new CypherASO("ABC", KEY_CYPHER_CODE));
		assertNotNull(validation);
		LOGGER.info("RBVDR201Test - Executing executeCypherServiceTestOK - END... validation: {}", validation);
	}

	@Test
	public void executeCypherServiceTestRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executeCypherServiceTestRestClientException...");
		when(this.internalApiConnector.postForObject(anyString(), anyObject(), any()))
				.thenThrow(new RestClientException("ERROR"));

		String validation = rbvdR201.executeCypherService(new CypherASO("ABC", KEY_CYPHER_CODE));
		assertNull(validation);
	}

	@Test
	public void executePutEventUpsilonServiceOK() {
		LOGGER.info("RBVDR201Test - Executing executePutEventUpsilonServiceOK...");

		when(this.internalApiConnectorImpersonation.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Integer>)any())).
				thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

		CreatedInsrcEventDTO createdInsrcEvent = new CreatedInsrcEventDTO();
		CreatedInsuranceDTO createdInsurance = new CreatedInsuranceDTO();
		createdInsurance.setOperationDate(Calendar.getInstance());
		createdInsrcEvent.setCreatedInsurance(createdInsurance);

		Integer validation = rbvdR201.executePutEventUpsilonService(createdInsrcEvent);

		assertNotNull(validation);
		assertEquals(201, validation.intValue());
	}

	@Test
	public void executePutEventUpsilonServiceWithTimeoutException() {
		LOGGER.info("RBVDR201Test - Executing executePutEventUpsilonServiceOK...");

		when(this.internalApiConnectorImpersonation.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Integer>)any())).
				thenThrow(new TimeoutException("TIMEOUT"));

		CreatedInsrcEventDTO createdInsrcEvent = new CreatedInsrcEventDTO();
		CreatedInsuranceDTO createdInsurance = new CreatedInsuranceDTO();
		createdInsurance.setOperationDate(Calendar.getInstance());
		createdInsrcEvent.setCreatedInsurance(createdInsurance);

		Integer validation = rbvdR201.executePutEventUpsilonService(createdInsrcEvent);

		assertNotNull(validation);
		assertEquals(0, validation.intValue());
	}

	@Test
	public void executePutEventUpsilonServiceWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executePutEventUpsilonServiceWithRestClientException...");

		when(this.internalApiConnectorImpersonation.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Integer>)any())).
				thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		Integer validation = rbvdR201.executePutEventUpsilonService(new CreatedInsrcEventDTO());

		assertNotNull(validation);
		assertEquals(0, validation.intValue());
	}

	@Test
	public void testExecuteAddParticipantsService_OK() throws IOException{

		AgregarTerceroBO response = mockData.getAddParticipantsRimacResponse();

		when(this.rimacUrlForker.generateKeyAddParticipants(anyString())).thenReturn("key.property");
		when(this.rimacUrlForker.generateUriAddParticipants(anyString(),anyString())).thenReturn("value-key-1");
		when(this.externalApiConnector.exchange(anyString(), anyObject(),anyObject(), (Class<AgregarTerceroBO>) any(), anyMap())).thenReturn(new ResponseEntity<>(response,HttpStatus.OK));

		AgregarTerceroBO validation = this.rbvdR201.executeAddParticipantsService(new AgregarTerceroBO(),"quotationId","840","traceId");

		assertNotNull(validation);
		assertNotNull(validation.getPayload());
		assertNotNull(validation.getPayload().getStatus());
		assertNotNull(validation.getPayload().getMensaje());
		assertEquals("1",validation.getPayload().getStatus());
		assertNotNull(validation.getPayload().getTerceros());
		assertEquals(3,validation.getPayload().getTerceros().size());
		assertNotNull(validation.getPayload().getTerceros().get(0));
		assertNotNull(validation.getPayload().getTerceros().get(1));
		assertNotNull(validation.getPayload().getTerceros().get(2));
		assertEquals(0,validation.getPayload().getBeneficiario().size());
	}
	@Test
	public void testExecuteAddParticipantsServiceWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing testExecuteAddParticipantsServiceWithRestClientException...");

		String responseBody = "{\"error\":{\"code\":\"VIDA001\",\"message\":\"ErroralValidarDatos.\",\"details\":[\"\\\"persona[0].celular\\\"esrequerido\"],\"httpStatus\":400}}";
		when(rimacUrlForker.generateUriAddParticipants(anyString(),anyString())).thenReturn("any-value");
		when(rimacUrlForker.generateKeyAddParticipants(anyString())).thenReturn("any-value");
		when(this.externalApiConnector.exchange(anyString(), anyObject(),anyObject(), (Class<AgregarTerceroBO>) any(), anyMap())).thenThrow(new HttpServerErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8));

		AgregarTerceroBO validation = this.rbvdR201.executeAddParticipantsService(new AgregarTerceroBO(),"quotationId","productId","traceId");
		assertNull(validation);
	}

	@Test
	public void testExecuteAddParticipantsServiceWithTimeoutException() {
		LOGGER.info("RBVDR201Test - Executing testExecuteAddParticipantsServiceWithTimeoutException...");

		when(rimacUrlForker.generateUriAddParticipants(anyString(),anyString())).thenReturn("any-value");
		when(rimacUrlForker.generateKeyAddParticipants(anyString())).thenReturn("any-value");
		when(this.externalApiConnector.exchange(anyString(), anyObject(),anyObject(), (Class<AgregarTerceroBO>) any(), anyMap())).thenThrow(new TimeoutException(HttpStatus.GATEWAY_TIMEOUT.toString()));

		AgregarTerceroBO validation = this.rbvdR201.executeAddParticipantsService(new AgregarTerceroBO(),"quotationId","productId","traceId");
		assertNull(validation);
	}

	@Test
	public void executePrePilicyEmissionServiceWithTimeoutException() {

		LOGGER.info("RBVDR201Test - Executing executePrePilicyEmissionServiceWithTimeoutException...");

		String responseBody = "{\"error\":{\"code\":\"CQT001\",\"message\"\"Error interno del Servidor.\",\"details\":[\"\\\"SIN999001\\\"Error de sintaxis\"],\"httpStatus\":504}}";
		when(rimacUrlForker.generatePropertyKeyName(anyString())).thenReturn("value");
		when(rimacUrlForker.generateUriForSignatureAWS(anyString(), anyString())).thenReturn("value");
		when(externalApiConnector.postForObject(anyString(), anyObject(), any(), anyMap())).thenThrow(new TimeoutException("RBVD01020044"));

		EmisionBO rimacResponse = rbvdR201.executePrePolicyEmissionService(new EmisionBO(), "quotationId", "traceId", "830");
		assertNull(rimacResponse);

	}

}
