package com.bbva.rbvd.lib.r201;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.apx.exception.io.network.TimeoutException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.lib.r352.PISDR352;
import com.bbva.rbvd.dto.cicsconnection.icr2.ICR2Response;
import com.bbva.rbvd.dto.cicsconnection.utils.HostAdvice;
import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;
import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherDataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r046.RBVDR046;
import com.bbva.rbvd.lib.r047.RBVDR047;
import com.bbva.rbvd.lib.r066.RBVDR066;
import com.bbva.rbvd.lib.r201.factory.ApiConnectorFactoryMock;
import com.bbva.rbvd.lib.r201.impl.RBVDR201Impl;
import com.bbva.rbvd.lib.r201.properties.EmissionServiceProperties;

import com.bbva.rbvd.lib.r201.util.RimacUrlForker;
import com.bbva.rbvd.lib.r602.RBVDR602;
import com.bbva.rbvd.mock.EntityMock;
import com.bbva.rbvd.mock.MockBundleContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
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


	private MockData mockData;
	private APIConnector internalApiConnector;
	private APIConnector externalApiConnector;
	private APIConnector internalApiConnectorImpersonation;
	private RimacUrlForker rimacUrlForker;
	private CustomerListASO customerList;
	private MockDTO mockDTO;

	private PISDR352 pisdR352;

	private RBVDR066 rbvdR066;

	private RBVDR602 rbvdR602;
	private RBVDR046 rbvdR046;
	private RBVDR047 rbvdR047;

	private ApplicationConfigurationService applicationConfigurationService;

	private EmissionServiceProperties emissionServiceProperties;

	private static final String ERROR_SERVICE_ASO_MESSAGE = "Actualmente, estamos experimentando dificultades para establecer conexión con el servicio %s, utilizado en el servicio ASO '%s', debido a un error detectado: '%s'. Por favor, inténtalo de nuevo más tarde. Lamentamos los inconvenientes.";
	private static final String ERROR_SERVICE_TIMEOUT_ASO_MESSAGE = "Actualmente, el servicio %s no está disponible debido a un tiempo de espera en la conexión, al ser utilizado en el contexto del servicio ASO %s. Te recomendamos intentar acceder a este servicio en unos minutos, gracias.";
	private static final String PROPERTIES_TIMEOUT_EXCEPTION = "error.message.timeout";
	private static final String PROPERTIES_REST_EXCEPTION = "error.message.restException";

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


		rimacUrlForker = mock(RimacUrlForker.class);
		rbvdR201.setRimacUrlForker(rimacUrlForker);

		mockDTO = MockDTO.getInstance();
		customerList = mockDTO.getCustomerDataResponse();

		pisdR352 = mock(PISDR352.class);
		rbvdR201.setPisdR352(pisdR352);

		rbvdR066 = mock(RBVDR066.class);
		rbvdR201.setRbvdR066(rbvdR066);

		rbvdR602 = mock(RBVDR602.class);
		rbvdR201.setRbvdR602(rbvdR602);

		rbvdR046 = mock(RBVDR046.class);
		rbvdR201.setRbvdR046(rbvdR046);

		rbvdR047 = mock(RBVDR047.class);
		rbvdR201.setRbvdR047(rbvdR047);

		applicationConfigurationService = Mockito.mock(ApplicationConfigurationService.class);
		rbvdR201.setApplicationConfigurationService(applicationConfigurationService);

		emissionServiceProperties = new EmissionServiceProperties();
		emissionServiceProperties.setApplicationConfigurationService(applicationConfigurationService);

		rbvdR201.setEmissionServiceProperties(emissionServiceProperties);

		when(this.applicationConfigurationService.getDefaultProperty(eq(PROPERTIES_REST_EXCEPTION), eq(ERROR_SERVICE_ASO_MESSAGE))).thenReturn(ERROR_SERVICE_ASO_MESSAGE);
		when(this.applicationConfigurationService.getDefaultProperty(eq(PROPERTIES_TIMEOUT_EXCEPTION), eq(ERROR_SERVICE_TIMEOUT_ASO_MESSAGE))).thenReturn(ERROR_SERVICE_TIMEOUT_ASO_MESSAGE);
		when(this.applicationConfigurationService.getDefaultProperty(eq("enabled.mock.emission.cics"), eq(Boolean.FALSE.toString()))).thenReturn(Boolean.FALSE.toString());
	}

	@Test
	public void emission_cics_mock_result() throws IOException {
		// Given
		when(this.applicationConfigurationService.getDefaultProperty(eq("enabled.mock.emission.cics"), eq(Boolean.FALSE.toString()))).thenReturn(Boolean.TRUE.toString());
		DataASO requestBody = new DataASO();
		RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_S;
		ICR2Response icr2Response = new ICR2Response();
		icr2Response.setHostAdviceCode(new ArrayList<>());
		icr2Response.setIcmrys2(EntityMock.getInstance().buildFormatoICMRYS2());
		when(rbvdR047.executePreFormalizationContract(Mockito.anyObject())).thenReturn(icr2Response);

		// When
		ResponseLibrary<PolicyASO> result = rbvdR201.executePrePolicyEmissionCics(requestBody, indicatorPreFormalized);

		// Then
		assertEquals(RBVDInternalConstants.Status.OK, result.getStatusProcess());
		assertEquals("00110135104001261609", result.getBody().getData().getId());
	}


	@Test
	public void prePolicyEmissionCicsReturnsExpectedResult() throws IOException {
		// Given
		DataASO requestBody = new DataASO();
		RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_S;
		ICR2Response icr2Response = new ICR2Response();
		icr2Response.setHostAdviceCode(new ArrayList<>());
		icr2Response.setIcmrys2(EntityMock.getInstance().buildFormatoICMRYS2());
		when(rbvdR047.executePreFormalizationContract(Mockito.anyObject())).thenReturn(icr2Response);

		// When
		ResponseLibrary<PolicyASO> result = rbvdR201.executePrePolicyEmissionCics(requestBody, indicatorPreFormalized);

		// Then
		assertEquals(RBVDInternalConstants.Status.OK, result.getStatusProcess());
	}

	@Test
	public void prePolicyEmissionCicsReturnsErrorResult() {
		// Given
		DataASO requestBody = new DataASO();
		RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.NOT_PRE_FORMALIZED_N;
		ICR2Response icr2Response = new ICR2Response();
		icr2Response.setHostAdviceCode(Collections.singletonList(new HostAdvice("IC123123","ERROR ABEND")));
		when(rbvdR047.executePreFormalizationContract(Mockito.anyObject())).thenReturn(icr2Response);

		// When
		ResponseLibrary<PolicyASO> result = rbvdR201.executePrePolicyEmissionCics(requestBody, indicatorPreFormalized);

		// Then
		assertEquals(RBVDInternalConstants.Status.ENR, result.getStatusProcess());
	}



	@Test
	public void prePolicyEmissionThrowsBusinessExceptionForHttpStatusCodeException() {
		// Given
		DataASO requestBody = new DataASO();
		when(internalApiConnector.postForObject(anyString(), any(HttpEntity.class), eq(PolicyASO.class))).thenThrow(new HttpStatusCodeException(HttpStatus.BAD_REQUEST) {});

		// When / Then
		assertThrows(BusinessException.class, () -> rbvdR201.executePrePolicyEmissionASO(requestBody));
	}

	@Test
	public void prePolicyEmissionThrowsBusinessExceptionForRestClientException() {
		// Given
		DataASO requestBody = new DataASO();
		when(internalApiConnector.postForObject(anyString(), any(HttpEntity.class), eq(PolicyASO.class))).thenThrow(new RestClientException("error"));

		// When / Then
		assertThrows(BusinessException.class, () -> rbvdR201.executePrePolicyEmissionASO(requestBody));
	}

	@Test
	public void prePolicyEmissionThrowsBusinessExceptionForTimeoutException() {
		// Given
		DataASO requestBody = new DataASO();
		when(internalApiConnector.postForObject(anyString(), any(HttpEntity.class), eq(PolicyASO.class))).thenThrow(new TimeoutException("TIMEOUT EXCEPTION"));

		// When / Then
		assertThrows(BusinessException.class, () -> rbvdR201.executePrePolicyEmissionASO(requestBody));
	}

	@Test
	public void executeGetContactDetailsServiceReturnsExpectedResult() {
		// Given
		String customerId = "customerId";
		GetContactDetailsASO expected = new GetContactDetailsASO();
		when(rbvdR046.executeGetContactDetailsService(customerId)).thenReturn(expected);

		// When
		GetContactDetailsASO result = rbvdR201.executeGetContactDetailsService(customerId);

		// Then
		assertEquals(expected, result);
	}

	@Test
	public void executeGetContactDetailsServiceReturnsNullWhenServiceFails() {
		// Given
		String customerId = "customerId";
		when(rbvdR046.executeGetContactDetailsService(customerId)).thenReturn(null);

		// When
		GetContactDetailsASO result = rbvdR201.executeGetContactDetailsService(customerId);

		// Then
		assertNull(result);
	}


	@Test
	public void executePrePolicyEmissionServiceReturnsExpectedResult() {
		// Given
		EmisionBO requestBody = new EmisionBO();
		String quotationId = "quotationId";
		String productId = "productId";
		String traceId = "traceId";
		EmisionBO expected = new EmisionBO();
		when(pisdR352.executePrePolicyEmissionService(Mockito.anyObject(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(expected);

		// When
		EmisionBO result = rbvdR201.executePrePolicyEmissionService(requestBody, quotationId, productId, traceId);

		// Then
		assertEquals(expected, result);
	}



	@Test
	public void executeGetListBusinessesReturnsExpectedResult() {
		// Given
		String customerId = "customerId";
		String expands = "expands";
		ListBusinessesASO expected = new ListBusinessesASO();
		when(rbvdR066.executeGetListBusinesses(customerId, expands)).thenReturn(expected);

		// When
		ListBusinessesASO result = rbvdR201.executeGetListBusinesses(customerId, expands);

		// Then
		assertEquals(expected, result);
	}

	@Test
	public void executeGetListBusinessesReturnsNullWhenServiceFails() {
		// Given
		String customerId = "customerId";
		String expands = "expands";
		when(rbvdR066.executeGetListBusinesses(customerId, expands)).thenReturn(null);

		// When
		ListBusinessesASO result = rbvdR201.executeGetListBusinesses(customerId, expands);

		// Then
		assertNull(result);
	}

	@Test
	public void executeAddParticipantsServiceReturnsExpectedResult() {
		// Given
		AgregarTerceroBO requestBody = new AgregarTerceroBO();
		String quotationId = "quotationId";
		String productId = "productId";
		String traceId = "traceId";
		AgregarTerceroBO expected = new AgregarTerceroBO();
		when(pisdR352.executeAddParticipantsService(requestBody, quotationId, productId, traceId)).thenReturn(expected);

		// When
		AgregarTerceroBO result = rbvdR201.executeAddParticipantsService(requestBody, quotationId, productId, traceId);

		// Then
		assertEquals(expected, result);
	}

	@Test(expected = BusinessException.class)
	public void executeAddParticipantsServiceThrowsBusinessException() {
		// Given
		AgregarTerceroBO requestBody = new AgregarTerceroBO();
		String quotationId = "quotationId";
		String productId = "productId";
		String traceId = "traceId";
		when(pisdR352.executeAddParticipantsService(requestBody, quotationId, productId, traceId)).thenThrow(BusinessException.class);

		// When
		rbvdR201.executeAddParticipantsService(requestBody, quotationId, productId, traceId);

		// Then exception is thrown
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
	public void executePutEventUpsilonServiceWithRestClientException() {
		LOGGER.info("RBVDR201Test - Executing executePutEventUpsilonServiceWithRestClientException...");

		when(this.internalApiConnectorImpersonation.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Integer>)any())).
				thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		Integer validation = rbvdR201.executePutEventUpsilonService(new CreatedInsrcEventDTO());

		assertNotNull(validation);
		assertEquals(0, validation.intValue());
	}



	@Test(expected = BusinessException.class)
	public void executePrePolicyEmissionASOWithStatusCodeException() {
		LOGGER.info("RBVDR201Test - Executing executePrePolicyEmissionASOWithRestClientException...");

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

		rbvdR201.executePrePolicyEmissionASO(new DataASO());
	}

}
