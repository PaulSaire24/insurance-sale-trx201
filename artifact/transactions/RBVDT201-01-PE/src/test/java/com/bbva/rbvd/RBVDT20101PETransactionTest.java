package com.bbva.rbvd;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.TransactionParameter;
import com.bbva.elara.domain.transaction.request.TransactionRequest;
import com.bbva.elara.domain.transaction.request.body.CommonRequestBody;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;

import com.bbva.elara.test.osgi.DummyBundleContext;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import com.bbva.rbvd.dto.insrncsale.mock.MockData;
import com.bbva.rbvd.dto.insrncsale.policy.*;

import com.bbva.rbvd.lib.r211.RBVDR211;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/elara-test.xml",
		"classpath:/META-INF/spring/RBVDT20101PETest.xml" })
public class RBVDT20101PETransactionTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT20101PETransactionTest.class);

	@Spy
	@Autowired
	private RBVDT20101PETransaction transaction;

	@Resource(name = "dummyBundleContext")
	private DummyBundleContext bundleContext;

	@Resource(name = "rbvdR211")
	private RBVDR211 rbvdr211;

	@Mock
	private CommonRequestHeader header;

	@Mock
	private TransactionRequest transactionRequest;

	private MockData mockData;

	@Before
	public void initializeClass() throws Exception {
		MockitoAnnotations.initMocks(this);

		this.transaction.start(bundleContext);
		this.transaction.setContext(new Context());

		CommonRequestBody commonRequestBody = new CommonRequestBody();
		commonRequestBody.setTransactionParameters(new ArrayList<>());

		this.transactionRequest.setBody(commonRequestBody);

		when(header.getHeaderParameter(RequestHeaderParamsName.REQUESTID)).thenReturn("traceId");
		when(header.getHeaderParameter(RequestHeaderParamsName.CHANNELCODE)).thenReturn("BI");
		when(header.getHeaderParameter(RequestHeaderParamsName.USERCODE)).thenReturn("user");

		this.transactionRequest.setHeader(header);
		this.transaction.getContext().setTransactionRequest(transactionRequest);

		this.addParameter("isDataTreatment", true);
		this.addParameter("hasAcceptedContract", true);
		this.addParameter("productId", "830");

		mockData = MockData.getInstance();

	}

	@Test
	public void execute() throws IOException {
		PolicyDTO simulateResponse = mockData.getCreateInsuranceRequestBody();
		simulateResponse.setOperationDate(new Date());

		when(rbvdr211.executeBusinessLogicEmissionPrePolicy(anyObject())).thenReturn(simulateResponse);

		this.transaction.getContext().getParameterList().forEach(
				(key, value) -> LOGGER.info("Key {} with value: {}", key, value)
		);

		this.transaction.execute();

		assertTrue(this.transaction.getAdviceList().isEmpty());
	}

	@Test
	public void testNotNull(){
		assertNotNull(this.transaction);
		this.transaction.execute();

		assertNotNull(this.transaction.getSeverity());
	}

	@Test
	public void testNull() {
		when(rbvdr211.executeBusinessLogicEmissionPrePolicy(anyObject())).thenReturn(null);
		this.transaction.execute();
		assertEquals(Severity.ENR.getValue(), this.transaction.getSeverity().getValue());
	}

	private void addParameter(final String parameter, final Object value) {
		final TransactionParameter tParameter = new TransactionParameter(parameter, value);
		transaction.getContext().getParameterList().put(parameter, tParameter);
	}

	@Test
	public void executeEasyLife() throws IOException {
		PolicyDTO simulateResponse = mockData.getCreateInsuranceRequestBody();
		this.addParameter("productId", "840");
		simulateResponse.setOperationDate(new Date());

		when(rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(anyObject())).thenReturn(simulateResponse);

		this.transaction.getContext().getParameterList().forEach(
				(key, value) -> LOGGER.info("Key {} with value: {}", key, value)
		);

		this.transaction.execute();

		assertTrue(this.transaction.getAdviceList().isEmpty());
	}

	@Test
	public void executeDynamicLife() throws IOException {
		PolicyDTO simulateResponse = mockData.getCreateInsuranceRequestBody();
		this.addParameter("productId", "841");
		simulateResponse.setOperationDate(new Date());

		when(rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(anyObject())).thenReturn(simulateResponse);

		this.transaction.getContext().getParameterList().forEach(
				(key, value) -> LOGGER.info("Key {} with value: {}", key, value)
		);

		this.transaction.execute();

		assertTrue(this.transaction.getAdviceList().isEmpty());
	}
	@Test
	public void executeLawLife() throws IOException {
		PolicyDTO simulateResponse = mockData.getCreateInsuranceRequestBody();
		this.addParameter("productId", "842");
		simulateResponse.setOperationDate(new Date());

		when(rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(anyObject())).thenReturn(simulateResponse);

		this.transaction.getContext().getParameterList().forEach(
				(key, value) -> LOGGER.info("Key {} with value: {}", key, value)
		);

		this.transaction.execute();

		assertTrue(this.transaction.getAdviceList().isEmpty());
	}

	@Test
	public void executeInvestmentLife() throws IOException {
		PolicyDTO simulateResponse = mockData.getCreateInsuranceRequestBody();
		this.addParameter("productId", "845");
		simulateResponse.setOperationDate(new Date());

		when(rbvdr211.executeBusinessLogicEmissionPrePolicyLifeProduct(anyObject())).thenReturn(simulateResponse);

		this.transaction.getContext().getParameterList().forEach(
				(key, value) -> LOGGER.info("Key {} with value: {}", key, value)
		);

		this.transaction.execute();

		assertTrue(this.transaction.getAdviceList().isEmpty());
	}

}
