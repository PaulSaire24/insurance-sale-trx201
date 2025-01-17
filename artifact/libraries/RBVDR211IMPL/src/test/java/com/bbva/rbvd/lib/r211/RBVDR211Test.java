package com.bbva.rbvd.lib.r211;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.ksmk.dto.caas.OutputDTO;

import com.bbva.ksmk.lib.r002.KSMKR002;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;

import com.bbva.pisd.dto.insurance.mock.MockDTO;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.pisd.lib.r012.PISDR012;

import com.bbva.pisd.lib.r401.PISDR401;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.insrncsale.aso.*;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.*;

import com.bbva.rbvd.dto.insrncsale.commons.DocumentTypeDTO;
import com.bbva.rbvd.dto.insrncsale.commons.IdentityDocumentDTO;

import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;

import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantTypeDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;

import com.bbva.rbvd.dto.insrncsale.utils.PersonTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.lib.r211.impl.RBVDR211Impl;
import com.bbva.rbvd.lib.r211.impl.util.ConstantsUtil;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
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
	private KSMKR002 ksmkr002;
	private MapperHelper mapperHelper;
	private PISDR350 pisdr350;
	private PISDR401 pisdr401;

	private PolicyDTO requestBody;

	private Map<String, Object>[] argumentsForMultipleInsertion;

	private Map<String, Object> argumentValidateIfPolicyExists;
	private Map<String, Object> responseValidateIfPolicyExists;
	private Map<String, Object> responseQueryGetRequiredFields;
	private Map<String, Object> argumentsUpdateEndorsementTable;

	private Map<String, Object> responseQueryRoles;
	private List<Map<String, Object>> roles;
	private Map<String, Object> firstRole;

	private PolicyASO asoResponse;
	private EmisionBO rimacResponse;

	private CustomerListASO customerList;
	private Map<String,Object> responseQueryGetProductById;

	@Before
	public void setUp() throws IOException {
		ThreadContext.set(new Context());

		mockData = MockData.getInstance();

		applicationConfigurationService = mock(ApplicationConfigurationService.class);
		rbvdr201 = mock(RBVDR201.class);
		pisdR012 = mock(PISDR012.class);
		ksmkr002 = mock(KSMKR002.class);
		mapperHelper = mock(MapperHelper.class);
		pisdr401 = mock(PISDR401.class);
		pisdr350 = mock(PISDR350.class);

		rbvdr211.setApplicationConfigurationService(applicationConfigurationService);
		rbvdr211.setRbvdR201(rbvdr201);
		rbvdr211.setPisdR012(pisdR012);
		rbvdr211.setKsmkR002(ksmkr002);
		rbvdr211.setMapperHelper(mapperHelper);
		rbvdr211.setPisdR350(pisdr350);
		rbvdr211.setPisdR401(pisdr401);

		requestBody = mockData.getCreateInsuranceRequestBody();
		requestBody.setSaleChannelId("XX");

		argumentsForMultipleInsertion = new Map[1];
		argumentsForMultipleInsertion[0] = new HashMap<>();

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
		when(responseQueryGetRequiredFields.get(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue())).thenReturn("DESEMPLEO_PRESTAMO");

		responseQueryRoles = mock(Map.class);
		roles = mock(List.class);
		firstRole = mock(Map.class);

		argumentsUpdateEndorsementTable = new HashMap<>();
		argumentsUpdateEndorsementTable.put(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue(), "957968");
		argumentsUpdateEndorsementTable.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), "0000001102");

		when(this.applicationConfigurationService.getProperty("pisd.channel.contact.detail.aap")).thenReturn("13000013");
		when(this.applicationConfigurationService.getProperty("pisd.channel.glomo.aap")).thenReturn("13000013");
		when(this.applicationConfigurationService.getProperty("telemarketing.code")).thenReturn("7794");
		when(this.applicationConfigurationService.getProperty("pic.code")).thenReturn("PC");
		when(this.applicationConfigurationService.getProperty("cc.code")).thenReturn("CC");
		when(this.applicationConfigurationService.getProperty("agent.and.promoter.code")).thenReturn(AGENT_AND_PROMOTER_DEFAULT_CODE);
		when(this.applicationConfigurationService.getProperty("ENDOSATARIO_RUC")).thenReturn("00000000000");
		when(this.applicationConfigurationService.getProperty("ENDOSATARIO_PORCENTAJE")).thenReturn("40");
		when(this.applicationConfigurationService.getDefaultProperty("property.validation.range.830.NN", "0")).thenReturn("1");
		when(this.applicationConfigurationService.getDefaultProperty("property.validation.range.830.XX", "0")).thenReturn("0");
		when(this.applicationConfigurationService.getDefaultProperty("property.validation.range.832.XX", "0")).thenReturn("0");
		when(this.applicationConfigurationService.getDefaultProperty("property.validation.range.833.BI", "0")).thenReturn("0");
		when(this.applicationConfigurationService.getDefaultProperty("property.validation.range.840.PC", "0")).thenReturn("0");
		when(this.applicationConfigurationService.getDefaultProperty("property.validation.range.841.PC", "0")).thenReturn("0");
		when(this.applicationConfigurationService.getDefaultProperty("property.range.payment.amount.insurance", "5")).thenReturn("5");
		when(this.applicationConfigurationService.getProperty("product.codes.without.third.party.validation")).thenReturn("841");

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
		when(mapperHelper.mapRimacEmisionRequest(anyObject(), anyObject(), anyMap(),anyMap(), anyObject())).thenReturn(generalEmisionRequest);
		when(mapperHelper.generateRimacRequestLife(anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(), anyObject(), anyString())).thenReturn(generalEmisionRequest);
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

		when(mapperHelper.createSaveReceiptsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

		when(pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(), argumentsForMultipleInsertion)).
				thenReturn(new int[2]);

		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue(), new HashMap<>())).
				thenReturn(1);

		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(rimacResponse);

		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT", new HashMap<>(),
				RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue(), RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue(),
				RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue(), RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue())).thenReturn(1);

		when(pisdR012.executeMultipleInsertionOrUpdate("PISD.UPDATE_EXPIRATION_DATE_RECEIPTS", argumentsForMultipleInsertion)).
				thenReturn(new int[2]);

		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", argumentsUpdateEndorsementTable,
				RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue())).thenReturn(1);
		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", new HashMap<>())).thenReturn(1);

		responseQueryGetProductById = new HashMap<>();
		responseQueryGetProductById.put("INSURANCE_BUSINESS_NAME","VIDA");
		responseQueryGetProductById.put("PRODUCT_SHORT_DESC","VIDADINAMICO");

		when(pisdr401.executeGetProductById("PISD.SELECT_PRODUCT_BY_PRODUCT_TYPE",
				singletonMap(RBVDProperties.FIELD_INSURANCE_PRODUCT_TYPE.getValue(),requestBody.getProductId())))
				.thenReturn(responseQueryGetProductById);

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
	public void executeBusinessLogicEmissionPrePolicyWitContractInsertionError() throws IOException {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWitContractInsertionError...");

		requestBody.getHolder().getContactDetails().get(0).setId("C001985478569");
		requestBody.getHolder().getContactDetails().get(0).setContact(null);
		requestBody.getHolder().getContactDetails().get(1).setId("EMAIL001");
		requestBody.getHolder().getContactDetails().get(1).setContact(null);
		requestBody.setAap("13000013");

		when(applicationConfigurationService.getProperty("pisd.channel.contact.detail.aap")).thenReturn("13000013");
		when(applicationConfigurationService.getProperty("pisd.channel.glomo.aap")).thenReturn("13000013");

		GetContactDetailsASO contactDetailsResponse = mockDTO.getContactDetailsResponse();

		when(rbvdr201.executeGetContactDetailsService(anyString())).thenReturn(contactDetailsResponse);

		OutputDTO firstOutput = new OutputDTO();
		firstOutput.setData("emhSTGcxRnM");
		when(ksmkr002.executeKSMKR002(anyList(), anyString(), anyString(), anyObject())).thenReturn(singletonList(firstOutput));

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
	public void executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError...");
		when(pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(), argumentsForMultipleInsertion)).
				thenReturn(new int[0]);

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

		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

		when(pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSRNC_CTR_PARTICIPANT.getValue(), argumentsForMultipleInsertion)).
				thenReturn(new int[0]);

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

		when(pisdR012.executeGetRolesByProductAndModality(BigDecimal.ONE, "01")).thenReturn(responseQueryRoles);

		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

		when(pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSRNC_CTR_PARTICIPANT.getValue(), argumentsForMultipleInsertion)).
				thenReturn(new int[2]);
		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

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

		requestBody.getParticipants().set(1, secondParticipant);

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

		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithReceiptsUpdateError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithReceiptsUpdateError...");

		when(pisdR012.executeMultipleInsertionOrUpdate("PISD.UPDATE_EXPIRATION_DATE_RECEIPTS", argumentsForMultipleInsertion)).
				thenReturn(null);
		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);
		when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt", "")).thenReturn("");

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithEndorsementUpdateError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithEndorsementUpdateError...");

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

		requestBody.getParticipants().set(1, secondParticipant);

		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_POLICY_ENDORSEMENT.getValue(), new HashMap<>())).
				thenReturn(1);

		Map<String, Object> filters = new HashMap<>();
		filters.put(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue(), "957968");
		filters.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), "0000001102");

		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", filters,
				RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue())).thenReturn(0);
		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);
		when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt", "")).thenReturn("");

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithVehicularProductOK() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithVehicularProductOK...");

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

		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_POLICY_ENDORSEMENT.getValue(), new HashMap<>())).
				thenReturn(1);
		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);
		when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt", "")).thenReturn("");

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNotNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithHomeProductOK() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithHomeProductOK...");

		requestBody.setProductId("832");

		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);
		when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt", "")).thenReturn("");

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

		requestBody.setRelatedContracts(null);
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);


		assertNotNull(validation);
		PolicyDTO validation2 = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNotNull(validation2);
	}

	@Test
	public void eexecuteBusinessLogicEmissionPrePolicySetOrganizationTest() {
		LOGGER.info("RBVDR211Test - Executing eexecuteBusinessLogicEmissionPrePolicySetOrganizationTest...");

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

		when(rbvdr201.executeGetListBusinesses(anyString(), anyString())).thenReturn(null);

		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

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
		when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt", "")).thenReturn("");

		validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNotNull(validation);
	}

	@Test
	public void eexecuteBusinessLogicEmissionPrePolicySetOrganizatioFieldsNullsTest() {
		LOGGER.info("RBVDR211Test - Executing eexecuteBusinessLogicEmissionPrePolicySetOrganizationTest...");

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

		when(rbvdr201.executeGetListBusinesses(anyString(), anyString())).thenReturn(null);

		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

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

		businesses.getData().get(0).setEconomicActivity(null);
		businesses.getData().get(0).setBusinessGroup(null);
		businesses.getData().get(0).setAnnualSales(null);
		businesses.getData().get(0).setFormation(null);

		when(rbvdr201.executeGetListBusinesses(anyString(), anyString())).thenReturn(businesses);
		when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt", "")).thenReturn("");

		validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNotNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithError() {
		LOGGER.info("RBVDR211Test - Executing eexecuteBusinessLogicEmissionPrePolicySetOrganizationTest...");

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

		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);
		customerList.setData(new ArrayList<>());
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);

		when(rbvdr201.executeGetListBusinesses(anyString(), anyString())).thenReturn(null);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountQuotationMonthlyError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", new BigDecimal(130.00));
		responseBD.put("PREMIUM_CURRENCY_ID", "USD");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "M");
		requestBody.getTotalAmount().setAmount(1560d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyEasyYesWithAmountQuotationMonthlyError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyEasyYesWithAmountQuotationMonthlyError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", new BigDecimal(130.00));
		responseBD.put("PREMIUM_CURRENCY_ID", "PEN");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "M");
		requestBody.getTotalAmount().setAmount(1560d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}


	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountTotalAmountMonthlyError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", new BigDecimal(130.00));
		responseBD.put("PREMIUM_CURRENCY_ID", "USD");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "M");
		requestBody.getFirstInstallment().getPaymentAmount().setAmount(100d);
		requestBody.getTotalAmount().setAmount(1700d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountQuotationMonthlyCurrencyError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", new BigDecimal(130.00));
		responseBD.put("PREMIUM_CURRENCY_ID", "PEN");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "M");
		requestBody.getFirstInstallment().getPaymentAmount().setAmount(100d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountTotalAmountCurrencyMonthlyError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", new BigDecimal(130.00));
		responseBD.put("PREMIUM_CURRENCY_ID", "PEN");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "M");
		requestBody.getFirstInstallment().getPaymentAmount().setAmount(100d);
		requestBody.getTotalAmount().setAmount(1700d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountQuotationAnnualError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", new BigDecimal(1000));
		responseBD.put("PREMIUM_CURRENCY_ID", "USD");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "A");
		requestBody.getFirstInstallment().getPaymentAmount().setAmount(300d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountTotalAmountAnnualError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", new BigDecimal(1000));
		responseBD.put("PREMIUM_CURRENCY_ID", "USD");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "A");
		requestBody.getFirstInstallment().getPaymentAmount().setAmount(300d);
		requestBody.getTotalAmount().setAmount(300d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountQuotationAnnualCurrencyError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", new BigDecimal(1000));
		responseBD.put("PREMIUM_CURRENCY_ID", "PEN");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "A");
		requestBody.getFirstInstallment().getPaymentAmount().setAmount(300d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountTotalAmountCurrencyAnnualError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", new BigDecimal(1000));
		responseBD.put("PREMIUM_CURRENCY_ID", "PEN");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "A");
		requestBody.getFirstInstallment().getPaymentAmount().setAmount(300d);
		requestBody.getTotalAmount().setAmount(300d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountQuotationMonthlyNull() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", null);
		responseBD.put("PREMIUM_CURRENCY_ID", null);
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", null);
		requestBody.getTotalAmount().setAmount(1560d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.QUERY_EMPTY_RESULT.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountQuotationMonthlyNullTwo() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", null);
		responseBD.put("PREMIUM_CURRENCY_ID", "USD");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", null);
		requestBody.getTotalAmount().setAmount(1560d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.QUERY_EMPTY_RESULT.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountQuotationMonthlyNullThree() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationError...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT", null);
		responseBD.put("PREMIUM_CURRENCY_ID", "USD");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "M");
		requestBody.getTotalAmount().setAmount(1560d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.QUERY_EMPTY_RESULT.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountQuotationMonthlyNullFour() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationMonthlyNullFour...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT",  new BigDecimal(100));
		responseBD.put("PREMIUM_CURRENCY_ID", "USD");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "M");
		requestBody.getFirstInstallment().getPaymentAmount().setAmount(100d);
		requestBody.getTotalAmount().setAmount(1200d);
		requestBody.getInstallmentPlan().getPaymentAmount().setAmount(10000d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAmountQuotationMonthlyOK() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAmountQuotationMonthlyOK...");

		Map<String,Object> responseBD = new HashMap<>();
		responseBD.put("PREMIUM_AMOUNT",  new BigDecimal(100));
		responseBD.put("PREMIUM_CURRENCY_ID", "USD");
		responseBD.put("POLICY_PAYMENT_FREQUENCY_TYPE", "M");
		responseBD.put("OPERATION_GLOSSARY_DESC", "DESEMPLEO");
		requestBody.getFirstInstallment().getPaymentAmount().setAmount(100d);
		requestBody.getTotalAmount().setAmount(1200d);
		requestBody.getInstallmentPlan().getPaymentAmount().setAmount(100d);
		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseBD);
		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);
		when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt", "")).thenReturn("");

		requestBody.setSaleChannelId("NN");
		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);
		assertNotNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithLifeEasyYesOK() throws  IOException{
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithLifeEasyYesOK...");
		AgregarTerceroBO responseAddParticipants = mockData.getAddParticipantsRimacResponse();
		EmisionBO responseEmission = mockData.getEmissionRimacResponseLife();

		requestBody.setProductId("840");
		requestBody.setSaleChannelId("PC");
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);
		when(rbvdr201.executeAddParticipantsService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseAddParticipants);
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseEmission);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNotNull(validation);
	}


	@Test
	public void executeBusinessLogicEmissionPrePolicyDynamicLifeProduct_WithInsuredParticipant() throws  IOException{
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyDynamicLifeProduct_WithInsuredParticipant...");
		AgregarTerceroBO responseAddParticipants = mockData.getAddParticipantsRimacResponse();
		EmisionBO responseEmission = mockData.getEmissionRimacResponseLife();
		ParticipantDTO insured = new ParticipantDTO();
		insured.setCustomerId("84948543");
		ParticipantTypeDTO participantTypeDTO = new ParticipantTypeDTO();
		participantTypeDTO.setId(ConstantsUtil.Participant.INSURED);
		insured.setParticipantType(participantTypeDTO);
		IdentityDocumentDTO identityDocumentDTO = new IdentityDocumentDTO();
		identityDocumentDTO.setNumber("494830484");
		DocumentTypeDTO documentTypeDTO = new DocumentTypeDTO();
		documentTypeDTO.setId("DNI");
		identityDocumentDTO.setDocumentType(documentTypeDTO);
		insured.setIdentityDocument(identityDocumentDTO);
		requestBody.getParticipants().add(insured);
		requestBody.setProductId("841");
		requestBody.setSaleChannelId("PC");

		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);
		when(rbvdr201.executeAddParticipantsService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseAddParticipants);
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseEmission);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);
		assertNotNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithLifeDynamic_WithEndorse() throws  IOException{
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithLifeDynamic_WithEndorse...");
		AgregarTerceroBO responseAddParticipants = mockData.getAddParticipantsRimacResponse();
		EmisionBO responseEmission = mockData.getEmissionRimacResponseLife();
		Map<String, Object> filters = new HashMap<>();
		filters.put(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue(), "1300009671");
		filters.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), "0000001102");

		requestBody.setProductId("841");
		requestBody.setSaleChannelId("PC");
		requestBody.getParticipants().add(getParticipantEndorse());
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);
		when(rbvdr201.executeAddParticipantsService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseAddParticipants);
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseEmission);
		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_POLICY_ENDORSEMENT.getValue(), new HashMap<>())).thenReturn(1);
		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", filters, RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue())).thenReturn(1);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNotNull(validation);

		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", filters, RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue())).thenReturn(0);

		validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNull(validation);
	}

	private static ParticipantDTO getParticipantEndorse() {
		ParticipantDTO participantEndorse = new ParticipantDTO();
		participantEndorse.setBenefitPercentage(new Double(100));
		ParticipantTypeDTO participantType = new ParticipantTypeDTO();
		participantType.setId("ENDORSEE");
		participantEndorse.setParticipantType(participantType);
		IdentityDocumentDTO identityDocument = new IdentityDocumentDTO();
		identityDocument.setNumber("20100130204");
		DocumentTypeDTO documentType = new DocumentTypeDTO();
		documentType.setId("RUC");
		identityDocument.setDocumentType(documentType);
		participantEndorse.setIdentityDocument(identityDocument);
		return participantEndorse;
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithAddParticipantsResponseNull() throws  IOException{
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAddParticipantsResponseNull...");
		EmisionBO responseEmission = mockData.getEmissionRimacResponseLife();

		requestBody.setProductId("840");
		requestBody.setSaleChannelId("PC");
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);
		when(rbvdr201.executeAddParticipantsService(anyObject(), anyString(), anyString(), anyString())).thenReturn(null);
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseEmission);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithPolicyAlreadyExistsError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithPolicyAlreadyExistsError...");

		Map<String, Object> policiesNumber = new HashMap<>();
		policiesNumber.put(RBVDProperties.FIELD_RESULT_NUMBER.getValue(), BigDecimal.ONE);

		when(pisdR012.executeGetASingleRow(RBVDProperties.QUERY_VALIDATE_IF_POLICY_EXISTS.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(policiesNumber);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.POLICY_ALREADY_EXISTS.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithParticipantsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithParticipantsInsertionError...");

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));
		when(roles.get(0)).thenReturn(firstRole);
		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);
		when(pisdR012.executeGetRolesByProductAndModality(BigDecimal.ONE, "01")).thenReturn(responseQueryRoles);
		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(argumentsForMultipleInsertion);
		when(pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSRNC_CTR_PARTICIPANT.getValue(), argumentsForMultipleInsertion)).
				thenReturn(new int[0]);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_PARTICIPANT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithNonExistentQuotation() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithNonExistentQuotation...");

		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(null);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.NON_EXISTENT_QUOTATION.getAdviceCode());
	}
	@Test
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWitContractInsertionError() throws IOException {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWitContractInsertionError...");

		requestBody.getHolder().getContactDetails().get(0).setId("C001985478569");
		requestBody.getHolder().getContactDetails().get(0).setContact(null);
		requestBody.getHolder().getContactDetails().get(1).setId("EMAIL001");
		requestBody.getHolder().getContactDetails().get(1).setContact(null);
		requestBody.setAap("13000013");

		when(applicationConfigurationService.getProperty("pisd.channel.contact.detail.aap")).thenReturn("13000013");
		when(applicationConfigurationService.getProperty("pisd.channel.glomo.aap")).thenReturn("13000013");

		GetContactDetailsASO contactDetailsResponse = mockDTO.getContactDetailsResponse();

		when(rbvdr201.executeGetContactDetailsService(anyString())).thenReturn(contactDetailsResponse);

		OutputDTO firstOutput = new OutputDTO();
		firstOutput.setData("emhSTGcxRnM");
		when(ksmkr002.executeKSMKR002(anyList(), anyString(), anyString(), anyObject())).thenReturn(singletonList(firstOutput));

		when(pisdR012.executeInsertSingleRow(PISDProperties.QUERY_INSERT_INSURANCE_CONTRACT.getValue(), new HashMap<>(),
				RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(),
				RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(), RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(),
				RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue(),
				RBVDProperties.FIELD_CUSTOMER_ID.getValue(), RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue(),
				RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), RBVDProperties.FIELD_USER_AUDIT_ID.getValue())).thenReturn(0);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithReceiptsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithReceiptsInsertionError...");

		when(pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(), argumentsForMultipleInsertion)).
				thenReturn(new int[0]);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithContractMovInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithContractMovInsertionError...");

		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue(), new HashMap<>())).
				thenReturn(0);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_MOV_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithError...");

		requestBody.setProductId("840");
		requestBody.setSaleChannelId("PC");

		customerList.setData(new ArrayList<>());
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithLifeEasyYesOK_ChannelTM() throws  IOException{
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithLifeEasyYesOK...");
		AgregarTerceroBO responseAddParticipants = mockData.getAddParticipantsRimacResponse();
		EmisionBO responseEmission = mockData.getEmissionRimacResponseLife();

		requestBody.setProductId("840");
		requestBody.setSaleChannelId("PC");
		requestBody.getBank().getBranch().setId("7794");
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);
		when(rbvdr201.executeAddParticipantsService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseAddParticipants);
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseEmission);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNotNull(validation);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithLifeEasyYesOK_DigitalChannel() throws  IOException{
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithLifeEasyYesOK...");
		AgregarTerceroBO responseAddParticipants = mockData.getAddParticipantsRimacResponse();
		EmisionBO responseEmission = mockData.getEmissionRimacResponseLife();

		requestBody.setProductId("840");
		requestBody.setSaleChannelId("BI");
		requestBody.setPromoter(null);

		when(this.applicationConfigurationService.getDefaultProperty("property.validation.range.840.BI", "0")).thenReturn("0");
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);
		when(rbvdr201.executeAddParticipantsService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseAddParticipants);
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseEmission);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(requestBody);

		assertNotNull(validation);
	}

}