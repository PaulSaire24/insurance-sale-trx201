package com.bbva.rbvd.lib.r211;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.ksmk.dto.caas.OutputDTO;
import com.bbva.ksmk.lib.r002.KSMKR002;
import com.bbva.pisd.dto.contract.constants.PISDErrors;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurancedao.entities.ContractEntity;
import com.bbva.pisd.dto.insurancedao.entities.QuotationEntity;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r226.PISDR226;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.pisd.lib.r401.PISDR401;
import com.bbva.pisd.lib.r601.PISDR601;
import com.bbva.rbvd.dto.insrncsale.aso.*;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.*;
import com.bbva.rbvd.dto.insrncsale.commons.DocumentTypeDTO;
import com.bbva.rbvd.dto.insrncsale.commons.IdentityDocumentDTO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.events.CreatedInsuranceDTO;
import com.bbva.rbvd.dto.insrncsale.events.StatusDTO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.*;
import com.bbva.rbvd.dto.insrncsale.utils.PersonTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Date;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR211-app.xml",
		"classpath:/META-INF/spring/RBVDR211-app-v2-test.xml",
		"classpath:/META-INF/spring/RBVDR211-arc.xml",
		"classpath:/META-INF/spring/RBVDR211-arc-test.xml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RBVDR211NotLifeTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211NotLifeTest.class);
	private static final String AGENT_AND_PROMOTER_DEFAULT_CODE = "UCQGSPPP";

	@Spy
	private Context context;

	@Resource(name = "rbvdR211")
	private RBVDR211 rbvdr211;

	private MockData mockData;
	private MockDTO mockDTO;

	@Resource(name = "applicationConfigurationService")
	private ApplicationConfigurationService applicationConfigurationService;

	@Resource(name = "rbvdR201")
	private RBVDR201 rbvdr201;

	@Resource(name = "pisdR012")
	private PISDR012 pisdR012;
	@Resource(name = "pisdR601")
	private PISDR601 pisdR601;

	@Resource(name = "ksmkR002")
	private KSMKR002 ksmkr002;
	@Resource(name = "mapperHelper")
	private MapperHelper mapperHelper;

	@Resource(name = "pisdR350")
	private PISDR350 pisdr350;

	@Resource(name = "pisdR401")
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

	@Resource(name = "pisdR226")
	private PISDR226 pisdR226;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		context = new Context();
		ThreadContext.set(context);
		getObjectIntrospection();
		loadingContextConfiguration();
	}

	void loadingContextConfiguration() throws IOException {
		mockData = MockData.getInstance();

		requestBody = mockData.getCreateInsuranceRequestBody();
		requestBody.setSaleChannelId("XX");
		requestBody.setRelatedContracts(new ArrayList<>());
		RelatedContractDTO relatedContractDTO = new RelatedContractDTO();

		// Crear y configurar el objeto RelatedContractProductDTO
		RelatedContractProductDTO productDTO = new RelatedContractProductDTO();
		productDTO.setId("product_id");
		relatedContractDTO.setProduct(productDTO);

		// Crear y configurar el objeto RelationTypeDTO (si es necesario)
		RelationTypeDTO relationTypeDTO = new RelationTypeDTO();
		// Configurar los atributos según sea necesario
		relatedContractDTO.setRelationType(relationTypeDTO);

		// Crear y configurar el objeto ContractDetailsDTO
		ContractDetailsDTO contractDetailsDTO = new ContractDetailsDTO();
		contractDetailsDTO.setContractType("internal");
		// Configurar otros atributos según sea necesario
		relatedContractDTO.setContractDetails(contractDetailsDTO);
		requestBody.getRelatedContracts().add(relatedContractDTO);

		argumentsForMultipleInsertion = new Map[1];
		argumentsForMultipleInsertion[0] = new HashMap<>();

		mockDTO = MockDTO.getInstance();
		customerList = mockDTO.getCustomerDataResponse();

		argumentValidateIfPolicyExists = new HashMap<>();
		argumentValidateIfPolicyExists.put(RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue(), "0814000000366");

		responseValidateIfPolicyExists = mock(Map.class);
		when(responseValidateIfPolicyExists.get(RBVDProperties.FIELD_RESULT_NUMBER.getValue())).thenReturn(BigDecimal.ZERO);

		responseQueryGetRequiredFields = new HashMap<>();
		responseQueryGetRequiredFields.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(),BigDecimal.valueOf(1));
		responseQueryGetRequiredFields.put(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue(),BigDecimal.valueOf(12));
		responseQueryGetRequiredFields.put(RBVDProperties.FIELD_PAYMENT_FREQUENCY_ID.getValue(),BigDecimal.valueOf(1));
		responseQueryGetRequiredFields.put(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue(),"rimacQuotation");
		responseQueryGetRequiredFields.put(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue(),"DESEMPLEO_PRESTAMO");
		responseQueryGetRequiredFields.put(RBVDProperties.FIELD_CONTRACT_DURATION_TYPE.getValue(), "A");
		responseQueryGetRequiredFields.put(PISDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue(), "Insurance Product Description");
		responseQueryGetRequiredFields.put(PISDProperties.FIELD_INSURANCE_MODALITY_NAME.getValue(), "Insurance Modality Name");
		responseQueryGetRequiredFields.put(PISDProperties.FIELD_VEHICLE_BRAND_NAME.getValue(), "Toyota");
		responseQueryGetRequiredFields.put(PISDProperties.FIELD_VEHICLE_MODEL_NAME.getValue(), "Corolla");
		responseQueryGetRequiredFields.put(PISDProperties.FIELD_VEHICLE_YEAR_ID.getValue(), "2022");
		responseQueryGetRequiredFields.put(PISDProperties.FIELD_VEHICLE_LICENSE_ID.getValue(), "ABC123");
		responseQueryGetRequiredFields.put(PISDProperties.FIELD_VEHICLE_GAS_CONVERSION_TYPE.getValue(), "CNG");
		responseQueryGetRequiredFields.put(PISDProperties.FIELD_VEHICLE_CIRCULATION_SCOPE_TYPE.getValue(), "Urban");
		responseQueryGetRequiredFields.put(PISDProperties.FIELD_COMMERCIAL_VEHICLE_AMOUNT.getValue(), new BigDecimal(1));


		responseQueryRoles = mock(Map.class);
		roles = mock(List.class);
		firstRole = mock(Map.class);

		argumentsUpdateEndorsementTable = new HashMap<>();
		argumentsUpdateEndorsementTable.put(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue(), "957968");
		argumentsUpdateEndorsementTable.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), "0000001102");

		when(this.applicationConfigurationService.getProperty("pisd.channel.contact.detail.aap")).thenReturn("13000013");
		when(this.applicationConfigurationService.getProperty("pisd.channel.glomo.aap")).thenReturn("13000013");
		when(this.applicationConfigurationService.getDefaultProperty(Mockito.eq("telemarketing.code"),Mockito.eq(StringUtils.EMPTY))).thenReturn("7794");
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
		when(this.applicationConfigurationService.getDefaultProperty("property.validation.range.842.PC", "0")).thenReturn("0");
		when(this.applicationConfigurationService.getDefaultProperty("property.range.payment.amount.insurance", "5")).thenReturn("5");
		when(this.applicationConfigurationService.getDefaultProperty(eq("MONTHLY"),eq(StringUtils.EMPTY))).thenReturn("M");
		when(this.applicationConfigurationService.getDefaultProperty(eq("flow.royal2.enabled.all.products"),eq(Boolean.FALSE.toString()))).thenReturn(Boolean.TRUE.toString());
		when(this.applicationConfigurationService.getDefaultProperty(eq("flow.royal2.enabled.all.products.life"),eq(Boolean.FALSE.toString()))).thenReturn(Boolean.TRUE.toString());

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
		when(mapperHelper.mapRimacNoLifeEmisionRequest(anyObject(), anyObject(), anyMap(),anyMap())).thenReturn(generalEmisionRequest);
		when(mapperHelper.generateRimacRequestLife(anyString(),anyString(),anyString(),anyString(),anyString(),anyString(),anyString(), anyObject(), anyString())).thenReturn(generalEmisionRequest);
		when(mapperHelper.getPersonType(anyObject())).thenReturn(PersonTypeEnum.NATURAL);
		when(pisdR012.executeGetASingleRow(RBVDProperties.QUERY_VALIDATE_IF_POLICY_EXISTS.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(responseValidateIfPolicyExists);
		when(pisdR012.executeGetASingleRow(Mockito.eq(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue()), Mockito.anyMap())).
				thenReturn(responseQueryGetRequiredFields);

		when(rbvdr201.executeCypherService(anyObject())).thenReturn("");

		/* P030557 */
		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);
		when(rbvdr201.executePreFormalizationContract(anyObject(),any())).thenReturn(ResponseLibrary.ResponseServiceBuilder
				.an().statusIndicatorProcess(RBVDInternalConstants.Status.OK).body(asoResponse));

		when(rbvdr201.executeInsurancePaymentAndFormalization(anyObject(),any())).thenReturn(ResponseLibrary.ResponseServiceBuilder
				.an().statusIndicatorProcess(RBVDInternalConstants.Status.OK).body(asoResponse));
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(rimacResponse);
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(rimacResponse);

		when(pisdR012.executeInsertSingleRow(eq(PISDProperties.QUERY_INSERT_INSURANCE_CONTRACT.getValue()), Mockito.anyMap(),
				eq(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue()),
				eq(RBVDProperties.FIELD_CUSTOMER_ID.getValue()),
				eq(RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue()),
				eq(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()),
				eq(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()))).thenReturn(1);

		when(mapperHelper.createSaveReceiptsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

		int[] array = new int[12];
		array[0] = 1;
		array[1] = 1;
		array[2] = 1;
		array[3] = 1;
		array[4] = 1;
		array[5] = 1;
		array[6] = 1;
		array[7] = 1;
		array[8] = 1;
		array[9] = 1;
		array[10] = 1;
		array[11] = 1;

		when(pisdR012.executeMultipleInsertionOrUpdate(Mockito.eq(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue()), Mockito.any())).
				thenReturn(array);

		when(pisdR012.executeInsertSingleRow(eq(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue()), Mockito.anyMap())).
				thenReturn(1);



		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT", new HashMap<>(),
				RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue(), RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue(),
				RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue(), RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue())).thenReturn(1);


		int[] array2 = new int[2];
		array2[0] = 1;
		when(pisdR012.executeMultipleInsertionOrUpdate(Mockito.eq("PISD.UPDATE_EXPIRATION_DATE_RECEIPTS"), Mockito.any())).
				thenReturn(array2);

		when(pisdR012.executeMultipleInsertionOrUpdate(Mockito.eq(RBVDProperties.QUERY_INSERT_INSURANCE_CONTRACT_DETAILS.getValue()), Mockito.any())).thenReturn(new int[2]);

		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", argumentsUpdateEndorsementTable,
				RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue())).thenReturn(1);
		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", new HashMap<>())).thenReturn(1);

		responseQueryGetProductById = new HashMap<>();
		responseQueryGetProductById.put("INSURANCE_BUSINESS_NAME","VIDA");
		responseQueryGetProductById.put("PRODUCT_SHORT_DESC","VIDADINAMICO");

		when(pisdr401.executeGetProductById(Mockito.eq("PISD.SELECT_PRODUCT_BY_PRODUCT_TYPE"), Mockito.anyMap()))
				.thenReturn(responseQueryGetProductById);

		/* P030557 */
		when(pisdR012.executeGetASingleRow(eq(RBVDProperties.QUERY_SELECT_PAYMENT_PERIOD.getValue()), Mockito.anyMap())).
				thenReturn(responseQueryGetProductById);
		when(applicationConfigurationService.getDefaultProperty(Mockito.eq("obtain.sale.channels.list"),Mockito.eq(StringUtils.EMPTY))).thenReturn("PC;CC");
		when(applicationConfigurationService.getDefaultProperty(Mockito.eq("obtain.aaps.config.contact.details"),Mockito.eq(StringUtils.EMPTY))).thenReturn("13000013;13000013");
		when(applicationConfigurationService.getDefaultProperty(eq("products.modalities.only.first.receipt"), Mockito.anyString())).thenReturn("");
		OutputDTO firstOutput = new OutputDTO();
		firstOutput.setData("emhSTGcxRnM");
		when(ksmkr002.executeKSMKR002(anyList(), anyString(), anyString(), anyObject())).thenReturn(singletonList(firstOutput));

		CreatedInsrcEventDTO createdInsrcEventDTO = new CreatedInsrcEventDTO();
		createdInsrcEventDTO.setCreatedInsurance(new CreatedInsuranceDTO());
		createdInsrcEventDTO.getCreatedInsurance().setStatus(new StatusDTO());
		when(mapperHelper.buildCreatedInsuranceEventObject(anyObject())).thenReturn(createdInsrcEventDTO);

		QuotationEntity quotationEntity = new QuotationEntity();
		quotationEntity.setRfqInternalId("R05658");
		quotationEntity.setPayrollId("P05658");
		when(pisdR601.executeFindQuotationByReferenceAndPayrollId(anyString())).thenReturn(quotationEntity);
		when(this.applicationConfigurationService.getDefaultProperty("invoke.participant.validation.emission.noLife.legal.833.BI", "true")).thenReturn("true");
		when(this.applicationConfigurationService.getDefaultProperty("invoke.participant.validation.emission.noLife.natural.833.BI", "true")).thenReturn("true");
		when(this.applicationConfigurationService.getDefaultProperty("flow.royal2.enabled.payment.icr3", "true")).thenReturn("true");

		when(this.pisdR226.executeUpdateInsuranceContractByCertifyBank(Mockito.anyObject()))
				.thenReturn(true);

	}


	private Object getObjectIntrospection() throws Exception{
		Object result = this.rbvdr211;
		if(this.rbvdr211 instanceof Advised){
			Advised advised = (Advised) this.rbvdr211;
			result = advised.getTargetSource().getTarget();
		}
		return result;
	}

	@Test
	public void execute_flow_pre_emission_with_error_not_result_null_exception() {
		/**
		 * Preparación de data
		 * */
		this.requestBody.setId("00110284764000025977");
		String messageExceptionNotResultExpected = "El contrato '00110284764000025977' no se encuentra en nuestra base de datos. Por favor, verifica la información proporcionada y vuelve a intentarlo.";
		/**
		 * Mocks
		 * */
		when(this.pisdR226.executeFindByCertifiedBank(Mockito.anyObject()))
				.thenReturn(null);

		/**
		 * Ejecución de proceso
		 * */

		ResponseLibrary<PolicyDTO> contractAndPolicyGenerated = this.rbvdr211.executeEmissionPolicyNotLifeFlowNew(this.requestBody);
		assertNull(contractAndPolicyGenerated.getBody());
		assertEquals(RBVDInternalConstants.Status.EWR,contractAndPolicyGenerated.getStatusProcess());
		assertEquals(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS,contractAndPolicyGenerated.getFlowProcess());
		assertTrue(this.context.getAdviceList().stream().anyMatch(advice -> RBVDInternalErrors.ERROR_NOT_RESULT_CONTRACT.getAdviceCode().equalsIgnoreCase(advice.getCode())));
		assertEquals(messageExceptionNotResultExpected,	this.context.getAdviceList().get(0).getDescription());
		assertEquals(1,this.context.getAdviceList().size());

	}

	@Test
	public void execute_flow_pre_emission_with_error_not_result_exception() {
		/**
		 * Preparación de data
		* */
		this.requestBody.setId("00110284764000025977");
		String messageExceptionNotResultExpected = "El contrato '00110284764000025977' no se encuentra en nuestra base de datos. Por favor, verifica la información proporcionada y vuelve a intentarlo.";
		/**
		 * Mocks
		 * */
		when(this.pisdR226.executeFindByCertifiedBank(Mockito.anyObject()))
				.thenThrow(new BusinessException(PISDErrors.QUERY_EMPTY_RESULT.getAdviceCode(),PISDErrors.QUERY_EMPTY_RESULT.isRollback(),PISDErrors.QUERY_EMPTY_RESULT.getMessage()));
		/**
		 * Ejecución de proceso
		 * */

		ResponseLibrary<PolicyDTO> contractAndPolicyGenerated = this.rbvdr211.executeEmissionPolicyNotLifeFlowNew(this.requestBody);
		assertNull(contractAndPolicyGenerated.getBody());
		assertEquals(RBVDInternalConstants.Status.EWR,contractAndPolicyGenerated.getStatusProcess());
		assertEquals(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS,contractAndPolicyGenerated.getFlowProcess());
		assertTrue(this.context.getAdviceList().stream().anyMatch(advice -> RBVDInternalErrors.ERROR_NOT_RESULT_CONTRACT.getAdviceCode().equalsIgnoreCase(advice.getCode())));
		assertEquals(messageExceptionNotResultExpected,	this.context.getAdviceList().get(0).getDescription());
		assertEquals(1,this.context.getAdviceList().size());

	}

	@Test
	public void execute_flow_pre_emission_with_error_timeout_exception() {
		/**
		 * Preparación de data
		 * */
		this.requestBody.setId("00110284764000025977");
		String messageExceptionNotResultExpected = RBVDInternalErrors.ERROR_NOT_TIMEOUT_GENERAL.getMessage();
		/**
		 * Mocks
		 * */
		when(this.pisdR226.executeFindByCertifiedBank(Mockito.anyObject()))
				.thenThrow(new BusinessException(PISDErrors.ERROR_TIME_OUT.getAdviceCode(),PISDErrors.ERROR_TIME_OUT.isRollback(),PISDErrors.ERROR_TIME_OUT.getMessage()));
		/**
		 * Ejecución de proceso
		 * */

		ResponseLibrary<PolicyDTO> contractAndPolicyGenerated = this.rbvdr211.executeEmissionPolicyNotLifeFlowNew(this.requestBody);
		assertNull(contractAndPolicyGenerated.getBody());
		assertEquals(RBVDInternalConstants.Status.EWR,contractAndPolicyGenerated.getStatusProcess());
		assertEquals(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS,contractAndPolicyGenerated.getFlowProcess());
		assertTrue(this.context.getAdviceList().stream().anyMatch(advice -> RBVDInternalErrors.ERROR_NOT_TIMEOUT_GENERAL.getAdviceCode().equalsIgnoreCase(advice.getCode())));
		assertEquals(messageExceptionNotResultExpected,	this.context.getAdviceList().get(0).getDescription());
		assertEquals(1,this.context.getAdviceList().size());

	}

	@Test
	public void execute_flow_pre_emission_error_contract_status_id_for() {
		/**
		 * Preparación de data
		 * */
		this.requestBody.setId("00110284764000025977");
		String messageExceptionNotResultExpected = RBVDInternalErrors.ERROR_STATUS_CONTRACT_FOR.getMessage();
		/**
		 * Mocks
		 * */
		ContractEntity contractEntity = ContractEntity.ContractBuilder.an()
				.withContractStatusId(RBVDInternalConstants.CONTRACT_STATUS_ID.FOR.getValue()).build();
		when(this.pisdR226.executeFindByCertifiedBank(Mockito.anyObject()))
				.thenReturn(contractEntity);
		/**
		 * Ejecución de flujo con estado ContractStatusId = 'FOR'
		 * */
		ResponseLibrary<PolicyDTO> contractAndPolicyGenerated = this.rbvdr211.executeEmissionPolicyNotLifeFlowNew(this.requestBody);
		assertNull(contractAndPolicyGenerated.getBody());
		assertEquals(RBVDInternalConstants.Status.EWR,contractAndPolicyGenerated.getStatusProcess());
		assertEquals(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS,contractAndPolicyGenerated.getFlowProcess());
		assertTrue(this.context.getAdviceList().stream().anyMatch(advice -> RBVDInternalErrors.ERROR_STATUS_CONTRACT_FOR.getAdviceCode().equalsIgnoreCase(advice.getCode())));
		assertEquals(messageExceptionNotResultExpected,	this.context.getAdviceList().get(0).getDescription());
		assertEquals(1,this.context.getAdviceList().size());

	}

	@Test
	public void execute_flow_pre_emission_error_contract_status_id_baj() {
		/**
		 * Preparación de data
		 * */
		this.requestBody.setId("00110284764000025977");
		String messageExceptionNotResultExpected = RBVDInternalErrors.ERROR_STATUS_CONTRACT_BAJ.getMessage();
		/**
		 * Mocks
		 * */
		ContractEntity contractEntity = ContractEntity.ContractBuilder.an()
				.withContractStatusId(RBVDInternalConstants.CONTRACT_STATUS_ID.BAJ.getValue()).build();
		when(this.pisdR226.executeFindByCertifiedBank(Mockito.anyObject()))
				.thenReturn(contractEntity);
		/**
		 * Ejecución de flujo con estado ContractStatusId = 'FOR'
		 * */
		ResponseLibrary<PolicyDTO> contractAndPolicyGenerated = this.rbvdr211.executeEmissionPolicyNotLifeFlowNew(this.requestBody);
		assertNull(contractAndPolicyGenerated.getBody());
		assertEquals(RBVDInternalConstants.Status.EWR,contractAndPolicyGenerated.getStatusProcess());
		assertEquals(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS,contractAndPolicyGenerated.getFlowProcess());
		assertTrue(this.context.getAdviceList().stream().anyMatch(advice -> RBVDInternalErrors.ERROR_STATUS_CONTRACT_BAJ.getAdviceCode().equalsIgnoreCase(advice.getCode())));
		assertEquals(messageExceptionNotResultExpected,	this.context.getAdviceList().get(0).getDescription());
		assertEquals(1,this.context.getAdviceList().size());

	}

	@Test
	public void execute_flow_pre_emission_error_contract_status_id_anu() {
		/**
		 * Preparación de data
		 * */
		this.requestBody.setId("00110284764000025977");
		String messageExceptionNotResultExpected = RBVDInternalErrors.ERROR_STATUS_CONTRACT_ANU.getMessage();
		/**
		 * Mocks
		 * */
		ContractEntity contractEntity = ContractEntity.ContractBuilder.an()
				.withContractStatusId(RBVDInternalConstants.CONTRACT_STATUS_ID.ANU.getValue()).build();
		when(this.pisdR226.executeFindByCertifiedBank(Mockito.anyObject()))
				.thenReturn(contractEntity);
		/**
		 * Ejecución de flujo con estado ContractStatusId = 'FOR'
		 * */
		ResponseLibrary<PolicyDTO> contractAndPolicyGenerated = this.rbvdr211.executeEmissionPolicyNotLifeFlowNew(this.requestBody);
		assertNull(contractAndPolicyGenerated.getBody());
		assertEquals(RBVDInternalConstants.Status.EWR,contractAndPolicyGenerated.getStatusProcess());
		assertEquals(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS,contractAndPolicyGenerated.getFlowProcess());
		assertTrue(this.context.getAdviceList().stream().anyMatch(advice -> RBVDInternalErrors.ERROR_STATUS_CONTRACT_ANU.getAdviceCode().equalsIgnoreCase(advice.getCode())));
		assertEquals(messageExceptionNotResultExpected,	this.context.getAdviceList().get(0).getDescription());
		assertEquals(1,this.context.getAdviceList().size());

	}

	@Test
	public void execute_flow_pre_emission_error_contract_status_id_default() {
		/**
		 * Preparación de data
		 * */
		this.requestBody.setId("00110284764000025977");
		String messageExceptionNotResultExpected = "El contrato '00110284764000025977' esta en estado 'X' y no se puede realizar la preformalización. Por favor, verifica el contrato proporcionado .";
		/**
		 * Mocks
		 * */
		ContractEntity contractEntity = ContractEntity.ContractBuilder.an()
				.withContractStatusId("X").build();
		when(this.pisdR226.executeFindByCertifiedBank(Mockito.anyObject()))
				.thenReturn(contractEntity);
		/**
		 * Ejecución de flujo con estado ContractStatusId = 'FOR'
		 * */
		ResponseLibrary<PolicyDTO> contractAndPolicyGenerated = this.rbvdr211.executeEmissionPolicyNotLifeFlowNew(this.requestBody);
		assertNull(contractAndPolicyGenerated.getBody());
		assertEquals(RBVDInternalConstants.Status.EWR,contractAndPolicyGenerated.getStatusProcess());
		assertEquals(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS,contractAndPolicyGenerated.getFlowProcess());
		assertTrue(this.context.getAdviceList().stream().anyMatch(advice -> RBVDInternalErrors.ERROR_STATUS_CONTRACT_FLOW_PRE_FORMALIZATION.getAdviceCode().equalsIgnoreCase(advice.getCode())));
		assertEquals(messageExceptionNotResultExpected,	this.context.getAdviceList().get(0).getDescription());
		assertEquals(1,this.context.getAdviceList().size());

	}

	@Test
	public void execute_flow_pre_emission_sucessfull() {
		/**
		 * Preparación de data
		 * */
		this.requestBody.setId("00110284764000025977");
		/**
		 * Mocks
		 * */
		ContractEntity contractEntity = ContractEntity.ContractBuilder.an()
				.withContractStatusId(RBVDInternalConstants.CONTRACT_STATUS_ID.PEN.getValue()).build();
		when(this.pisdR226.executeFindByCertifiedBank(Mockito.anyObject()))
				.thenReturn(contractEntity);
		/**
		 * Ejecución de proceso
		 * */

		ResponseLibrary<PolicyDTO> contractAndPolicyGenerated = this.rbvdr211.executeEmissionPolicyNotLifeFlowNew(this.requestBody);
		assertNotNull(contractAndPolicyGenerated.getBody());
		assertEquals(RBVDInternalConstants.Status.OK,contractAndPolicyGenerated.getStatusProcess());
		assertEquals(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS,contractAndPolicyGenerated.getFlowProcess());
		assertEquals(0,this.context.getAdviceList().size());

	}

	@Test
	public void execute_create_emission_bank_successful_with_error_rimac() {

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


		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

		customerList.getData().get(0).getIdentityDocuments().get(0).getDocumentType().setId("RUC");
		customerList.getData().get(0).getIdentityDocuments().get(0).setDocumentNumber("20999999991");

		requestBody.getHolder().setId("123123123");

		ResponseLibrary<PolicyDTO> validation = null;



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

		when(this.rbvdr201.executeGetListBusinesses(anyString(), eq(null))).thenReturn(businesses);
		when(this.mapperHelper.constructPerson(any(), any(),any())).thenReturn(new PersonaBO());
		when(applicationConfigurationService.getDefaultProperty(eq("products.modalities.only.first.receipt"), Mockito.anyString())).thenReturn("");
        String messageError = "Ha ocurrido un evento inesperado en el servicio. Por favor, verifíquelo.";

        // Error Principle 1.0
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenThrow(new NullPointerException(messageError));

		validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		assertNotNull(validation.getBody());
		assertEquals(validation.getStatusProcess(),RBVDInternalConstants.Status.OK);
		assertEquals(validation.getFlowProcess(),RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS);
		assertTrue(context.getAdviceList().stream().anyMatch(advice -> RBVDInternalErrors.ERROR_GENERIC_APX_IN_CALLED_RIMAC.getAdviceCode().equalsIgnoreCase(advice.getCode())));
	}


	@Test
	public void executeBusinessLogicEmissionPrePolicyWithPolicyAlreadyExistsError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithPolicyAlreadyExistsError...");
		when(this.applicationConfigurationService.getDefaultProperty(eq("flow.royal2.enabled.all.products"),eq(Boolean.FALSE.toString()))).thenReturn(Boolean.TRUE.toString());

		Map<String, Object> policiesNumber = new HashMap<>();
		policiesNumber.put(RBVDProperties.FIELD_RESULT_NUMBER.getValue(), BigDecimal.ONE);

		when(pisdR012.executeGetASingleRow(RBVDProperties.QUERY_VALIDATE_IF_POLICY_EXISTS.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(policiesNumber);

		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_POLICY_ALREADY_EXISTS.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithNonExistentQuotation() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithNonExistentQuotation...");

		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(null);


		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_EMPTY_RESULT_QUOTATION_DATA.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWitContractInsertionError() throws IOException {

		Mockito.reset(pisdR012);
		loadingContextConfiguration();
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWitContractInsertionError...");

		requestBody.getHolder().getContactDetails().get(0).setId("C001985478569");
		requestBody.getHolder().getContactDetails().get(0).setContact(null);
		requestBody.getHolder().getContactDetails().get(1).setId("EMAIL001");
		requestBody.getHolder().getContactDetails().get(1).setContact(null);
		requestBody.setAap("13000013");

		when(applicationConfigurationService.getProperty("pisd.channel.contact.detail.aap")).thenReturn("13000013");
		when(applicationConfigurationService.getProperty("pisd.channel.glomo.aap")).thenReturn("13000013");
		when(applicationConfigurationService.getDefaultProperty(Mockito.eq("obtain.sale.channels.list"),Mockito.eq(StringUtils.EMPTY))).thenReturn("PC;TM");
		when(applicationConfigurationService.getDefaultProperty(Mockito.eq("obtain.aaps.config.contact.details"),Mockito.eq(StringUtils.EMPTY))).thenReturn("13000013;13000013");



		GetContactDetailsASO contactDetailsResponse = mockDTO.getContactDetailsResponse();

		when(rbvdr201.executeGetContactDetailsService(anyString())).thenReturn(contactDetailsResponse);

		OutputDTO firstOutput = new OutputDTO();
		firstOutput.setData("emhSTGcxRnM");
		when(ksmkr002.executeKSMKR002(anyList(), anyString(), anyString(), anyObject())).thenReturn(singletonList(firstOutput));

		when(pisdR012.executeInsertSingleRow(eq(PISDProperties.QUERY_INSERT_INSURANCE_CONTRACT.getValue()), Mockito.anyMap(),
				eq(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue()),
				eq(RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue()),
				eq(RBVDProperties.FIELD_CUSTOMER_ID.getValue()),
				eq(RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue()),
				eq(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue()),
				eq(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()))).thenReturn(0);


		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.INSERTION_ERROR_IN_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError...");
		when(pisdR012.executeMultipleInsertionOrUpdate(Mockito.eq(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue()), Mockito.any())).
				thenReturn(new int[0]);

		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.INSERTION_ERROR_IN_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithContractMovInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithContractMovInsertionError...");

		when(pisdR012.executeInsertSingleRow(Mockito.eq(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue()),Mockito.any())).
				thenReturn(0);
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.INSERTION_ERROR_IN_TABLE.getAdviceCode());
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

		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);


		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.INSERTION_ERROR_IN_TABLE.getAdviceCode());
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

		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.INSERTION_ERROR_IN_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithContractUpdateError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithContractUpdateError...");

		when(pisdR012.executeInsertSingleRow(Mockito.eq( "PISD.UPDATE_CONTRACT"), Mockito.any(),
				Mockito.eq(RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue()),
						Mockito.eq(RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue()),
								Mockito.eq(RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue()),
										Mockito.eq(RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue()))).thenReturn(0);



		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNotNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithReceiptsUpdateError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithReceiptsUpdateError...");

		when(pisdR012.executeMultipleInsertionOrUpdate("PISD.UPDATE_EXPIRATION_DATE_RECEIPTS", argumentsForMultipleInsertion)).
				thenReturn(null);
		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);
		when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt", "")).thenReturn("");

		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNotNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode());
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

		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.INSERTION_ERROR_IN_TABLE.getAdviceCode());
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

		ResponseLibrary<PolicyDTO> validation  = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNotNull(validation.getBody());
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
		ResponseLibrary<PolicyDTO> validation  = rbvdr211.executeEmissionPolicy(requestBody);


		assertNotNull(validation.getBody());
		ResponseLibrary<PolicyDTO> validation2  = rbvdr211.executeEmissionPolicy(requestBody);




		assertNotNull(validation2.getBody());
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

		requestBody.getHolder().setId("123123123");



		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);

		assertNull(validation.getBody());

	}


	@Test
	public void eexecuteBusinessLogicEmissionPrePolicySetOrganizationTestV2() {

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


		when(mapperHelper.createSaveRelatedContractsArguments(anyList())).thenReturn(argumentsForMultipleInsertion);

		customerList.getData().get(0).getIdentityDocuments().get(0).getDocumentType().setId("RUC");
		customerList.getData().get(0).getIdentityDocuments().get(0).setDocumentNumber("20999999991");

		requestBody.getHolder().setId("123123123");

		ResponseLibrary<PolicyDTO> validation = null;



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

		when(this.rbvdr201.executeGetListBusinesses(anyString(), eq(null))).thenReturn(businesses);
		when(this.mapperHelper.constructPerson(any(), any(),any())).thenReturn(new PersonaBO());
		when(applicationConfigurationService.getDefaultProperty(eq("products.modalities.only.first.receipt"), Mockito.anyString())).thenReturn("");



		validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNotNull(validation.getBody());
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

		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());

		assertNull(validation.getBody());

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
		when(this.mapperHelper.constructPerson(any(), any(),any())).thenReturn(new PersonaBO());
		when(applicationConfigurationService.getDefaultProperty("products.modalities.only.first.receipt", "")).thenReturn("");

		validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		assertNotNull(validation.getBody());

		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
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


		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
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

		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION_FREQUENCY_TYPE.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION_FREQUENCY_TYPE.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
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
		ResponseLibrary<PolicyDTO> validation = rbvdr211.executeEmissionPolicyNotLifeFlowNew(requestBody);
		LOGGER.info("RBVDR211Test - DATA ERROR ... {}",context.getAdviceList());
		assertNull(validation.getBody());
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDInternalErrors.ERROR_NOT_VALUE_QUOTATION.getAdviceCode());
	}

	@Test
	@Ignore
	public void executeBusinessLogicEmissionPrePolicyWithLifeEasyYesOK() throws  IOException{
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithLifeEasyYesOK...");
		AgregarTerceroBO responseAddParticipants = mockData.getAddParticipantsRimacResponse();
		EmisionBO responseEmission = mockData.getEmissionRimacResponseLife();

		requestBody.setProductId("840");
		requestBody.setSaleChannelId("PC");
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);
		when(rbvdr201.executeAddParticipantsService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseAddParticipants);
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseEmission);

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNotNull(validation);
	}


	@Test
	@Ignore
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

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);
		assertNotNull(validation);
	}

	@Test
	@Ignore
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

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNotNull(validation);

		when(pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", filters, RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue())).thenReturn(0);

		validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

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
	@Ignore
	public void executeBusinessLogicEmissionPrePolicyWithAddParticipantsResponseNull() throws  IOException{
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithAddParticipantsResponseNull...");
		EmisionBO responseEmission = mockData.getEmissionRimacResponseLife();

		requestBody.setProductId("840");
		requestBody.setSaleChannelId("PC");
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);
		when(rbvdr201.executeAddParticipantsService(anyObject(), anyString(), anyString(), anyString())).thenReturn(null);
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString(), anyString())).thenReturn(responseEmission);

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNull(validation);
	}

	@Test
	@Ignore
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithPolicyAlreadyExistsError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithPolicyAlreadyExistsError...");

		Map<String, Object> policiesNumber = new HashMap<>();
		policiesNumber.put(RBVDProperties.FIELD_RESULT_NUMBER.getValue(), BigDecimal.ONE);

		when(pisdR012.executeGetASingleRow(RBVDProperties.QUERY_VALIDATE_IF_POLICY_EXISTS.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(policiesNumber);

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNull(validation);
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDErrors.POLICY_ALREADY_EXISTS.getAdviceCode());
	}

	@Test
	@Ignore
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithParticipantsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithParticipantsInsertionError...");

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));
		when(roles.get(0)).thenReturn(firstRole);
		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);
		when(pisdR012.executeGetRolesByProductAndModality(BigDecimal.ONE, "01")).thenReturn(responseQueryRoles);
		when(mapperHelper.createSaveParticipantArguments(anyList())).thenReturn(argumentsForMultipleInsertion);
		when(pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSRNC_CTR_PARTICIPANT.getValue(), argumentsForMultipleInsertion)).
				thenReturn(new int[0]);

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNull(validation);
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_PARTICIPANT_TABLE.getAdviceCode());
	}

	@Test
	@Ignore
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithNonExistentQuotation() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithNonExistentQuotation...");

		when(pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(), argumentValidateIfPolicyExists)).
				thenReturn(null);

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNull(validation);
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDErrors.NON_EXISTENT_QUOTATION.getAdviceCode());
	}
	@Test
	@Ignore
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

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNull(validation);
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode());
	}

	@Test
	@Ignore
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithReceiptsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithReceiptsInsertionError...");

		when(pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(), argumentsForMultipleInsertion)).
				thenReturn(new int[0]);

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNull(validation);
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode());
	}

	@Test
	@Ignore
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithContractMovInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithContractMovInsertionError...");

		when(pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue(), new HashMap<>())).
				thenReturn(0);

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNull(validation);
		assertEquals(this.context.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_MOV_TABLE.getAdviceCode());
	}

	@Test
	@Ignore
	public void executeBusinessLogicEmissionPrePolicyLifeEasyYesWithError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyLifeEasyYesWithError...");

		requestBody.setProductId("840");
		requestBody.setSaleChannelId("PC");

		customerList.setData(new ArrayList<>());
		when(rbvdr201.executeGetCustomerInformation(anyString())).thenReturn(customerList);

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNull(validation);
	}

	@Test
	@Ignore
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

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNotNull(validation);
	}

	@Test
	@Ignore
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

		PolicyDTO validation = rbvdr211.executeEmissionPrePolicyLifeProductLegacy(requestBody);

		assertNotNull(validation);
	}

}