package com.bbva.rbvd.lib.r211;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

	private RBVDR211Impl rbvdr211 = new RBVDR211Impl();

	private MockData mockData;

	private ApplicationConfigurationService applicationConfigurationService;
	private RBVDR201 rbvdr201;
	private PISDR012 pisdR012;
	private MapperHelper mapperHelper;

	private PolicyDTO requestBody;

	private Map<String, Object> responseQueryInsuranceProduct;

	private Map<String, Object> responseQueryProductModality;
	private List<Map<String, Object>> modalities;
	private Map<String, Object> modalityData;

	private Map<String, Object> responseQueryGetInsuranceCompanyQuotaId;

	private Map<String, Object> responseQueryRoles;
	private List<Map<String, Object>> roles;
	private Map<String, Object> firstRole;

	private PolicyASO asoResponse;

	@Before
	public void setUp() throws IOException {
		ThreadContext.set(new Context());

		mockData = MockData.getInstance();

		applicationConfigurationService = mock(ApplicationConfigurationService.class);
		rbvdr201 = mock(RBVDR201.class);
		pisdR012 = mock(PISDR012.class);
		mapperHelper = mock(MapperHelper.class);

		requestBody = mockData.getCreateInsuranceRequestBody();

		rbvdr211.setApplicationConfigurationService(applicationConfigurationService);
		rbvdr211.setRbvdR201(rbvdr201);
		rbvdr211.setPisdR012(pisdR012);
		rbvdr211.setMapperHelper(mapperHelper);

		responseQueryInsuranceProduct = mock(Map.class);
		when(responseQueryInsuranceProduct.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		responseQueryProductModality = mock(Map.class);
		modalities = mock(List.class);
		modalityData = mock(Map.class);

		responseQueryGetInsuranceCompanyQuotaId = mock(Map.class);

		responseQueryRoles = mock(Map.class);
		roles = mock(List.class);
		firstRole = mock(Map.class);

		asoResponse = mockData.getEmisionASOResponse();

		when(responseQueryGetInsuranceCompanyQuotaId.get(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue())).
				thenReturn("58676a43-ba16-45b3-b626-48b1eba0581b");
		when(pisdR012.executeRegisterAdditionalCompanyQuotaId(anyString())).thenReturn(responseQueryGetInsuranceCompanyQuotaId);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithErrorInsuranceProduct() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithErrorInsuranceProduct...");

		when(pisdR012.executeInsuranceProduct(anyMap())).thenReturn(null);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INCORRECT_PRODUCT_ID.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithErrorProductModality() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithErrorProductModality...");

		when(pisdR012.executeInsuranceProduct(anyMap())).thenReturn(responseQueryInsuranceProduct);

		when(responseQueryProductModality.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(new ArrayList<>());
		when(pisdR012.executeInsuranceProductModality(anyMap())).thenReturn(responseQueryProductModality);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INCORRECT_PLAN_ID.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWitContractInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWitContractInsertionError...");

		when(pisdR012.executeInsuranceProduct(anyMap())).thenReturn(responseQueryInsuranceProduct);

		when(modalityData.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue())).thenReturn(BigDecimal.valueOf(12));
		when(modalities.get(0)).thenReturn(modalityData);
		when(responseQueryProductModality.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(modalities);
		when(pisdR012.executeInsuranceProductModality(anyMap())).thenReturn(responseQueryProductModality);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(mapperHelper.buildInsuranceContract(anyObject(), anyObject(), any(), anyString())).thenReturn(new InsuranceContractDAO());

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(-1);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithReceiptsInsertionError...");

		when(pisdR012.executeInsuranceProduct(anyMap())).thenReturn(responseQueryInsuranceProduct);

		when(modalityData.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue())).thenReturn(BigDecimal.valueOf(12));
		when(modalities.get(0)).thenReturn(modalityData);
		when(responseQueryProductModality.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(modalities);
		when(pisdR012.executeInsuranceProductModality(anyMap())).thenReturn(responseQueryProductModality);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(mapperHelper.buildInsuranceContract(anyObject(), anyObject(), any(), anyString())).thenReturn(new InsuranceContractDAO());

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(-1);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyWithContractMovInsertionError() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyWithContractMovInsertionError...");

		when(pisdR012.executeInsuranceProduct(anyMap())).thenReturn(responseQueryInsuranceProduct);

		when(modalityData.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue())).thenReturn(BigDecimal.valueOf(12));
		when(modalities.get(0)).thenReturn(modalityData);
		when(responseQueryProductModality.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(modalities);
		when(pisdR012.executeInsuranceProductModality(anyMap())).thenReturn(responseQueryProductModality);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(mapperHelper.buildInsuranceContract(anyObject(), anyObject(), any(), anyString())).thenReturn(new InsuranceContractDAO());

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

		when(pisdR012.executeInsuranceProduct(anyMap())).thenReturn(responseQueryInsuranceProduct);

		when(modalityData.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue())).thenReturn(BigDecimal.valueOf(12));
		when(modalities.get(0)).thenReturn(modalityData);
		when(responseQueryProductModality.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(modalities);
		when(pisdR012.executeInsuranceProductModality(anyMap())).thenReturn(responseQueryProductModality);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(mapperHelper.buildInsuranceContract(anyObject(), anyObject(), any(), anyString())).thenReturn(new InsuranceContractDAO());

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveContractMove(anyMap())).thenReturn(1);

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		when(pisdR012.executeGetRolesByProductAndModality(any(), anyString())).thenReturn(responseQueryRoles);

		when(mapperHelper.buildIsrcContractParticipants(anyObject(), anyMap(), anyString())).thenReturn(Collections.singletonList(new IsrcContractParticipantDAO()));

		when(pisdR012.executeSaveParticipants(any())).thenReturn(new int[0]);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNull(validation);
		assertEquals(this.rbvdr211.getAdviceList().get(0).getCode(), RBVDErrors.INSERTION_ERROR_IN_PARTICIPANT_TABLE.getAdviceCode());
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicyOK() {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicyOK...");

		when(pisdR012.executeInsuranceProduct(anyMap())).thenReturn(responseQueryInsuranceProduct);

		when(modalityData.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue())).thenReturn(BigDecimal.valueOf(12));
		when(modalities.get(0)).thenReturn(modalityData);
		when(responseQueryProductModality.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(modalities);
		when(pisdR012.executeInsuranceProductModality(anyMap())).thenReturn(responseQueryProductModality);

		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(asoResponse);

		when(mapperHelper.buildInsuranceContract(anyObject(), anyObject(), any(), anyString())).thenReturn(new InsuranceContractDAO());

		when(pisdR012.executeSaveContract(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveFirstReceipt(anyMap())).thenReturn(1);

		when(pisdR012.executeSaveContractMove(anyMap())).thenReturn(1);

		when(firstRole.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())).thenReturn(BigDecimal.valueOf(1));

		when(roles.get(0)).thenReturn(firstRole);

		when(responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).thenReturn(roles);

		when(pisdR012.executeGetRolesByProductAndModality(any(), anyString())).thenReturn(responseQueryRoles);

		when(mapperHelper.buildIsrcContractParticipants(anyObject(), anyMap(), anyString())).thenReturn(Collections.singletonList(new IsrcContractParticipantDAO()));

		when(pisdR012.executeSaveParticipants(any())).thenReturn(new int[1]);

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNotNull(validation);
		assertNotNull(validation.getId());
		assertEquals(asoResponse.getData().getId(), validation.getId());
	}
	
}
