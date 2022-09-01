package com.bbva.rbvd.lib.r211;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r021.PISDR021;
import com.bbva.rbvd.dto.insrncsale.aso.*;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.*;
import com.bbva.rbvd.dto.insrncsale.commons.DocumentTypeDTO;
import com.bbva.rbvd.dto.insrncsale.commons.IdentityDocumentDTO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantTypeDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.PersonTypeEnum;
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

	private Map<String, Object> argumentValidateIfPolicyExists;
	private Map<String, Object> responseValidateIfPolicyExists;
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

		argumentValidateIfPolicyExists = new HashMap<>();
		argumentValidateIfPolicyExists.put(RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue(), "0814000000366");

		responseValidateIfPolicyExists = mock(Map.class);
		when(responseValidateIfPolicyExists.get(RBVDProperties.FIELD_RESULT_NUMBER.getValue())).thenReturn(BigDecimal.ZERO);

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
		when(this.applicationConfigurationService.getProperty("enable_gifole_sales_aso")).thenReturn("true");

		asoResponse = mockData.getEmisionASOResponse();
		rimacResponse = mockData.getEmisionRimacResponse();
		EmisionBO emision = new EmisionBO();
		emision.setPayload(new PayloadEmisionBO());
		when(this.mapperHelper.buildRequestBodyRimac(anyObject(), anyString(), anyString(), anyString(), anyString())).thenReturn(emision);

		EmisionBO generalEmisionRequest = new EmisionBO();
		PayloadEmisionBO payload = new PayloadEmisionBO();
		AgregarPersonaBO agregarPersona = new AgregarPersonaBO();
		agregarPersona.setPersona(Collections.singletonList(new PersonaBO()));
		agregarPersona.setOrganizacion(Collections.singletonList(new OrganizacionBO()));
		payload.setAgregarPersona(agregarPersona);
		generalEmisionRequest.setPayload(payload);
		when(mapperHelper.mapRimacEmisionRequest(anyObject(), anyObject(), anyMap(), anyObject())).thenReturn(generalEmisionRequest);
		when(mapperHelper.getPersonType(anyObject())).thenReturn(PersonTypeEnum.NATURAL);

		when(rbvdr201.executeCypherService(anyObject())).thenReturn("");

		/* P030557 */

		when(pisdR012.executeGetASingleRow(RBVDProperties.QUERY_VALIDATE_IF_POLICY_EXISTS.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseValidateIfPolicyExists);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(pisdR012.executeInsertSingleRow(PISDProperties.QUERY_INSERT_INSURANCE_CONTRACT.getValue(), new HashMap<>(),
				RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(),
				RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(), RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(),
				RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue(),
				RBVDProperties.FIELD_CUSTOMER_ID.getValue(), RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue(),
				RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), RBVDProperties.FIELD_USER_AUDIT_ID.getValue())).thenReturn(1);

		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(), new HashMap<>(),
				RBVDProperties.FIELD_POLICY_RECEIPT_ID.getValue())).thenReturn(1);

		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue(), new HashMap<>())).
				thenReturn(1);

		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(rimacResponse);

		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT", new HashMap<>(),
				RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue(), RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue(),
				RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue(), RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue())).thenReturn(1);

		when(pisdR012.executeSaveReceipts(any())).thenReturn(new int[1]);
		/* P030557 */
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithPolicyAlreadyExistsError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithPolicyAlreadyExistsError...");

		Map<String, Object> policiesNumber = new HashMap<>();
		policiesNumber.put(RBVDProperties.FIELD_RESULT_NUMBER.getValue(), BigDecimal.ONE);

		when(pisdR012.executeGetASingleRow(RBVDProperties.QUERY_VALIDATE_IF_POLICY_EXISTS.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(policiesNumber);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.POLICY_ALREADY_EXISTS.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithNonExistentQuotation() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithNonExistentQuotation...");

		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(null);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.NON_EXISTENT_QUOTATION.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWitContractInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWitContractInsertionError...");

		when(pisdR012.executeInsertSingleRow(PISDProperties.QUERY_INSERT_INSURANCE_CONTRACT.getValue(), new HashMap<>(),
				RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(),
				RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(), RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(),
				RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue(),
				RBVDProperties.FIELD_CUSTOMER_ID.getValue(), RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue(),
				RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), RBVDProperties.FIELD_USER_AUDIT_ID.getValue())).thenReturn(0);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithFirstReceiptInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithFirstReceiptInsertionError...");

		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(), new HashMap<>(),
				RBVDProperties.FIELD_POLICY_RECEIPT_ID.getValue())).thenReturn(0);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithContractMovInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithContractMovInsertionError...");

		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue(), new HashMap<>())).
				thenReturn(0);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_MOV_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithParticipantsInsertionError() {

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		when(pisdR012.executeGetRolesByProductAndModality(BigDecimal.ONE, "01")).thenReturn(responseQueryRoles);

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

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		Map<String, Object> argumentsForRoles = new HashMap<>();
		argumentsForRoles.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(), BigDecimal.ONE);
		argumentsForRoles.put(RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(), "01");

		when(pisdR012.executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSRNC_ROLE_MODALITY.getValue(), argumentsForRoles)).
				thenReturn(responseQueryRoles);

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

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithContractUpdateError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithContractUpdateError...");

		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT", new HashMap<>(),
				RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue(), RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue(),
				RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue(), RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue())).thenReturn(0);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError...");

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveReceiptsArguments(anyList())).thenReturn(arguments);

		when(pisdR012.executeSaveReceipts(any())).thenReturn(null);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithVehicularProductOK() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithVehicularProductOK...");

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveReceiptsArguments(anyList())).thenReturn(arguments);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNotNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithHomeProductOK() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithHomeProductOK...");

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveReceiptsArguments(anyList())).thenReturn(arguments);

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

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNotNull(validation);
	}

	@Test
	public void eexecuteBusinessLogicEmissionPrePolicySetOrganizationTest() {
		LOGGER.info("RBVDR211Test - Executing eexecuteBusinessLogicEmissionPrePolicySetOrganizationTest...");

		Map<String, Object>[] arguments = new Map[1];
		arguments[0] = new HashMap<>();

		when(mapperHelper.createSaveReceiptsArguments(anyList())).thenReturn(arguments);

		this.requestBody.getBank().getBranch().setId("7794");
		requestBody.setProductId("833");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		this.requestBody.getValidityPeriod().setStartDate(calendar.getTime());
		this.requestBody.getBank().getBranch().setId("0057");
		this.requestBody.setSaleChannelId("BI");

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

		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);

		when(pisdR021.executeGetHomeInfoForEmissionService(any())).thenReturn(responseGetHomeInfoForEmissionService);

		when(pisdR021.executeGetHomeRiskDirection(anyString())).thenReturn(responseGetHomeRiskDirectionService);

		when(rbvdr201.executeGetListBusinesses(anyString(), anyString())).thenReturn(null);

		customerList.getData().get(0).getIdentityDocuments().get(0).getDocumentType().setId("RUC");
		customerList.getData().get(0).getIdentityDocuments().get(0).setDocumentNumber("20999999991");

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);

		ListBusinessesASO businesses = new ListBusinessesASO();
		BusinessASO business = new BusinessASO();
		FormationASO formation = new FormationASO();
		formation.setCountry(new CountryASO());
		business.setBusinessDocuments(Collections.singletonList(new BusinessDocumentASO()));
		business.setFormation(formation);
		business.setAnnualSales(new SaleASO());
		business.setBusinessGroup(new BusinessGroupASO());
		business.setEconomicActivity(new EconomicActivityASO());
		businesses.setData(Collections.singletonList(business));

		when(rbvdr201.executeGetListBusinesses(anyString(), anyString())).thenReturn(businesses);

		validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNotNull(validation);
	}
}
