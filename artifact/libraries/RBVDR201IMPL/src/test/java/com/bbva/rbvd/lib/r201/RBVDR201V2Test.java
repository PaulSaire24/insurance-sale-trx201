package com.bbva.rbvd.lib.r201;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.elara.domain.transaction.request.TransactionRequest;
import com.bbva.elara.domain.transaction.request.body.CommonRequestBody;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.rbvd.dto.cicsconnection.icr2.ICR2Response;
import com.bbva.rbvd.dto.cicsconnection.utils.HostAdvice;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r609.RBVDR609;
import com.bbva.rbvd.mock.EntityMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.aop.framework.Advised;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR201-app.xml",
		"classpath:/META-INF/spring/RBVDR201-app-test.xml",
		"classpath:/META-INF/spring/RBVDR201-arc.xml",
		"classpath:/META-INF/spring/RBVDR201-arc-test.xml" })
public class RBVDR201V2Test {

	@Spy
	private Context context;

	@Resource(name = "rbvdR201")
	private RBVDR201 rbvdR201;

	@Resource(name = "rbvdR609")
	private RBVDR609 rbvdr609;

	@Resource(name = "applicationConfigurationService")
	private ApplicationConfigurationService applicationConfigurationService;

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
		when(this.applicationConfigurationService.getDefaultProperty(eq("enabled.mock.emission.cics"), eq(Boolean.FALSE.toString()))).thenReturn(Boolean.FALSE.toString());
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
		DataASO requestBody = new DataASO();
		ICR2Response icContract = new ICR2Response();
		icContract.setIcmrys2(EntityMock.getInstance().buildFormatoICMRYS2());
		when(rbvdr609.executeFormalizationContractInsurance(Mockito.anyObject())).thenReturn(icContract);

		// When
		ResponseLibrary<PolicyASO> result = rbvdR201.executeInsurancePaymentAndFormalization(requestBody,RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_COLLECT);

		// Then
		assertNotNull(result);
		assertEquals(RBVDInternalConstants.Status.OK, result.getStatusProcess());
	}

	@Test
	public void insurancePaymentAndFormalizationReturnsErrorResult() {
		// Given
		DataASO requestBody = new DataASO();


		ICR2Response icr3Response = new ICR2Response();
		icr3Response.setHostAdviceCode(Collections.singletonList(new HostAdvice("IC123123","Error")));
		when(rbvdr609.executeFormalizationContractInsurance(Mockito.anyObject())).thenReturn(icr3Response);

		// When
		ResponseLibrary<PolicyASO> result = rbvdR201.executeInsurancePaymentAndFormalization(requestBody,RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_COLLECT);

		// Then
		assertNotNull(result);
		assertEquals(RBVDInternalConstants.Status.EWR, result.getStatusProcess());
	}
	


}
