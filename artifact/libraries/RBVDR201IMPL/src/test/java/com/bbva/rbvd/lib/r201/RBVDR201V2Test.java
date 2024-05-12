package com.bbva.rbvd.lib.r201;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.elara.domain.transaction.request.TransactionRequest;
import com.bbva.elara.domain.transaction.request.body.CommonRequestBody;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.rbvd.dto.cicsconnection.icr3.ICR3Response;
import com.bbva.rbvd.dto.cicsconnection.utils.HostAdvice;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r602.RBVDR602;
import com.bbva.rbvd.mock.EntityMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR201-app.xml",
		"classpath:/META-INF/spring/RBVDR201-app-test.xml",
		"classpath:/META-INF/spring/RBVDR201-arc.xml",
		"classpath:/META-INF/spring/RBVDR201-arc-test.xml" })
public class RBVDR201V2Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR201V2Test.class);

	@Spy
	private Context context;

	@Resource(name = "rbvdR201")
	private RBVDR201 rbvdR201;

	@Resource(name = "rbvdR602")
	private RBVDR602 rbvdR602;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		context = new Context();
		ThreadContext.set(context);
		getObjectIntrospection();
       /**
        *  Contexto RequestHeader Application
       * */
		String channel = "PC";
		CommonRequestHeader header = new CommonRequestHeader();
		header.setHeaderParameter(RequestHeaderParamsName.CHANNELCODE,channel);
		CommonRequestBody bodyRequest = new CommonRequestBody();
		bodyRequest.setTransactionParameters(new ArrayList<>());
		TransactionRequest transactionRequest = new TransactionRequest();
		transactionRequest.setHeader(header);
		transactionRequest.setBody(bodyRequest);
		this.context.setTransactionRequest(transactionRequest);
	}

	private Object getObjectIntrospection() throws Exception{
		Object result = this.rbvdR201;
		if(this.rbvdR201 instanceof Advised){
			Advised advised = (Advised) this.rbvdR201;
			result = advised.getTargetSource().getTarget();
		}
		return result;
	}


	@Test
	public void insurancePaymentAndFormalizationReturnsExpectedResult() throws IOException {
		// Given
		PolicyASO policyASO =  EntityMock.getInstance().createMockPolicyASO();
		ICR3Response icr3Response = new ICR3Response();
		icr3Response.setIcmrys2(EntityMock.getInstance().buildFormatoICMRYS2());
		when(rbvdR602.executeFormalizationContractAndPayment(Mockito.anyObject())).thenReturn(icr3Response);

		// When
		ResponseLibrary<PolicyASO> result = rbvdR201.executeInsurancePaymentAndFormalization(policyASO);

		// Then
		assertNotNull(result);
		assertEquals(RBVDInternalConstants.Status.OK, result.getStatusProcess());
	}

	@Test
	public void insurancePaymentAndFormalizationReturnsErrorResult() {
		// Given
		PolicyASO policyASO = EntityMock.getInstance().createMockPolicyASO();


		ICR3Response icr3Response = new ICR3Response();
		icr3Response.setHostAdviceCode(Collections.singletonList(new HostAdvice("IC123123","Error")));
		when(rbvdR602.executeFormalizationContractAndPayment(Mockito.anyObject())).thenReturn(icr3Response);

		// When
		ResponseLibrary<PolicyASO> result = rbvdR201.executeInsurancePaymentAndFormalization(policyASO);

		// Then
		assertNotNull(result);
		assertEquals(RBVDInternalConstants.Status.EWR, result.getStatusProcess());
	}
	


}
