package com.bbva.rbvd.lib.r211;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.lib.r201.RBVDR201;
import com.bbva.rbvd.lib.r211.impl.RBVDR211Impl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
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
	private RBVDR201 rbvdr201;

	@Before
	public void setUp() {
		ThreadContext.set(new Context());

		mockData = MockData.getInstance();

		rbvdr201 = mock(RBVDR201.class);
		rbvdr211.setRbvdR201(rbvdr201);
	}

	@Test
	public void executeBusinessLogicEmissionPrePolicy_OK() throws IOException {
		LOGGER.info("RBVDR211Test - Executing executeBusinessLogicEmissionPrePolicy_OK...");
		PolicyASO responseASO = mockData.getEmisionASOResponse();
		when(rbvdr201.executePrePolicyEmissionASO(anyObject())).thenReturn(responseASO);
		EmisionBO responseRimac = mockData.getEmisionRimacResponse();
		when(rbvdr201.executePrePolicyEmissionService(anyObject(), anyString(), anyString())).thenReturn(responseRimac);
		PolicyDTO requestBody = mockData.getCreateInsuranceRequestBody();

		PolicyDTO validation = rbvdr211.executeBusinessLogicEmissionPrePolicy(requestBody);

		assertNotNull(validation);
		assertNotNull(validation.getId());
	}
	
}
