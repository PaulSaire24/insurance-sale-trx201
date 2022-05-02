package com.bbva.rbvd.lib.r211;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r021.PISDR021;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PayloadEmisionBO;
import com.bbva.rbvd.dto.insrncsale.commons.DocumentTypeDTO;
import com.bbva.rbvd.dto.insrncsale.commons.IdentityDocumentDTO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantTypeDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.lib.r211.impl.RBVDR211Impl;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR211-app.xml",
		"classpath:/META-INF/spring/RBVDR211-app-test.xml",
		"classpath:/META-INF/spring/RBVDR211-arc.xml",
		"classpath:/META-INF/spring/RBVDR211-arc-test.xml" })
public class RBVDR211Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Test.class);
	private static final String AGENT_AND_PROMOTER_DEFAULT_CODE = "UCQGSPPP";

	private RBVDR211Impl rbvdr211 = new RBVDR211Impl();

	private MockData mockData;
	private MockDTO mockDTO;
	private ApplicationConfigurationService applicationConfigurationService;
	private RBVDR201 rbvdr201;
	private PISDR012 pisdR012;
	private PISDR021 pisdR021;
	private MapperHelper mapperHelper;

	private PolicyDTO requestBody;

	private Map<String, Object> responseQueryGetRequiredFields;

	private Map<String, Object> responseQueryRoles;
	private List<Map<String, Object>> roles;
	private Map<String, Object> firstRole;

	private PolicyASO asoResponse;
	private EmisionBO rimacResponse;

	private CustomerListASO customerList;

	@Before
	public void setUp() throws IOException {
		ThreadContext.set(new Context());

		mockData = MockData.getInstance();

		applicationConfigurationService = mock(ApplicationConfigurationService.class);
		rbvdr201 = mock(RBVDR201.class);
		pisdR012 = mock(PISDR012.class);
		pisdR021 = mock(PISDR021.class);
		mapperHelper = mock(MapperHelper.class);

		rbvdr211.setApplicationConfigurationService(applicationConfigurationService);
		rbvdr211.setRbvdR201(rbvdr201);
		rbvdr211.setPisdR012(pisdR012);
		rbvdr211.setPisdR021(pisdR021);
		rbvdr211.setMapperHelper(mapperHelper);

		requestBody = mockData.getCreateInsuranceRequestBody();

		mockDTO = MockDTO.getInstance();
		customerList = mockDTO.getCustomerDataResponse();

		responseQueryGetRequiredFields = mock(Map.class);

		when(responseQueryGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue())).thenReturn(BigDecimal.valueOf(1));
		when(responseQueryGetRequiredFields.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue())).thenReturn(BigDecimal.valueOf(12));
		when(responseQueryGetRequiredFields.get(RBVDProperties.FIELD_PAYMENT_FREQUENCY_ID.getValue())).thenReturn(BigDecimal.valueOf(1));
		when(responseQueryGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue())).thenReturn("rimacQuotation");

		responseQueryRoles = mock(Map.class);
		roles = mock(List.class);
		firstRole = mock(Map.class);

		when(this.applicationConfigurationService.getProperty("telemarketing.code")).thenReturn("7794");
		when(this.applicationConfigurationService.getProperty("pic.code")).thenReturn("PC");
		when(this.applicationConfigurationService.getProperty("agent.and.promoter.code")).thenReturn(AGENT_AND_PROMOTER_DEFAULT_CODE);
		when(this.applicationConfigurationService.getProperty("ENDOSATARIO_RUC")).thenReturn("00000000000");
		when(this.applicationConfigurationService.getProperty("ENDOSATARIO_PORCENTAJE")).thenReturn("40");
		
		asoResponse = mockData.getEmisionASOResponse();
		rimacResponse = mockData.getEmisionRimacResponse();
		EmisionBO emision = new EmisionBO();
		emision.setPayload(new PayloadEmisionBO());
		when(this.mapperHelper.buildRequestBodyRimac(anyObject(), anyString(), anyString(), anyString(), anyString())).thenReturn(emision);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithNonExistentQuotation() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithNonExistentQuotation...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(null);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.NON_EXISTENT_QUOTATION.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWitContractInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWitContractInsertionError...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(-1);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithFirstReceiptInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithFirstReceiptInsertionError...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(-1);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithContractMovInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithContractMovInsertionError...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveContractMove(anyMap())).thenReturn(-1);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_MOV_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithParticipantsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithParticipantsInsertionError...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveContractMove(anyMap())).thenReturn(1);

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		when(pisdR012.executeGetRolesByProductAndModality(any(), anyString())).thenReturn(responseQueryRoles);

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(arguments);

		when(pisdR012.executeSaveParticipants(any())).thenReturn(null);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_PARTICIPANT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithEndorsementInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithEndorsementInsertionError...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveContractMove(anyMap())).thenReturn(1);

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		when(pisdR012.executeGetRolesByProductAndModality(any(), anyString())).thenReturn(responseQueryRoles);

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(arguments);

		when(pisdR012.executeSaveParticipants(any())).thenReturn(new int[1]);

		ParticipantDTO secondParticipant = new ParticipantDTO();

		IdentityDocumentDTO document = new IdentityDocumentDTO();

		DocumentTypeDTO tipoDocumento = new DocumentTypeDTO();
		tipoDocumento.setId("RUC");

		document.setDocumentType(tipoDocumento);
		secondParticipant.setIdentityDocument(document);

		secondParticipant.setBenefitPercentage(0.0d);

		ParticipantTypeDTO tipoParticipante = new ParticipantTypeDTO();
		tipoParticipante.setId("ENDORSEE");

		secondParticipant.setParticipantType(tipoParticipante);

		requestBody.getParticipants().add(secondParticipant);

		when(pisdR012.executeSaveContractEndoserment(anyMap())).thenReturn(-1);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithContractUpdateError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithContractUpdateError...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveContractMove(anyMap())).thenReturn(1);

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		when(pisdR012.executeGetRolesByProductAndModality(any(), anyString())).thenReturn(responseQueryRoles);

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(arguments);

		when(pisdR012.executeSaveParticipants(any())).thenReturn(new int[1]);

		ParticipantDTO secondParticipant = new ParticipantDTO();

		IdentityDocumentDTO document = new IdentityDocumentDTO();

		DocumentTypeDTO tipoDocumento = new DocumentTypeDTO();
		tipoDocumento.setId("RUC");

		document.setDocumentType(tipoDocumento);
		secondParticipant.setIdentityDocument(document);

		secondParticipant.setBenefitPercentage(0.0d);

		ParticipantTypeDTO tipoParticipante = new ParticipantTypeDTO();
		tipoParticipante.setId("ENDORSEE");

		secondParticipant.setParticipantType(tipoParticipante);

		requestBody.getParticipants().add(secondParticipant);

		when(pisdR012.executeSaveContractEndoserment(anyMap())).thenReturn(1);

		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(rimacResponse);

		when(pisdR012.executeUpdateContract(anyMap())).thenReturn(-1);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveContractMove(anyMap())).thenReturn(1);

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		when(pisdR012.executeGetRolesByProductAndModality(any(), anyString())).thenReturn(responseQueryRoles);

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(arguments);

		when(pisdR012.executeSaveParticipants(any())).thenReturn(new int[1]);

		ParticipantDTO secondParticipant = new ParticipantDTO();

		IdentityDocumentDTO document = new IdentityDocumentDTO();

		DocumentTypeDTO tipoDocumento = new DocumentTypeDTO();
		tipoDocumento.setId("RUC");

		document.setDocumentType(tipoDocumento);
		secondParticipant.setIdentityDocument(document);

		secondParticipant.setBenefitPercentage(0.0d);

		ParticipantTypeDTO tipoParticipante = new ParticipantTypeDTO();
		tipoParticipante.setId("ENDORSEE");

		secondParticipant.setParticipantType(tipoParticipante);

		requestBody.getParticipants().add(secondParticipant);

		when(pisdR012.executeSaveContractEndoserment(anyMap())).thenReturn(1);

		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(rimacResponse);

		when(pisdR012.executeUpdateContract(anyMap())).thenReturn(1);

		when(mapperHelper.createSaveReceiptsArguments(anyList())).thenReturn(arguments);

		when(pisdR012.executeSaveReceipts(any())).thenReturn(null);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithVehicularProductOK() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithVehicularProductOK...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		ParticipantDTO secondParticipant = new ParticipantDTO();

		IdentityDocumentDTO document = new IdentityDocumentDTO();

		DocumentTypeDTO tipoDocumento = new DocumentTypeDTO();
		tipoDocumento.setId("RUC");

		document.setDocumentType(tipoDocumento);
		secondParticipant.setIdentityDocument(document);

		secondParticipant.setBenefitPercentage(0.0d);

		ParticipantTypeDTO tipoParticipante = new ParticipantTypeDTO();
		tipoParticipante.setId("ENDORSEE");

		secondParticipant.setParticipantType(tipoParticipante);

		requestBody.getParticipants().add(secondParticipant);

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveContractMove(anyMap())).thenReturn(1);

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		when(pisdR012.executeGetRolesByProductAndModality(any(), anyString())).thenReturn(responseQueryRoles);

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(arguments);

		when(pisdR012.executeSaveParticipants(any())).thenReturn(new int[1]);

		when(pisdR012.executeSaveContractEndoserment(anyMap())).thenReturn(1);

		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(rimacResponse);

		when(pisdR012.executeUpdateContract(anyMap())).thenReturn(1);

		when(mapperHelper.createSaveReceiptsArguments(anyList())).thenReturn(arguments);

		when(pisdR012.executeSaveReceipts(any())).thenReturn(new int[1]);

		when(rbvdr201.executeCreateEmail(anyObject())).thenReturn(200);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNotNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithHomeProductOK() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithHomeProductOK...");

		when(pisdR012.executeGetRequiredFieldsForEmissionService(anyString())).thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveContractMove(anyMap())).thenReturn(1);

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		when(pisdR012.executeGetRolesByProductAndModality(any(), anyString())).thenReturn(responseQueryRoles);

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(arguments);

		when(pisdR012.executeSaveParticipants(any())).thenReturn(new int[1]);

		requestBody.setProductId("832");

		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);

		Map<String,Object> responseGetHomeInfoForEmissionService = new HashMap<>();
		responseGetHomeInfoForEmissionService.put("DEPARTMENT_NAME", "LIMA");
		responseGetHomeInfoForEmissionService.put("PROVINCE_NAME", "LIMA");
		responseGetHomeInfoForEmissionService.put("DISTRICT_NAME", "LINCE");
		responseGetHomeInfoForEmissionService.put("HOUSING_TYPE", "A");
		responseGetHomeInfoForEmissionService.put("AREA_PROPERTY_1_NUMBER", new BigDecimal(2));
		responseGetHomeInfoForEmissionService.put("PROP_SENIORITY_YEARS_NUMBER", new BigDecimal(10));
		responseGetHomeInfoForEmissionService.put("FLOOR_NUMBER", new BigDecimal(3));
		responseGetHomeInfoForEmissionService.put("EDIFICATION_LOAN_AMOUNT", new BigDecimal(111.1));
		responseGetHomeInfoForEmissionService.put("HOUSING_ASSETS_LOAN_AMOUNT", new BigDecimal(222.2));

		Map<String,Object> responseGetHomeRiskDirectionService = new HashMap<>();
		responseGetHomeRiskDirectionService.put("LEGAL_ADDRESS_DESC", "RISK_DIRECTION");

		when(pisdR021.executeGetHomeInfoForEmissionService(any())).thenReturn(responseGetHomeInfoForEmissionService);

		when(pisdR021.executeGetHomeRiskDirection(anyString())).thenReturn(responseGetHomeRiskDirectionService);

		when(rbvdr201.executeCreateEmail(anyObject())).thenReturn(500);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNotNull(validation);
	}
}
